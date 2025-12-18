(ns digitavlen.pages.repo
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.year-month :as ym]
            [digitavlen.aggregate :as aggregate]
            [digitavlen.navigation :as navigation]
            [digitavlen.time :as time]
            [mattilsynet.design :as mtds]
            [superstring.core :as str]))

(defn render [db page]
  (let [repo (:git/repo page)
        commits (aggregate/commits-in db repo)]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (aggregate/commits-per-month commits)]
       [:mtds-chart {:class (mtds/classes :card)
                     :style {:--mtdsc-chart-aspect "4 / 1"}}
        [:table
         [:thead
          [:tr
           [:th]
           (for [[month _] data]
             [:th month])]]
         [:tbody
          [:tr
           [:th "Commits per month"]
           (for [[_ cnt] data]
             [:td cnt])]]]])]))

(defn render-year [db page]
  (let [repo (:git/repo page)
        commits (filter (partial aggregate/by-year (:param/year page))
                        (aggregate/commits-in db repo))]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (aggregate/commits-per-week commits)]
       [:mtds-chart {:class (mtds/classes :card)
                     :style {:--mtdsc-chart-aspect "4 / 1"}}
        [:table
         [:thead
          [:tr
           [:th]
           (for [[week _] data]
             [:th (second week)])]]
         [:tbody
          [:tr
           [:th "Commits per week"]
           (for [[_ cnt] data]
             [:td cnt])]]]])]))

(defn render-month [db page]
  (let [repo (:git/repo page)
        commits (filter (partial aggregate/by-month
                                 (ym/of (:param/year page) (:param/month page)))
                        (aggregate/commits-in db repo))]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (aggregate/commits-per-day commits)]
       [:mtds-chart {:class (mtds/classes :card)
                     :style {:--mtdsc-chart-aspect "4 / 1"}}
        [:table
         [:thead
          [:tr
           [:th]
           (for [[ld _] data]
             [:th (ld/get-day-of-month ld)])]]
         [:tbody
          [:tr
           [:th "Commits per day"]
           (for [[_ cnt] data]
             [:td cnt])]]]])]))

(defn render-week [db page]
  (let [repo (:git/repo page)
        commits (filter (partial aggregate/by-week
                                 [(:param/year page) (:param/week page)])
                        (aggregate/commits-in db repo))]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (->> (aggregate/commits-per-half-day commits)
                     (group-by ffirst))]
       [:mtds-chart {:class (mtds/classes :card)
                     :style {:--mtdsc-chart-aspect "4 / 1"}}
        [:table
         [:thead
          [:tr
           [:th]
           (for [[ld _] data]
             [:th (str (time/->month (ld/get-month-value ld)) " "
                       (ld/get-day-of-month ld))])]]
         [:tbody
          [:tr
           [:th "Commits per morning"]
           (for [[_ cnt] data]
             [:td (-> cnt first second)])]
          [:tr
           [:th "Commits per afternoon"]
           (for [[_ cnt] data]
             [:td (-> cnt second second)])]]]])]))

(comment

  (do
    (require '[powerpack.dev :as dev]
             '[datomic.api :as d])
    (def app (dev/get-app))
    (def db (d/db (:datomic/conn app))))

  (def week-11-commits (filter (partial aggregate/by-week [2025 11])
                               (aggregate/commits-in db {:repo/id "mattilsynet/matnyttig"})))

  (->> (aggregate/commits-per-half-day week-11-commits)
       (group-by ffirst))

  )
