(ns digitavlen.navigation
  (:require [datomic.api :as d]
            [digitavlen.time :as time]
            [digitavlen.utils :as utils]
            [mattilsynet.design :as mtds]))

(defn get-repo-pages [db page]
  (let [repo-pages (->> (d/q '[:find [?e ...]
                               :in $ ?repo-id
                               :where
                               [?e :param/year]
                               [?e :git/repo ?repo]
                               [?repo :repo/id ?repo-id]]
                             db (:repo/id (:git/repo page)))
                        (map #(d/entity db %)))]
    (cond-> {:years (->> repo-pages
                         (filter #(and (not (:param/month %))
                                       (not (:param/week %))))
                         (sort-by :param/year))}
      (:param/year page)
      (-> (assoc :months (->> repo-pages
                              (filter :param/month)
                              (filter (comp #{(:param/year page)} :param/year))
                              (sort-by :param/month)))
          (assoc :weeks (->> repo-pages
                             (filter :param/week)
                             (filter (comp #{(:param/year page)} :param/year))
                             (sort-by :param/week)))))))

(defn bar [db page]
  (let [{:keys [years months weeks]} (get-repo-pages db page)]
    [:section {:class (mtds/classes :prose)}
     [:div {:class (mtds/classes :flex)}
      (if (= :page.kind/repo (:page/kind page))
        [:label "All time"]
        [:a {:href (str "/" (-> page :git/repo :repo/name))}
         "All time"])
      (when (< 1 (count years))
        (if (= :page.kind.repo/compare (:page/kind page))
          [:label "Compare"]
          [:a {:href (str "/" (-> page :git/repo :repo/name) "/compare")}
           "Compare"]))
      (for [year years]
        (if (= (:param/year year)
               (:param/year page))
          [:label (:param/year year)]
          [:a {:href (:page/uri year)}
           (:param/year year)]))]

     (when months
       [:div {:class (mtds/classes :flex)}
        (when (= :page.kind.repo/month (:page/kind page))
          [:a {:href (str "/" (-> page :git/repo :repo/name) "/" (:param/year page))}
           "All year"])
        (for [month (utils/add-missing :param/month (range 1 13) months)]
          (cond
            (not (:param/year month))
            [:span (time/->month (:param/month month))]

            (= (:param/month page)
               (:param/month month))
            [:label (time/->month (:param/month month))]

            :else
            [:a {:href (:page/uri month)}
             (time/->month (:param/month month))]))])

     (when weeks
       [:div {:class (mtds/classes :flex)}
        (when (= :page.kind.repo/week (:page/kind page))
          [:a {:href (str "/" (-> page :git/repo :repo/name) "/" (:param/year page))}
           "All year"])
        (for [week (utils/add-missing :param/week
                     (range 1 (inc (time/number-of-weeks-in-year (:param/year page))))
                     weeks)]
          (cond
            (not (:param/year week))
            [:span (:param/week week)]

            (= (:param/week page)
               (:param/week week))
            [:label (:param/week week)]

            :else
            [:a {:href (:page/uri week)}
             (:param/week week)]))])]))
