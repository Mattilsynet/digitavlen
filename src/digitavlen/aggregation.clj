(ns digitavlen.aggregation
  (:require [digitavlen.commit :as commit]))

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
