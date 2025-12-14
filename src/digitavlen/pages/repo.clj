(ns digitavlen.pages.repo
  (:require [clojure.string :as str]))

(defn render [db page]
  [:h1 (str/capitalize (:repo/name page))])
