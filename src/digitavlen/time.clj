(ns digitavlen.time
  (:require [cljc.java-time.instant :as instant]
            [cljc.java-time.local-date :as ld]
            [cljc.java-time.temporal.week-fields :as wf]
            [cljc.java-time.year-month :as ym]
            [cljc.java-time.zone-id :as zone-id]
            [cljc.java-time.zoned-date-time :as zdt]))

(defn ->ld [inst]
  (-> (.toInstant inst)
      (instant/at-zone (zone-id/of "Europe/Oslo"))
      zdt/to-local-date))

(defn ->year [inst]
  (ld/get-year (->ld inst)))

(defn ->ym [inst]
  (ym/from (->ld inst)))

(defn ->week [inst]
  (ld/get (->ld inst) (wf/week-of-year wf/iso)))

(defn ->yw [inst]
  [(->year inst)
   (->week inst)])
