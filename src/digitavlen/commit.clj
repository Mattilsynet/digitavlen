(ns digitavlen.commit
  (:require [cljc.java-time.local-date-time :as ldt]
            [digitavlen.time :as time]))

(defn get-year-authored [commit]
  (time/->year (:commit/authored-date commit)))

(defn get-ym-authored [commit]
  (time/->ym (:commit/authored-date commit)))

(defn get-yw-authored [commit]
  (time/->yw (:commit/authored-date commit)))

(defn get-ld-authored [commit]
  (time/->ld (:commit/authored-date commit)))

(defn get-half-day-authored [commit]
  (let [ldt (ldt/parse (:commit/authored-date commit))
        ld (ldt/to-local-date ldt)
        hour (ldt/get-hour ldt)]
    [ld (if (< hour 12) :am :pm)]))
