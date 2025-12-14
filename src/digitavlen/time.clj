(ns digitavlen.time
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.temporal.week-fields :as wf]
            [cljc.java-time.year-month :as ym]))

(defn ->ld [datetime-str]
  (ldt/to-local-date (ldt/parse datetime-str)))

(defn ->year [datetime-str]
  (ld/get-year (->ld datetime-str)))

(defn ->ym [datetime-str]
  (ym/from (->ld datetime-str)))

(defn ->week [datetime-str]
  (ld/get (->ld datetime-str) (wf/week-of-year wf/iso)))

(defn ->yw [datetime-str]
  [(->year datetime-str)
   (->week datetime-str)])
