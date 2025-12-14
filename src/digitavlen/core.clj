(ns digitavlen.core
  (:require [digitavlen.ingest :as ingest]))

(defn html [body]
  [:html {:lang :no}
   [:body body]])

(defn render-page [context page]
  (case (:page/uri page)
    "/" (html "Yo")

    (html "404")))

(def config
  {:site/title "Digitavlen"
   :powerpack/render-page #'render-page
   :powerpack/create-ingest-tx #'ingest/create-tx
   :optimus/bundles {"styles.css" {:public-dir "public"
                                   :paths ["/mtds/styles.css"]}}})

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
