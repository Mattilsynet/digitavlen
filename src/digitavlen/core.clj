(ns digitavlen.core)

(defn html [body]
  [:html {:lang :no}
   [:head
    [:link {:rel "stylesheet" :type "text/css" :href "/mtds/styles.css"}]]
   [:body body]])

(defn render-page [context page]
  (case (:page/uri page)
    "/" (html "Yo")

    (html "404")))

(def config
  {:site/title "Digitavlen"
   :powerpack/render-page #'render-page})
