(ns digitavlen.pages.repo
  (:require [superstring.core :as str]))

(defn render [db page]
  [:h1 (str/capitalize (:repo/name page))])
