(ns digitavlen.aggregation-test
  (:require [cljc.java-time.year-month :as ym]
            [clojure.test :refer [deftest is testing]]
            [digitavlen.aggregation :as aggregation]))

(deftest by-year-test
  (testing "matches the commit by year"
    (is (= (aggregation/by-year 2025 {:commit/authored-date "2025-12-14T12:00:00"})
           2025)))

  (testing "returns nil when not in year"
    (is (nil? (aggregation/by-year 2024 {:commit/authored-date "2025-12-14T12:00:00"})))))

(deftest by-month-test
  (testing "matches the commit by month"
    (is (= (aggregation/by-month (ym/parse "2025-12")
                                 {:commit/authored-date "2025-12-14T12:00:00"})
           (ym/parse "2025-12"))))

  (testing "returns nil when not in month"
    (is (nil? (aggregation/by-month (ym/parse "2025-11")
                                    {:commit/authored-date "2025-12-14T12:00:00"})))))

(deftest by-week-test
  (testing "matches the commit by week"
    (is (= (aggregation/by-week [2025 50]
                                {:commit/authored-date "2025-12-14T12:00:00"})
           [2025 50])))

  (testing "returns nil when not in week"
    (is (nil? (aggregation/by-week [2025 49]
                                   {:commit/authored-date "2025-12-14T12:00:00"})))))
