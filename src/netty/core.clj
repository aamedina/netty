(ns netty.core
  (:require [netty.server :as server]
            [netty.client :as client]
            [netty.redis :as redis :refer [conn wcar]]
            [clojure.core.async
             :as a :refer [<! >! go chan put! take! go-loop]])
  (:import [java.util.concurrent ThreadFactory Executor Executors]
           [java.net InetSocketAddress InetAddress]
           [io.netty.channel Channel ChannelHandler ChannelHandlerAdapter]))

(defprotocol NetworkAddress
  (port [_])
  (local-host [_])
  (remote-host [_]))

(extend-protocol NetworkAddress
  InetAddress
  (port [inet-address] nil)
  (local-host [inet-address]
    (.getHostName inet-address))
  (remote-host [inet-address]
    (.getHostAddress inet-address))
  InetSocketAddress
  (port [socket-address]
    (.getPort ^InetSocketAddress socket-address))
  (local-host [socket-address]
    (local-host (.getAddress ^InetSocketAddress socket-address)))
  (remote-host [socket-address]
    (remote-host (.getAddress ^InetSocketAddress socket-address)))
  Channel
  (port [channel]
    (port (.getLocalAddress channel)))
  (local-host [channel]
    (local-host (.getLocalAddress channel)))
  (remote-host [channel]
    (remote-host (.getRemoteAddress channel))))

(def local-options (ThreadLocal.))

(def local-channel (ThreadLocal.))

(defn current-options []
  (.get local-options))

(defn current-channel []
  (.get local-channel))

(defn cached-thread-executor [options]
  (Executors/newCachedThreadPool
   (reify ThreadFactory
     (newThread [_ r]
       (Thread. (fn [] (.set local-options options) (.run r)))))))

(defn close-channel! [netty-channel]
  (.close netty-channel))

(defn channel-remote-host-address
  [])

(defn channel-local-host-name
  [])

(defn channel-local-port
  [])

(defn wrap-netty-channel-future
  [])

(defn wrap-network-channel
  [netty-channel]
  (let [port (chan)]
    (go-loop []
      (when-let [in (<! port)]
        (println in)
        (recur)))
    port))

(defn network-channel->netty-channel
  [])

(defmacro create-netty-pipeline
  [pipeline-name server? channel-group & stages]
  )

(defn current-options
  [])

(defn current-channel
  [])

(defn start-server
  [])

(defn server-message-handler
  [])

(defn create-client
  [])

(defn connection-handler
  [pipeline-name channel-group server?]
  (proxy [ChannelHandlerAdapter] []
    (channelActive [ctx]
      (.add channel-group (.channel ctx)))
    (channelInactive [ctx]
      (.remove channel-group (.channel ctx)))
    (channelRead [ctx msg]
      )))
