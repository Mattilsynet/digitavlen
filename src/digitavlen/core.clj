(ns digitavlen.core
  (:require [digitavlen.ingest :as ingest]
            [digitavlen.pages.repo :as repo]))

(defn html [body]
  [:html {:lang :no}
   [:body body]])

(defn render-page [{:keys [:app/db]} page]
  (cond
    (:repo/id page)
    (html (repo/render db page))

    (= "/" (:page/uri page))
    (html [:h1 "Digitavlen"])

    :else
    (html "404")))

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
