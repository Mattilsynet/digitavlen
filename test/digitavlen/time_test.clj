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
