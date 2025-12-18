(ns digitavlen.navigation
  (:require [cljc.java-time.year-month :as ym]
            [datomic.api :as d]
            [mattilsynet.design :as mtds]))

(def ->month
  {1 "Jan"
   2 "Feb"
   3 "Mar"
   4 "Apr"
   5 "May"
   6 "Jun"
   7 "Jul"
   8 "Aug"
   9 "Sep"
   10 "Oct"
   11 "Nov"
   12 "Dec"})

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
        [:label "Siden start"]
        [:a {:href (str "/" (-> page :git/repo :repo/name))}
         "Siden start"])
      (for [year years]
        (if (= (:param/year year)
               (:param/year page))
          [:label (:param/year year)]
          [:a {:href (:page/uri year)}
           (:param/year year)]))]

     [:div {:class (mtds/classes :flex)}
      (for [month months]
        (if (and (:param/month page)
                 (= (ym/of (:param/year page) (:param/month page))
                    (ym/of (:param/year month) (:param/month month))))
          [:label (->month (:param/month month))]
          [:a {:href (:page/uri month)}
           (->month (:param/month month))]))]

     [:div {:class (mtds/classes :flex)}
      (for [week weeks]
        (if (and (:param/week page)
                 (= [(:param/year page) (:param/week page)]
                    [(:param/year week) (:param/week week)]))
          [:label (:param/week week)]
          [:a {:href (:page/uri week)}
           (:param/week week)]))]]))
