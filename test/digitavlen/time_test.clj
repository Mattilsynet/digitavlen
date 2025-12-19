(ns digitavlen.time-test
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.year-month :as ym]
            [clojure.test :refer [deftest is testing]]
            [digitavlen.time :as time]))

(deftest ->ld-test
  (testing "parses datetime-str to local date"
    (is (= (time/->ld "2025-12-14T08:51:10")
           (ld/parse "2025-12-14"))))

  (testing "returns local date if not string"
    (is (= (time/->ld (ldt/parse "2025-12-14T08:51:10"))
           (ld/parse "2025-12-14")))))

(deftest ->year-test
  (testing "parses datetime-str to year"
    (is (= (time/->year "2025-12-14T08:51:10")
           2025))))

(deftest ->ym-test
  (testing "parses datetime-str to year-month"
    (is (= (time/->ym "2025-12-14T08:51:10")
           (ym/parse "2025-12")))))

(deftest ->week-test
  (testing "parses datetime-str to week"
    (is (= (time/->week "2025-12-14T08:51:10")
           50))))

(deftest ->yw-test
  (testing "parses datetime-str to year-week"
    (is (= (time/->yw "2025-12-14T08:51:10")
           [2025 50]))))

(deftest number-of-weeks-in-year-test
  (testing "gets the number of weeks in a year"
    (is (= (time/number-of-weeks-in-year 2025)
           52)))

  (testing "also for long years"
    (is (= (time/number-of-weeks-in-year 2020)
           53))))

(deftest get-months-between-test
  (testing "returns list of months between two year-months incl."
    (is (= (time/get-months (ym/parse "2023-03") (ym/parse "2025-11"))
           [(ym/parse "2023-03")
            (ym/parse "2023-04")
            (ym/parse "2023-05")
            (ym/parse "2023-06")
            (ym/parse "2023-07")
            (ym/parse "2023-08")
            (ym/parse "2023-09")
            (ym/parse "2023-10")
            (ym/parse "2023-11")
            (ym/parse "2023-12")
            (ym/parse "2024-01")
            (ym/parse "2024-02")
            (ym/parse "2024-03")
            (ym/parse "2024-04")
            (ym/parse "2024-05")
            (ym/parse "2024-06")
            (ym/parse "2024-07")
            (ym/parse "2024-08")
            (ym/parse "2024-09")
            (ym/parse "2024-10")
            (ym/parse "2024-11")
            (ym/parse "2024-12")
            (ym/parse "2025-01")
            (ym/parse "2025-02")
            (ym/parse "2025-03")
            (ym/parse "2025-04")
            (ym/parse "2025-05")
            (ym/parse "2025-06")
            (ym/parse "2025-07")
            (ym/parse "2025-08")
            (ym/parse "2025-09")
            (ym/parse "2025-10")
            (ym/parse "2025-11")]))))
