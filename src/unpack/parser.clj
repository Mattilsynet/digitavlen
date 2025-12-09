(ns unpack.parser
  (:require [clojure.instant :as inst]
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

(defn ->txes [commits]
  (let [people (atom {})
        pers-or-ref (fn [{:keys [:person/email :db/id] :as p}]
                      (if (contains? @people email)
                        id
                        (get (swap! people assoc email p) email)))]
    (map (fn [[hash
               author-name author-email author-date
               committer-name committer-email committer-date
               subject body numstats]]
           (let [author-email (str/lower-case author-email)]
             (->> {:commit/hash hash
                   :commit/subject subject
                   :commit/body (-> body without-co-authors)
                   :commit/authored-date (inst/read-instant-date author-date)
                   :commit/committed-date (inst/read-instant-date committer-date)
                   :commit/author (pers-or-ref (->person author-email author-name))
                   :commit/committer (pers-or-ref (->person committer-email committer-name))
                   :commit/co-authors (->> (extract-co-authors body)
                                           (map pers-or-ref)
                                           seq)
                   :commit/filestats
                   (mapv #(assoc % :db/id (->> (str hash (:file/name %))
                                               hash
                                               str))
                         (map ->file numstats))}
                  (remove (comp nil? val))
                  (into {}))))
         commits)))
