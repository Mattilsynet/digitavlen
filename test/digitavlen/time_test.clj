(ns digitavlen.time-test
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.year-month :as ym]
            [clojure.test :refer [deftest is testing]]
            [digitavlen.time :as time]))

(deftest ->ld-test
  (testing "parses inst to local date"
    (is (= (time/->ld #inst "2025-12-14T08:51:10.000-00:00")
           (ld/parse "2025-12-14")))))

(deftest ->year-test
  (testing "parses inst to year"
    (is (= (time/->year #inst "2025-12-14T08:51:10.000-00:00")
           2025))))

(deftest ->ym-test
  (testing "parses inst to year-month"
    (is (= (time/->ym #inst "2025-12-14T08:51:10.000-00:00")
           (ym/parse "2025-12")))))

(deftest ->week-test
  (testing "parses inst to week"
    (is (= (time/->week #inst "2025-12-14T08:51:10.000-00:00")
           50))))

(deftest ->yw-test
  (testing "parses inst to year-week"
    (is (= (time/->yw #inst "2025-12-14T08:51:10.000-00:00")
           [2025 50]))))
