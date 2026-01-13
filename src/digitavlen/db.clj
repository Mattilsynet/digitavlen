(ns digitavlen.db
  (:require [datomic.api :as d]))

(defn entities [db entity-ids]
  (map #(d/entity db %) entity-ids))

(defn get-commits [db]
  (->> (d/q '[:find [?c ...]
              :where
              [?c :commit/hash]]
            db)
       (entities db)))

(defn commits-in [db repo]
  (->> (d/q '[:find [?c ...]
              :in $ ?repo-id
              :where
              [?r :repo/id ?repo-id]
              [?c :git/repo ?r]
              [?c :commit/hash]]
            db (:repo/id repo))
       (entities db)))

(defn get-authors [db]
  (->> (d/q '[:find [?p ...]
              :where [?p :person/email]]
            db)
       (entities db)))

(defn get-repo-authors [db repo]
  (->> (d/q '[:find [?author ...]
              :in $ ?repo-id
              :where
              [?r :repo/id ?repo-id]
              [?c :git/repo ?r]
              [?c :commit/author ?author]]
            db (:repo/id repo))
       (entities db)))
