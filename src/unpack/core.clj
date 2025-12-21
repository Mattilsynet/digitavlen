(ns unpack.core
  (:require [babashka.fs :as fs]
            [unpack.git :as git]
            [unpack.parser :as parser]))

(defn unpack [repo repo-url]
  (fs/with-temp-dir [temp-dir]
    (println "[unpack.core] Git cloning" (:repo/display-name repo))
    (git/clone! repo-url temp-dir)
    (parser/->txes repo (git/get-git-commits! {:repo-path temp-dir}))))
