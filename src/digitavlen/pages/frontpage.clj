(ns digitavlen.pages.frontpage
  (:require [datomic.api :as d]
            [digitavlen.author :as author]
            [digitavlen.charts.author :as author-charts]
            [digitavlen.db :as db]
            [mattilsynet.design :as mtds]))

(defn render [db _]
  [:main {:class (mtds/classes :prose :group)}
   [:h1 "Digitavlen"]
   [:div {:class (mtds/classes :flex)}
    (for [repo (->> (d/q '[:find [?e ...]
                           :where
                           [?e :page/kind :page.kind/repo]]
                         db)
                    (db/entities db)
                    (sort-by :page/uri))]
      [:a {:href (:page/uri repo)}
       (:repo/display-name (:git/repo repo))])]

   (author-charts/collaborators (author/author-pairs (db/get-authors db))
                                (db/get-commits db))])
