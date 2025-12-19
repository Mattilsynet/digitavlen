(ns digitavlen.pages.repo
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.year-month :as ym]
            [digitavlen.aggregate :as aggregate]
            [digitavlen.navigation :as navigation]
            [digitavlen.time :as time]
            [digitavlen.utils :as utils]
            [mattilsynet.design :as mtds]
            [superstring.core :as str]))

(defn render [db page]
  (let [repo (:git/repo page)
        commits-per-month (->> (aggregate/commits-in db repo)
                               aggregate/commits-per-month)]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (utils/add-missing first
                  (fn [v] [v 0])
                  (time/get-months (ffirst commits-per-month)
                                   (first (last commits-per-month)))
                  commits-per-month)]
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
                        (aggregate/commits-in db repo))
        weeks-in-year (range 1 (inc (time/number-of-weeks-in-year (:param/year page))))]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (utils/add-missing (comp second first)
                  (fn [v] [[(:param/year page) v] 0])
                  weeks-in-year
                  (aggregate/commits-per-week commits))]
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
        current-month (ym/of (:param/year page) (:param/month page))
        commits (filter (partial aggregate/by-month current-month)
                        (aggregate/commits-in db repo))
        days-in-month (->> (inc (ym/length-of-month current-month))
                           (range 1)
                           (map #(ym/at-day current-month %)))]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (utils/add-missing first
                  (fn [v] [v 0])
                  days-in-month
                  (aggregate/commits-per-day commits))]
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
                        (aggregate/commits-in db repo))
        half-days-in-week (->> (time/lds-in-week (:param/year page) (:param/week page))
                               (mapcat (fn [ld]
                                         [[ld :am]
                                          [ld :pm]])))]
    [:main {:class (mtds/classes :prose :group)}
     [:h1 (str/capitalize (:repo/name repo))]

     (navigation/bar db page)

     (let [data (->> (utils/add-missing first
                       (fn [v] [v 0])
                       half-days-in-week
                       (aggregate/commits-per-half-day commits))
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
