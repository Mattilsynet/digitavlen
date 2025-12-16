(ns digitavlen.pages.repo
  (:require [digitavlen.aggregation :as aggregation]
            [mattilsynet.design :as mtds]
            [superstring.core :as str]))

(defn render [db page]
  (let [repo (:git/repo page)
        commits (aggregation/commits-in db repo)]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (let [dataene (aggregation/commits-per-month commits)]
       [:mtds-chart {:class (mtds/classes :card)
                     :style {:--mtdsc-chart-aspect "4 / 1"}}
        [:table
         [:thead
          [:tr
           [:th]
           (for [[måned _] dataene]
             [:th måned])]]
         [:tbody
          [:tr
           [:th "Commits per måned"]
           (for [[_ cnt] dataene]
             [:td cnt])]]]])]))
