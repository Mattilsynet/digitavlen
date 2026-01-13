(ns digitavlen.author-test
  (:require [clojure.test :refer [deftest is testing]]
            [digitavlen.author :as author]))

(deftest ->initials-test
  (testing "Extracts initials from name"
    (is (= (author/->initials "Bruce Wayne")
           "BW")))

  (testing "even when lowercased"
    (is (= (author/->initials "username")
           "U"))))

(deftest author-pairs-test
  (testing "Makes pairs of all the authors"
    (is (= (author/author-pairs [{:person/name "Bruce Wayne"}
                                  {:person/name "Alfred"}
                                  {:person/name "Joker"}])
           [#{{:person/name "Bruce Wayne"} {:person/name "Joker"}}
            #{{:person/name "Alfred"} {:person/name "Bruce Wayne"}}
            #{{:person/name "Alfred"} {:person/name "Joker"}}]))))

(deftest collaborations-test
  (testing "Who collaborates with whom?"
    (is (= (author/collaborations [{:commit/author {:person/name "Bruce Wayne"}
                                    :commit/co-authors #{{:person/name "Alfred"}}}
                                   {:commit/author {:person/name "Alfred"}
                                    :commit/co-authors #{{:person/name "Bruce Wayne"}}}
                                   {:commit/author {:person/name "Bruce Wayne"}
                                    :commit/co-authors #{{:person/name "Joker"}}}])
           [[#{{:person/name "Alfred"} {:person/name "Bruce Wayne"}} 2]
            [#{{:person/name "Bruce Wayne"} {:person/name "Joker"}} 1]]))))
