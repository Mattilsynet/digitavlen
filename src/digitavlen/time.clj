(ns digitavlen.time
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.temporal.week-fields :as wf]
            [cljc.java-time.year-month :as ym]))

(defn ->ld [ldt]
  (ldt/to-local-date (cond-> ldt
                       (string? ldt)
                       ldt/parse)))

(defn ->year [ldt]
  (ld/get-year (->ld ldt)))

(defn ->ym [ldt]
  (ym/from (->ld ldt)))

(defn ->week [ldt]
  (ld/get (->ld ldt) (wf/week-of-year wf/iso)))

(defn ->yw [ldt]
  [(->year ldt)
   (->week ldt)])
