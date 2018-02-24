(ns user
  (:require 
            [mount.core :as mount]
            [demo.figwheel :refer [start-fw stop-fw cljs]]
            [demo.core :refer [start-app]]))

(defn start []
  (mount/start-without #'demo.core/repl-server))

(defn stop []
  (mount/stop-except #'demo.core/repl-server))

(defn restart []
  (stop)
  (start))


