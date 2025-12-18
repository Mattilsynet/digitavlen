(ns digitavlen.aggregate
  (:require [datomic.api :as d]
            [digitavlen.commit :as commit]
            [digitavlen.utils :as utils]))

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

(defn commits-per-month [commits]
  (->> commits
       (group-by commit/get-ym-authored)
       (sort-by first)
       (utils/update-vvals count)))

(defn commits-per-week [commits]
  (->> commits
       (group-by commit/get-yw-authored)
       (sort-by first)
       (utils/update-vvals count)))

(defn commits-per-day [commits]
  (->> commits
       (group-by commit/get-ld-authored)
       (sort-by first)
       (utils/update-vvals count)))

(defn commits-per-half-day [commits]
  (->> commits
       (group-by commit/get-half-day-authored)
       (sort-by first)
       (utils/update-vvals count)))
