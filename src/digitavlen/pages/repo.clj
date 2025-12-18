(ns digitavlen.pages.repo
  (:require [digitavlen.aggregate :as aggregate]
            [digitavlen.navigation :as navigation]
            [mattilsynet.design :as mtds]
            [superstring.core :as str]))

(defn render [db page]
  (let [repo (:git/repo page)
        commits (aggregate/commits-in db repo)]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [dataene (aggregate/commits-per-month commits)]
       [:mtds-chart {:class (mtds/classes :card)
                     :style {:--mtdsc-chart-aspect "4 / 1"}}
        [:table
         [:thead
          [:tr
           [:th]
           (for [[month _] dataene]
             [:th month])]]
         [:tbody
          [:tr
           [:th "Commits per måned"]
           (for [[_ cnt] dataene]
             [:td cnt])]]]])]))

(defn render-year [db page]
  (let [repo (:git/repo page)]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)]))

(defn render-month [db page]
  (let [repo (:git/repo page)]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)]))

(defn render-week [db page]
  (let [repo (:git/repo page)]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)]))
