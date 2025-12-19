(ns digitavlen.aggregate
  (:require [digitavlen.commit :as commit]
            [digitavlen.utils :as utils]))

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
