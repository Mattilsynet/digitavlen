(ns unpack.parser-test
  (:require [clojure.test :refer [deftest is testing]]
            [unpack.parser :as parser]))

(deftest ->file-test
  (testing "parses numstats to a file"
    (is (= (parser/->file ["11" "8" "some-file.clj"])
           {:file/name "some-file.clj"
            :lines/added 11
            :lines/removed 8})))

  (testing "even when its a binary file"
    (is (= (parser/->file ["-" "-" "some-file.clj"])
           {:file/name "some-file.clj"
            :file/binary? true}))))

(deftest extract-co-authors-test
  (testing "returns nil if no co-authors"
    (is (nil? (parser/extract-co-authors "Here be no authors of co"))))

  (testing "returns a list of co-authors"
    (is (= (parser/extract-co-authors
            "Co-authored-by: John Johnnyboy <john@boy.com>\nCo-authored-by: Ser Coolberg <ser.coolberg@testing.com>")
           [{:db/id "john@boy.com"
             :person/email "john@boy.com"
             :person/name "John Johnnyboy"}
            {:db/id "ser.coolberg@testing.com"
             :person/email "ser.coolberg@testing.com"
             :person/name "Ser Coolberg"}])))

  (testing "with lowercased emails"
    (is (= (parser/extract-co-authors
            "Co-authored-by: John Johnnyboy <John@Boy.com>")
           [{:db/id "john@boy.com"
             :person/email "john@boy.com"
             :person/name "John Johnnyboy"}]))))

(deftest without-co-authors-test
  (testing "Removes co-authors from the body"
    (is (= (parser/without-co-authors "This is a body\n\nCo-authored-by: John Johnnyboy <john@boy.com>\nCo-authored-by: Ser Coolberg <ser.coolberg@testing.com>")
           "This is a body"))))

(deftest ->txes-test
  (testing "parses a list of commits"
    (is (= (parser/->txes {:db/id "mattilsynet/digitavlen"
                           :repo/id "mattilsynet/digitavlen"
                           :repo/name "digitavlen"
                           :repo/owner "mattilsynet"}
             [["25311dbd25b4a20833a387e39c3bd9ca5e90baac"
               "John Johnnyboy"
               "john@boy.com"
               "2025-12-10T16:07:11+01:00"
               "John Johnnyboy"
               "john@boy.com"
               "2025-12-10T16:07:33+01:00"
               "Do some stuff"
               "And describe it\n\nCo-authored-by: Ser Coolberg <ser.coolberg@testing.com>"
               [["13" "11" "the-stuff.clj"]]]])
           [{:git/repo "mattilsynet/digitavlen"
             :commit/hash "25311dbd25b4a20833a387e39c3bd9ca5e90baac"
             :commit/message "Do some stuff"
             :commit/desc "And describe it"
             :commit/authored-date #inst "2025-12-10T16:07:11+01:00"
             :commit/committed-date #inst "2025-12-10T16:07:33+01:00"
             :commit/author {:db/id "john@boy.com"
                             :person/email "john@boy.com"
                             :person/name "John Johnnyboy"}
             :commit/committer "john@boy.com"
             :commit/co-authors [{:db/id "ser.coolberg@testing.com"
                                  :person/email "ser.coolberg@testing.com"
                                  :person/name "Ser Coolberg"}]
             :commit/filestats
             [{:db/id "454621952"
               :file/name "the-stuff.clj"
               :lines/added 13
               :lines/removed 11}]}]))))
