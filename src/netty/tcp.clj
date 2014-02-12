(ns netty.tcp
  (:require [netty.core :as netty]
            [netty.format :as fmt]
            [clojure.core.async :as a :refer [go go-loop <! >! put! chan]]))

(defn wrap-tcp-channel
  [channel options]

  )

(defn start-tcp-server
  [handler options])

(defn tcp-client
  [options])
