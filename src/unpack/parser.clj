(ns unpack.parser
  (:require [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.zoned-date-time :as zdt]
            [clojure.instant :as inst]
            [clojure.string :as str]))

(defn ->file [[added removed file]]
  (cond-> {:file/name file}

    (and (= "-" added) (= "-" removed))
    (assoc :file/binary? true)

    (and (some? (parse-long added)) (some? (parse-long removed)))
    (assoc :lines/added (Integer/parseInt added)
           :lines/removed (Integer/parseInt removed))))

(defn ->person [email name]
  {:db/id email
   :person/email email
   :person/name name})

(defn extract-co-authors [body]
  (when-let [co-authors (re-seq #"Co-authored-by:\s+([^<]+?)\s+<([^>]+)>" body)]
    (map #(->person (str/lower-case (nth % 2))
                    (nth % 1))
         co-authors)))

(defn without-co-authors [body]
  (first (str/split body #"(\n+)?Co-authored-by:")))

(defn ^{:indent 1} ->txes [repo commits]
  (let [people (atom {})
        pers-or-ref (fn [{:keys [:person/email :db/id] :as p}]
                      (if (contains? @people email)
                        id
                        (get (swap! people assoc email p) email)))]
    (map (fn [[commit-hash
               author-name author-email author-date
               committer-name committer-email committer-date
               subject body numstats]]
           (let [author-email (str/lower-case author-email)]
             (->> {:git/repo (:db/id repo)
                   :commit/hash commit-hash
                   :commit/message subject
                   :commit/desc (-> body without-co-authors)
                   :commit/authored-date (ldt/from (zdt/parse author-date))
                   :commit/committed-date (ldt/from (zdt/parse committer-date))
                   :commit/author (pers-or-ref (->person author-email author-name))
                   :commit/committer (pers-or-ref (->person committer-email committer-name))
                   :commit/co-authors (->> (extract-co-authors body)
                                           (map pers-or-ref)
                                           seq)
                   :commit/filestats
                   (->> (map ->file numstats)
                        (mapv #(assoc % :db/id (->> (str commit-hash (:file/name %))
                                                    hash
                                                    str))))}
                  (remove (comp nil? val))
                  (into {}))))
         commits)))
