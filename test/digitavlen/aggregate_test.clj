(ns digitavlen.aggregate-test
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.year-month :as ym]
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

(deftest commits-per-week-test
  (testing "aggregates the commits into count per week"
    (is (= (aggregate/commits-per-week [{:commit/authored-date "2025-12-14T12:00:00"}
                                        {:commit/authored-date "2025-12-13T12:00:00"}
                                        {:commit/authored-date "2025-12-10T12:00:00"}
                                        {:commit/authored-date "2025-12-07T12:00:00"}
                                        {:commit/authored-date "2025-12-05T12:00:00"}])
           [[[2025 49] 2]
            [[2025 50] 3]]))))

(deftest commits-per-day-test
  (testing "aggregates the commits into count per day"
    (is (= (aggregate/commits-per-day [{:commit/authored-date "2025-12-14T13:00:00"}
                                       {:commit/authored-date "2025-12-14T11:00:00"}
                                       {:commit/authored-date "2025-12-14T08:00:00"}
                                       {:commit/authored-date "2025-12-13T23:59:59"}
                                       {:commit/authored-date "2025-12-13T12:00:00"}])
           [[(ld/parse "2025-12-13") 3]
            [(ld/parse "2025-12-14") 3]]))))

(deftest commits-per-day-test
  (testing "aggregates the commits into count per half day"
    (is (= (aggregate/commits-per-half-day [{:commit/authored-date "2025-12-14T13:00:00"}
                                            {:commit/authored-date "2025-12-14T11:00:00"}
                                            {:commit/authored-date "2025-12-14T00:00:00"}
                                            {:commit/authored-date "2025-12-13T23:59:59"}
                                            {:commit/authored-date "2025-12-13T12:00:00"}])
           [[[(ld/parse "2025-12-13") :pm] 2]
            [[(ld/parse "2025-12-14") :am] 2]
            [[(ld/parse "2025-12-14") :pm] 1]]))))
