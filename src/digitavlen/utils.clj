(ns digitavlen.utils)

(defn update-vvals [f coll]
  (mapv (fn [[k v]] [k (f v)]) coll))

(defn ^{:indent 1} add-missing
  ([k all coll]
   (add-missing k (fn [v] {k v}) all coll))
  ([keyfn missing-val-fn all-vals coll]
   (let [lookup (group-by keyfn coll)]
     (mapv #(or (first (lookup %))
                (missing-val-fn %))
           all-vals))))
