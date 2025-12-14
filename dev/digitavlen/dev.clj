(ns digitavlen.dev
  (:require [digitavlen.core :as tavlen]
            [digitavlen.runtime :as runtime]
            [powerpack.dev :as dev]))

(alter-var-root #'runtime/*dev?* (constantly true))

(defmethod dev/configure! :default []
  tavlen/config)

(comment ;; s-:

  (set! *print-namespace-maps* false)

  (dev/start)
  (dev/stop)
  (dev/reset)

  (dev/get-app)

  )

(defn e->map [x]
  (cond
    (:db/id x) (update-vals (into {:db/id (:db/id x)} x) e->map)
    (map? x) (update-vals x e->map)
    (vector? x) (mapv e->map x)
    (set? x) (set (map e->map x))
    (coll? x) (map e->map x)
    :else x))

(intern 'clojure.core (with-meta 'e->map (meta #'e->map)) #'e->map)
