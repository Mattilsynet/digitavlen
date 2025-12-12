(ns unpack.git-test
  (:require [clojure.test :refer [deftest is testing]]
            [unpack.git :as git]))

(deftest build-git-log-command-test
  (testing "builds command"
    (is (= (git/build-git-log-command "path/to/temp/dir/repository"
                                      false
                                      "%n" ;; newline
                                      ["%H" "%s" "%b"])
           ["git" "-c" "core.quotepath=false"
            "-C" "path/to/temp/dir/repository" "log"
            "--pretty=format:%H%n%s%n%b"])))

  (testing "and with numstats"
    (is (= (git/build-git-log-command "path/to/temp/dir/repository"
                                      true
                                      "%n" ;; newline
                                      ["%H" "%s" "%b"])
           ["git" "-c" "core.quotepath=false"
            "-C" "path/to/temp/dir/repository" "log" "--numstat"
            "--pretty=format:%H%n%s%n%b"]))))

(deftest partitionize-test
  (testing "partitions the lines from the git log output into vectors of commits"
    (is (= (reduce git/partitionize []
                   ["25311dbd25b4a20833a387e39c3bd9ca5e90baac"
                    "John Johnnyboy"
                    "john@boy.com"
                    "2025-12-10T16:07:11+01:00"
                    "John Johnnyboy"
                    "john@boy.com"
                    "2025-12-10T16:07:33+01:00"
                    "I did some stuff"
                    ""
                    "13\t11\tsrc/digitavlen/the_stuff.clj"
                    ""
                    "79882784aad50e318b7b8355c20feb64d0a18b77"
                    "John Johnnyboy"
                    "john@boy.com"
                    "2025-12-10T15:41:52+01:00"
                    "John Johnnyboy"
                    "john@boy.com"
                    "2025-12-10T16:07:33+01:00"
                    "Some stuff was done"
                    ""
                    "16\t1\tsrc/digitavlen/the_stuff.clj"
                    "1\t1\tsrc/digitavlen/other_stuff.clj"
                    ""])
           [["25311dbd25b4a20833a387e39c3bd9ca5e90baac"
             "John Johnnyboy"
             "john@boy.com"
             "2025-12-10T16:07:11+01:00"
             "John Johnnyboy"
             "john@boy.com"
             "2025-12-10T16:07:33+01:00"
             "I did some stuff"
             ""
             "13\t11\tsrc/digitavlen/the_stuff.clj"
             ""]
            ["79882784aad50e318b7b8355c20feb64d0a18b77"
             "John Johnnyboy"
             "john@boy.com"
             "2025-12-10T15:41:52+01:00"
             "John Johnnyboy"
             "john@boy.com"
             "2025-12-10T16:07:33+01:00"
             "Some stuff was done"
             ""
             "16\t1\tsrc/digitavlen/the_stuff.clj"
             "1\t1\tsrc/digitavlen/other_stuff.clj"
             ""]]))))

(deftest process-commit-test
  (testing "combines all body lines together into one"
    (is (= (git/process-commit ["79882784aad50e318b7b8355c20feb64d0a18b77"
                                "John Johnnyboy"
                                "john@boy.com"
                                "2025-12-10T15:41:52+01:00"
                                "John Johnnyboy"
                                "john@boy.com"
                                "2025-12-10T16:07:33+01:00"
                                "Some stuff was done"
                                "One line"
                                "Two line"
                                "Three line"
                                "Four line"])
           ["79882784aad50e318b7b8355c20feb64d0a18b77"
            "John Johnnyboy"
            "john@boy.com"
            "2025-12-10T15:41:52+01:00"
            "John Johnnyboy"
            "john@boy.com"
            "2025-12-10T16:07:33+01:00"
            "Some stuff was done"
            "One line\nTwo line\nThree line\nFour line"
            []])))

  (testing "combines all numstat lines into a vector"
    (is (= (git/process-commit ["79882784aad50e318b7b8355c20feb64d0a18b77"
                                "John Johnnyboy"
                                "john@boy.com"
                                "2025-12-10T15:41:52+01:00"
                                "John Johnnyboy"
                                "john@boy.com"
                                "2025-12-10T16:07:33+01:00"
                                "Some stuff was done"
                                ""
                                "16\t1\tsrc/digitavlen/the_stuff.clj"
                                "1\t1\tsrc/digitavlen/other_stuff.clj"
                                ""])
           ["79882784aad50e318b7b8355c20feb64d0a18b77"
            "John Johnnyboy"
            "john@boy.com"
            "2025-12-10T15:41:52+01:00"
            "John Johnnyboy"
            "john@boy.com"
            "2025-12-10T16:07:33+01:00"
            "Some stuff was done"
            ""
            [["16" "1" "src/digitavlen/the_stuff.clj"]
             ["1" "1" "src/digitavlen/other_stuff.clj"]]]))))
