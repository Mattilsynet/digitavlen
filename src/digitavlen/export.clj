(ns digitavlen.export
  (:require [digitavlen.core :as tavlen]
            [powerpack.export :as export]))

(defn ^:export export! [& args]
  (-> tavlen/config
      (assoc :site/base-url "https://digitavlen.mattilsynet.io")
      export/export!))
