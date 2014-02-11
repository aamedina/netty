(ns netty.udp
  (:require [netty.core :as netty]
            [netty.channel :as channel]
            [netty.format :as fmt]
            [clojure.core.async :as a :refer [go go-loop <! >! put! chan]])
  (:import [io.netty.handler.codec.serialization
            ClassResolvers
            ObjectEncoder
            ObjectDecoder]
           [io.netty.channel.socket.nio NioDatagramChannel]
           [io.netty.bootstrap Bootstrap]))

(defn udp-message-handler
  [channel {:keys [auto-encode?] :as options}]
  (let [encoder (fmt/encoder options)
        decoder (fmt/decoder options)
        port (chan 1)
        handler (channel/channel-handler port)]
    ))

(defn class-resolver
  ([] (class-resolver :cache-disabled))
  ([type]
     (let [class-loader (ClassLoader/getSystemClassLoader)]
       (case type
         :cache-disabled
         (ClassResolvers/cacheDisabled class-loader)
         :soft-caching
         (ClassResolvers/softCachingResolver class-loader)
         :soft-caching-concurrent
         (ClassResolvers/softCachingConcurrentResolver class-loader)
         :weak-caching
         (ClassResolvers/weakCachingResolver class-loader)
         :weak-caching-concurrent
         (ClassResolvers/weakCachingConcurrentResolver class-loader)))))

(defn create-udp-socket
  [socket-name pipeline-generator {:keys [buf-port size broadcast?]
                                   :or {port 0 buf-size 16384} :as options}]
  (let [bootstrap (Bootstrap.)]
    ))

(defn udp-socket
  ([] (udp-socket nil))
  ([options]
     (let [name (or (:name options) "udp-socket")]
       (create-udp-socket
        name
        (fn [_] (netty/create-netty-pipeline name false nil))
        (assoc options :auto-encode? true)))))

(defn udp-object-socket
  ([] (udp-object-socket nil))
  ([options]
     (let [name (or (:name options) "udp-socket")
           encoder (ObjectEncoder.)
           decoder (ObjectDecoder. (class-resolver :soft-caching-concurrent))]
       (create-udp-socket
        name
        (fn [_]
          (netty/create-netty-pipeline
           name false nil
           :encoder encoder
           :decoder decoder))
        (assoc options :auto-encode? false)))))
