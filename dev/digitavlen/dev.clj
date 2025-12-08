(ns digitavlen.dev
  (:require [digitavlen.core :as tavlen]
            [powerpack.dev :as dev]))

(defmethod dev/configure! :default []
  tavlen/config)

(comment ;; s-:

  (dev/start)
  (dev/stop)
  (dev/reset)

  (dev/get-app)

  )
