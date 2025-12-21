(ns unpack.git
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.string :as str]))

(def pretty-format
  ["%H"  ;; hash
   "%an" ;; author-name
   "%ae" ;; author-email
   "%aI" ;; author-date
   "%cn" ;; committer-name
   "%ce" ;; committer-email
   "%cI" ;; committer-date
   "%s"  ;; subject
   "%b"  ;; body
   ])

(def default-options
  {:repo-path "\"\""
   :separator "|=≈=|"
   :pretty-format pretty-format
   :with-numstat false})

(defn build-git-log-command [path numstat? separator pretty-format]
  (keep identity ["git" "-c" "core.quotepath=false"
                  "-C" path "log" (when numstat? "--numstat")
                  (str "--pretty=format:" (str/join separator pretty-format))]))

(comment
  (str/join " " (build-git-log-command "matnyttig" true "%n" pretty-format))

  )

(defn get-git-log! [opts]
  (let [{:keys [repo-path with-numstat separator pretty-format]} (merge default-options opts)]
    (->> (build-git-log-command repo-path with-numstat separator pretty-format)
         (apply p/sh)
         :out)))

(defn matches-git-hash [s]
  (re-matches #"^[a-f0-9]{40}.*" s))

(defn partitionize [acc line]
  (if (matches-git-hash line)
    (conj acc [line])
    (update acc (dec (count acc))
            conj line)))

(defn matches-numstats
  "Matches lines added / removed"
  [s]
  (re-matches #"^[\d-]+\t[\d-]+\t.*" s))

(defn process-commit [c]
  (let [[commit rest] (split-at 8 c)
        [body-lines numstat-lines] (split-with #(not (matches-numstats %)) rest)
        git-body (str/trim (str/join "\n" body-lines))
        numstats (mapv #(str/split % #"\t")
                       (remove empty? numstat-lines))]
    (conj (vec commit) git-body numstats)))

(defn get-git-commits! [{:keys [repo-path]}]
  (println "[unpack.git] Reading history...")
  (let [log (get-git-log! {:repo-path repo-path
                           :separator "%n"
                           :with-numstat true})]
    (->> (str/split log #"\n")
         (reduce partitionize [])
         (mapv process-commit))))

(defn clone! [repo-url dir]
  (p/check (p/process "git" "clone" "--no-checkout" repo-url dir)))

(comment

  (def temp-dir (fs/create-temp-dir))
  (def repo-url "git@github.com:Mattilsynet/matnyttig.git")

  (clone! repo-url temp-dir)

  (def log (get-git-log! {:repo-path temp-dir
                          :separator "%n"
                          :with-numstat true}))

  (def splitted (str/split log #"\n"))
  (def partitioned (reduce partitionize [] splitted))
  (def processed (mapv process-commit partitioned))


  (fs/delete-tree temp-dir)

  )
