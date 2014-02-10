(ns netty.redis
  (:require [taoensso.carmine :as car]))

(def conn {:pool {} :spec {:host "127.0.0.1" :port 6379}})

(defmacro wcar [conn & body]
  `(car/wcar ~conn ~@body))
