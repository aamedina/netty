(ns netty.udp
  (:require [netty.core :as netty])
  (:import [io.netty.handler.codec.serialization
            ClassResolvers
            ObjectEncoder
            ObjectDecoder]))

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
  [socket-name pipeline-generator options]
  )

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
     (let [name (or (:name options) "udp-socket")]
       (create-udp-socket
        name
        (fn [_]
          (netty/create-netty-pipeline
           name false nil
           ;; :encoder (ObjectEncoder.)
           ;; :decoder (ObjectDecoder.)
           ))
        (assoc options :auto-encode? false)))))
