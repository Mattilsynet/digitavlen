(ns digitavlen.aggregate-test
  (:require [cljc.java-time.year-month :as ym]
            [clojure.test :refer [deftest is testing]]
            [digitavlen.aggregate :as aggregate]))

(deftest by-year-test
  (testing "matches the commit by year"
    (is (= (aggregate/by-year 2025 {:commit/authored-date "2025-12-14T12:00:00"})
           2025)))

  (testing "returns nil when not in year"
    (is (nil? (aggregate/by-year 2024 {:commit/authored-date "2025-12-14T12:00:00"})))))

(deftest by-month-test
  (testing "matches the commit by month"
    (is (= (aggregate/by-month (ym/parse "2025-12")
                                 {:commit/authored-date "2025-12-14T12:00:00"})
           (ym/parse "2025-12"))))

  (testing "returns nil when not in month"
    (is (nil? (aggregate/by-month (ym/parse "2025-11")
                                    {:commit/authored-date "2025-12-14T12:00:00"})))))

(deftest by-week-test
  (testing "matches the commit by week"
    (is (= (aggregate/by-week [2025 50]
                                {:commit/authored-date "2025-12-14T12:00:00"})
           [2025 50])))

  (testing "returns nil when not in week"
    (is (nil? (aggregate/by-week [2025 49]
                                   {:commit/authored-date "2025-12-14T12:00:00"})))))

(deftest commits-per-month-test
  (testing "aggregates the commits into count per month"
    (is (= (aggregate/commits-per-month [{:commit/authored-date "2025-12-14T12:00:00"}
                                           {:commit/authored-date "2025-12-13T12:00:00"}
                                           {:commit/authored-date "2025-12-10T12:00:00"}
                                           {:commit/authored-date "2025-11-28T12:00:00"}
                                           {:commit/authored-date "2025-11-26T12:00:00"}])
           [[(ym/parse "2025-11") 2]
            [(ym/parse "2025-12") 3]]))))
