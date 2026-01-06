(ns digitavlen.core
  (:require [digitavlen.frontpage :as frontpage]
            [digitavlen.ingest :as ingest]
            [digitavlen.pages.repo :as repo]))

(defn html [body]
  [:html {:lang :no}
   [:body body]])

(defn render-page [{:keys [:app/db]} page]
  (case (:page/kind page)
    :page.kind/frontpage (html (frontpage/render db page))
    :page.kind/repo (html (repo/render db page))
    :page.kind.repo/compare (html (repo/render-year-compare db page))
    :page.kind.repo/year (html (repo/render-year db page))
    :page.kind.repo/month (html (repo/render-month db page))
    :page.kind.repo/week (html (repo/render-week db page))
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
