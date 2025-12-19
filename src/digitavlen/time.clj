(ns digitavlen.time
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.temporal.iso-fields :as iso]
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

(defn number-of-weeks-in-year
  "Since week 1 must have at least 4 days of the new year,
   the 28th dec is guaranteed to be in the last week of
   the year."
  [year]
  (ld/get (ld/of year 12 28)
          iso/week-of-week-based-year))

(defn lds-in-week [year week]
  (let [monday (-> (ld/of year 1 31)
                   (ld/with (wf/week-of-year wf/iso) week)
                   (ld/with (wf/day-of-week wf/iso) 1))]
    (for [i (range 7)]
      (ld/plus-days monday i))))
