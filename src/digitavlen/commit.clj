(ns digitavlen.commit
  (:require [digitavlen.time :as time]))

(defn get-year-authored [commit]
  (time/->year (:commit/authored-date commit)))

(defn get-ym-authored [commit]
  (time/->ym (:commit/authored-date commit)))

(defn get-yw-authored [commit]
  (time/->yw (:commit/authored-date commit)))
