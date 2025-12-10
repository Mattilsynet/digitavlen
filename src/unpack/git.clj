(ns unpack.git
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.string :as str]))

(def format-options {:hash "%H"
                     :author-name "%an"
                     :author-email "%ae"
                     :author-date "%aI"
                     :committer-name "%cn"
                     :committer-email "%ce"
                     :committer-date "%cI"
                     :subject "%s"
                     :body "%b"})

(def default-options
  {:repo-path "\"\""
   :separator "|=≈=|"
   :options format-options
   :with-numstat false})

(defn get-git-log [opts]
  (let [{:keys [repo-path separator options with-numstat]} (merge default-options opts)]
    (->> (keep identity ["git" "-c" "core.quotepath=false"
                         "-C" repo-path "log" (when with-numstat "--numstat")
                         (str "--pretty=format:" (str/join separator (vals options)))])
         (apply p/sh) :out)))

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

(defn get-git-commits [{:keys [repo-path]}]
  (let [log (get-git-log {:repo-path repo-path
                          :separator "%n"
                          :with-numstat true})]
    (->> (str/split log #"\n")
         (reduce partitionize [])
         (mapv process-commit))))

(defn clone [repo-url dir]
  (p/check (p/process "git" "clone" "--no-checkout" repo-url dir)))

(comment

  (def temp-dir (fs/create-temp-dir))
  (def repo-url "git@github.com:Mattilsynet/matnyttig.git")

  (p/shell "git" "clone" "--no-checkout" repo-url temp-dir)

  (fs/delete-tree temp-dir)

  )
