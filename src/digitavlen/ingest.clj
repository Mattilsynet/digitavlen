(ns digitavlen.ingest
  (:require [cljc.java-time.year-month :as ym]
            [clojure.java.io :as io]
            [digitavlen.commit :as commit]
            [digitavlen.runtime :as runtime]
            [superstring.core :as str]
            [unpack.core :as unpack]))

(defn repo-identifier [repo]
  (str (:repo/owner repo) "/" (:repo/name repo)))

(defn github-url [repo]
  (str "https://github.com/" (repo-identifier repo)))

(defn github-ssh-url [repo]
  (str "git@github.com:" (repo-identifier repo) ".git"))

(defn ensure-file-path [file]
  (-> file .getParentFile .mkdirs))

(defn unpack-cached [repo]
  (let [repo-url (if runtime/*dev?*
                  (github-ssh-url repo)
                  (github-url repo))
        cached-data (some-> (io/resource (str "data/" (repo-identifier repo) ".edn"))
                            slurp
                            read-string)]
    (if cached-data
      cached-data
      (let [data (unpack/unpack repo repo-url)
            file (io/file (str "resources/data/" (repo-identifier repo) ".edn"))]
        (ensure-file-path file)
        (spit file (pr-str data))
        data))))

(defn get-units-of-time [repo commits]
  (reduce (fn [units c]
            (let [year (commit/get-year-authored c)
                  month (str/lower-case (ym/get-month (commit/get-ym-authored c)))
                  week (second (commit/get-yw-authored c))]
             (conj units
                   (str "/" (:repo/name repo) "/" year)
                   (str "/" (:repo/name repo) "/" year "/" month)
                   (str "/" (:repo/name repo) "/" year "/week-" week))))
          #{}
          commits))

(defn get-repo-pages [repo commits]
  (->> (get-units-of-time repo commits)
       (map (fn [path]
              {:page/uri path
               :git/repo (:db/id repo)}))))

(defn create-repository-txes [repo]
  (let [repo (-> repo
                 (assoc :db/id (repo-identifier repo))
                 (assoc :repo/id (repo-identifier repo)))
        commit-txes (unpack-cached repo)]
    (concat [repo
             {:page/uri (str "/" (:repo/name repo))
              :git/repo (:db/id repo)}]
            (get-repo-pages repo commit-txes)
            commit-txes)))

(defn create-tx [filename txes]
  (cond->> txes
    (= filename "repositories.edn")
    (mapcat create-repository-txes)))

(comment

  "repositories.edn"
  (def repo {:db/id "matnyttig/mattilsynet"
             :repo/id "matnyttig/mattilsynet"
             :repo/name "matnyttig"
             :repo/owner "mattilsynet"})


  (def txes (create-tx "repositories.edn" [repo]))
  (count txes)

  (def commit-txes (unpack-cached repo))
  (get-repo-pages repo commit-txes)

  )
