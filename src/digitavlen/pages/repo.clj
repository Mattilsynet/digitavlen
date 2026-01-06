(ns digitavlen.pages.repo
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.year-month :as ym]
            [digitavlen.aggregate :as aggregate]
            [digitavlen.db :as db]
            [digitavlen.navigation :as navigation]
            [digitavlen.time :as time]
            [digitavlen.utils :as utils]
            [mattilsynet.design :as mtds]))

(defn get-per-week-year-data [db year repo]
  (let [weeks-in-year (range 1 (inc (time/number-of-weeks-in-year year)))]
    (->> (db/commits-in db repo)
         (filter (partial aggregate/by-year year))
         aggregate/commits-per-week
         (utils/add-missing (comp second first)
           (fn [v] [[year v] 0])
           weeks-in-year))))

(defn ^{:indent 2} layout [db page & body]
  [:main {:class (mtds/classes :prose :group)}
   [:section {:class (mtds/classes :flex)
              :data-align :center}
    [:a {:href "/"} "< HOME"]
    [:h1 (-> page :git/repo :repo/display-name)]]

   (navigation/bar db page)

   body])

(defn render [db page]
  (let [commits-per-month (->> (db/commits-in db (:git/repo page))
                               aggregate/commits-per-month)]
    (layout db page
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
              [:td cnt])]]]]))))

(defn render-year [db page]
  (let [data (get-per-week-year-data db (:param/year page) (:git/repo page))]
    (layout db page
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
            [:td cnt])]]]])))

(defn render-month [db page]
  (let [current-month (ym/of (:param/year page) (:param/month page))
        commits (filter (partial aggregate/by-month current-month)
                        (db/commits-in db (:git/repo page)))
        days-in-month (->> (inc (ym/length-of-month current-month))
                           (range 1)
                           (map #(ym/at-day current-month %)))]
    (layout db page
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
              [:td cnt])]]]]))))

(defn render-week [db page]
  (let [commits (filter (partial aggregate/by-week
                                 [(:param/year page) (:param/week page)])
                        (db/commits-in db (:git/repo page)))
        half-days-in-week (->> (time/lds-in-week (:param/year page) (:param/week page))
                               (mapcat (fn [ld]
                                         [[ld :am]
                                          [ld :pm]])))]
    (layout db page
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
              [:td (-> cnt second second)])]]]]))))

(comment

  (do
    (require '[powerpack.dev :as dev]
             '[datomic.api :as d])
    (def app (dev/get-app))
    (def db (d/db (:datomic/conn app))))

  (def week-11-commits (filter (partial aggregate/by-week [2025 11])
                               (db/commits-in db {:repo/id "mattilsynet/matnyttig"})))

  (->> (aggregate/commits-per-half-day week-11-commits)
       (group-by ffirst))

  )
