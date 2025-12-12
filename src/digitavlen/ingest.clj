(ns digitavlen.ingest
  (:require [clojure.java.io :as io]
            [digitavlen.runtime :as runtime]
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

(defn create-repository-txes [repo]
  (let [repo (-> repo
                 (assoc :db/id (repo-identifier repo))
                 (assoc :repo/id (repo-identifier repo)))]
   (into [repo {:page/uri (str "/" (:repo/name repo))}]
         (unpack-cached repo))))

(defn create-tx [filename txes]
  (cond->> txes
    (= filename "repositories.edn")
    (mapcat create-repository-txes)))

(comment

  "repositories.edn"
  [{:repo/name "matnyttig"
    :repo/owner "mattilsynet"}]


  (def txes (create-tx "repositories.edn"
                       [{:repo/name "matnyttig"
                         :repo/owner "mattilsynet"}]))
  (count txes)

  )
