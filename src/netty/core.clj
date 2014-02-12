(ns netty.core
  (:require [netty.redis :as redis :refer [conn wcar]]
            [netty.format :as fmt]
            [clojure.core.async
             :as a :refer [<! >! go chan put! take! go-loop]]
            [netty.util :refer [static-field-hash-map]])
  (:import [java.util.concurrent ThreadFactory Executor Executors]
           [java.net InetSocketAddress InetAddress]
           [io.netty.channel.socket.nio NioServerSocketChannel]
           [io.netty.channel
            Channel
            ChannelHandler
            ChannelHandlerAdapter
            ChannelHandlerContext
            ChannelInitializer
            ChannelOption
            ChannelPipeline]))

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
    (.getPort socket-address))
  (local-host [socket-address]
    (local-host ^InetAddress (.getAddress socket-address)))
  (remote-host [socket-address]
    (remote-host ^InetAddress (.getAddress socket-address)))

  Channel
  (port [channel]
    (port ^InetSocketAddress (.localAddress channel)))
  (local-host [channel]
    (local-host ^InetSocketAddress (.localAddress channel)))
  (remote-host [channel]
    (remote-host ^InetSocketAddress (.remoteAddress channel)))

  ChannelHandlerContext
  (port [ctx]
    (port (.channel ctx)))
  (local-host [ctx]
    (local-host (.channel ctx)))
  (remote-host [ctx]
    (remote-host (.channel ctx))))

(def channel-options (static-field-hash-map ChannelOption))

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

(defn wrap-network-channel
  [netty-channel]
  (let [port (chan)]
    (go-loop []
      (when-let [in (<! port)]
        (println in)
        (recur)))
    port))

(defn channel-handler
  [handlers]
  (proxy [ChannelHandlerAdapter] []
    (channelActive [ctx]
      ((:active handlers) ctx))
    (channelInactive [ctx]
      ((:inactive handlers) ctx))
    (channelRead [ctx msg]
      ((:read handlers) ctx msg))))

(defmacro pipeline->
  [id channel-group & stages]
  (let [channel (gensym "channel")
        pipeline (gensym "pipeline")]
    `(let [~channel (.find ~channel-group ~id)
           ~pipeline (.pipeline ~channel)]
       (-> ~pipeline
           ~@stages))))

(defn connection-handler
  [pipeline-name channel-group server?]
  (proxy [ChannelHandlerAdapter] []
    (channelActive [ctx]
      (.add channel-group (.channel ctx)))
    (channelInactive [ctx]
      (.remove channel-group (.channel ctx)))
    (channelRead [ctx msg])))

(defn create-initializer
  [channel-group pipeline-generator]
  (proxy [ChannelInitializer] []
    (initChannel [ch]
      (pipeline-generator channel-group))))

(defn proxy-handler
  [handler]
  (proxy [ChannelHandler] []
    ))
