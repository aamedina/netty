(ns netty.udp
  (:require [netty.core :as netty])
  (:import [io.netty.handler.codec.serialization ObjectEncoder ObjectDecoder]))

(defn create-udp-socket
  [socket-name pipeline-generator options]
  )

(defn udp-socket
  ([] (udp-socket nil))
  ([options]
     (let [name (or (:name options) "udp-socket")]
       (create-udp-socket
        name
        (fn [_] (netty/create-netty-pipeline name nil nil))
        (assoc options :auto-encode? true)))))
