(ns digitavlen.charts.author
  (:require [digitavlen.author :as author]
            [mattilsynet.design :as mtds]
            [superstring.core :as str]))

(defn collaborators [authors commits]
  (let [all-author-pairs authors
        data (author/collaborations commits)]
    [:div {:class (mtds/classes :prose :card)}
     [:h2 "Who collaborates with whom?"]
     [:mtds-chart {:class (mtds/classes :card)
                   :data-legend :false
                   :style {:--mtdsc-chart-aspect "4 / 1"}}
      [:table
       [:thead
        [:tr
         [:th]
         (for [[pair] data]
           [:th (author/pair->label pair)])]]
       [:tbody
        [:tr
         [:th "Who collaborates with whom?"]
         (for [[_ cnt] data]
           [:td cnt])]]]]

     [:h3 "Pairs that have not collabed yet:"]
     [:ul
      (for [pair (->> (remove (comp (set (keys data))) all-author-pairs)
                      (map #(->> %
                                 (sort-by :person/name)
                                 (map :person/name)
                                 (str/join " + ")))
                      set
                      sort)]
        [:li pair])]]))
