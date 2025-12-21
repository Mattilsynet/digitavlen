(ns unpack.core
  (:require [babashka.fs :as fs]
            [unpack.git :as git]
            [unpack.parser :as parser]))

(defmacro timed [atom k & body]
  `(let [start# (System/currentTimeMillis)
         result# (do ~@body)
         end# (System/currentTimeMillis)]
     (swap! ~atom assoc ~k [start# end#])
     result#))

(defn time-elapsed
  "Calculates time elapsed in seconds"
  [durations]
  (reduce-kv (fn [res k [start end]]
               (let [elapsed (/ (- end start) 1000.0)]
                 (-> (update res :total + elapsed)
                     (assoc k elapsed))))
             {:total 0}
             durations))

(defonce all-durations (atom {}))

(defn unpack [repo repo-url]
  (let [durations (atom {})]
    (fs/with-temp-dir [temp-dir]
      (println "[unpack.core] Git cloning" (:repo/display-name repo))
      (timed durations :clone (git/clone! repo-url temp-dir))
      (let [raw-commits (timed durations :read (git/get-git-commits! {:repo-path temp-dir}))
            parsed (timed durations :parse (parser/->txes repo raw-commits))]
        (swap! all-durations assoc (:repo/display-name repo) (time-elapsed @durations))
        (println "[unpack.core] Finished unpacking" (:repo/display-name repo))
        parsed))))

(comment

  ;; Durations in seconds
  (reset! all-durations {})
  @all-durations

  (->> (update-vals @all-durations #(select-keys % [:total]))
       (sort-by (comp :total val) >))

  (->> (update-vals @all-durations #(select-keys % [:clone]))
       (sort-by (comp :clone val) >))

  (->> (update-vals @all-durations #(select-keys % [:read]))
       (sort-by (comp :read val) >))

  (->> (update-vals @all-durations #(select-keys % [:parse]))
       (sort-by (comp :parse val) >))

  ;;

  (def durs (atom {}))
  @durs

  (timed durs :b (Thread/sleep 1000))

  (time-elapsed @durs)

  )
