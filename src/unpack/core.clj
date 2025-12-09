(ns unpack.core
  (:require [babashka.fs :as fs]
            [unpack.git :as git]
            [unpack.parser :as parser]))

(defn unpack [repo-url]
  (fs/with-temp-dir [temp-dir]
    (git/clone repo-url temp-dir)
    (parser/->txes (git/get-git-commits {:repo-path temp-dir}))))
