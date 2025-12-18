(ns digitavlen.utils)

(defn update-vvals [f coll]
  (mapv (fn [[k v]] [k (f v)]) coll))
