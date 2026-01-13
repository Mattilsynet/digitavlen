(ns digitavlen.author
  (:require [superstring.core :as str]))

(defn ->initials [name]
  (->> (str/split name #" ")
       (map first)
       str/join
       str/upper-case))

(defn author-pairs [authors]
  (for [a authors
        b authors
        :when (< (compare (:person/name a) (:person/name b)) 0)]
    #{a b}))

(defn pair->label [pair]
  (->> pair
       (sort-by :person/name)
       (map (comp ->initials :person/name))
       (str/join "+")))

(defn inc-collaborations [collaborations commit]
  (let [pairs (author-pairs (conj (:commit/co-authors commit)
                                  (:commit/author commit)))]
    (reduce (fn [collabs pair]
              (update collabs pair (fnil inc 0)))
            collaborations
            pairs)))

(defn collaborations [commits]
  (->> commits
       (filter :commit/co-authors)
       (reduce inc-collaborations {})
       (sort-by val >)))

(comment

  (do
    (require '[datomic.api :as d]
             '[digitavlen.db :as db]
             '[powerpack.dev])
    (def db (d/db (:datomic/conn (powerpack.dev/get-app)))))

  (->> (db/get-commits db)
       (collaborations))

  (->> (db/get-authors db)
       (map (juxt :person/name :person/email))
       sort)

  )
