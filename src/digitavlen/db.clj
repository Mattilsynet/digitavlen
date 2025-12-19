(ns digitavlen.db
  (:require [datomic.api :as d]))

(defn entities [db entity-ids]
  (map #(d/entity db %) entity-ids))

(defn commits-in [db repo]
  (->> (d/q '[:find [?c ...]
              :in $ ?repo-id
              :where
              [?r :repo/id ?repo-id]
              [?c :git/repo ?r]
              [?c :commit/hash]]
            db (:repo/id repo))
       (entities db)))
