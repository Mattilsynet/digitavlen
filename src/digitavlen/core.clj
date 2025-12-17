(ns digitavlen.core
  (:require [digitavlen.ingest :as ingest]
            [digitavlen.pages.repo :as repo]))

(defn html [body]
  [:html {:lang :no}
   [:body body]])

(defn render-page [{:keys [:app/db]} page]
  (case (:page/kind page)
    :page.kind/frontpage (html [:h1 "Digitavlen"])
    :page.kind/repo (html (repo/render db page))
    :page.kind.repo/year (html "Year overview")
    :page.kind.repo/month (html "Month overview")
    :page.kind.repo/week (html "Week overview")
    (html "Page not implemented")))

(def config
  {:site/title "Digitavlen"
   :powerpack/render-page #'render-page
   :powerpack/create-ingest-tx #'ingest/create-tx
   :optimus/bundles {"styles.css" {:public-dir "public"
                                   :paths ["/mtds/styles.css"]}
                     "app.js" {:public-dir "public"
                               :paths ["/mtds/index.iife.js"]}}})

(comment

  (do
    (require '[powerpack.dev :as dev]
             '[datomic.api :as d])
    (def app (dev/get-app))
    (def db (d/db (:datomic/conn app))))

  (->> (d/q '[:find [?e ...]
              :where
              [?e :page/uri]]
            db)
       (map #(d/entity db %))
       (map #(into {} %)))

  )
