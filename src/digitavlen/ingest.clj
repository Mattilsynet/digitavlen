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

(defn gen-repo-pages [repo commits]
  (reduce (fn [units c]
            (let [year (commit/get-year-authored c)
                  month (str/lower-case (ym/get-month (commit/get-ym-authored c)))
                  week (second (commit/get-yw-authored c))
                  base-path (str "/" (:repo/name repo) "/")]
              (conj units
                    {:page/uri (str base-path year)
                     :page/kind :page.kind.repo/year
                     :param/year year
                     :git/repo (:db/id repo)}
                    {:page/uri (str base-path year "/" month)
                     :page/kind :page.kind.repo/month
                     :param/year year
                     :param/month (ym/get-month-value (commit/get-ym-authored c))
                     :git/repo (:db/id repo)}
                    {:page/uri (str base-path year "/week-" week)
                     :page/kind :page.kind.repo/week
                     :param/year year
                     :param/week week
                     :git/repo (:db/id repo)})))
          #{}
          commits))

(defn create-repository-txes [repo]
  (let [repo (-> repo
                 (assoc :db/id (repo-identifier repo))
                 (assoc :repo/id (repo-identifier repo)))
        commit-txes (unpack-cached repo)]
    (concat [repo
             {:page/uri (str "/" (:repo/name repo))
              :page/kind :page.kind/repo
              :git/repo (:db/id repo)}]
            (gen-repo-pages repo commit-txes)
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
  (gen-repo-pages repo commit-txes)

  )
