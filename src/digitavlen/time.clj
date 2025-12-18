(ns digitavlen.time
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.temporal.week-fields :as wf]
            [cljc.java-time.year-month :as ym]))

(def ->month
  {1 "Jan"
   2 "Feb"
   3 "Mar"
   4 "Apr"
   5 "May"
   6 "Jun"
   7 "Jul"
   8 "Aug"
   9 "Sep"
   10 "Oct"
   11 "Nov"
   12 "Dec"})

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
