(ns digitavlen.aggregate
  (:require [datomic.api :as d]
            [digitavlen.commit :as commit]))

(defn entities [db entity-ids]
  (map #(d/entity db %) entity-ids))

(defn by-year
  "Filter by year"
  [year commit]
  (-> commit commit/get-year-authored #{year}))

(defn by-month
  "Filter by month"
  [ym commit]
  (-> commit commit/get-ym-authored #{ym}))

(defn by-week
  "Filter by week"
  [yw commit]
  (-> commit commit/get-yw-authored #{yw}))

(defn commits-in [db repo]
  (->> (d/q '[:find [?c ...]
              :in $ ?repo-id
              :where
              [?r :repo/id ?repo-id]
              [?c :git/repo ?r]
              [?c :commit/hash]]
            db (:repo/id repo))
       (entities db)))

(defn update-vvals [f coll]
  (mapv (fn [[k v]] [k (f v)]) coll))

(defn commits-per-month [commits]
  (->> commits
       (group-by commit/get-ym-authored)
       (sort-by first)
       (update-vvals count)))
