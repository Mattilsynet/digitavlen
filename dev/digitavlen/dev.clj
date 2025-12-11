(ns digitavlen.dev
  (:require [digitavlen.core :as tavlen]
            [digitavlen.runtime :as runtime]
            [powerpack.dev :as dev]))

(alter-var-root #'runtime/*dev?* (constantly true))

(defmethod dev/configure! :default []
  tavlen/config)

(comment ;; s-:

  (set! *print-namespace-maps* false)

  (dev/start)
  (dev/stop)
  (dev/reset)

  (dev/get-app)

  )
