(ns netty.core
  (:require [netty.redis :as redis :refer [conn wcar]]
            [netty.format :as fmt]
            [clojure.core.async
             :as a :refer [<! >! go chan put! take! go-loop]])
  (:import [java.util.concurrent ThreadFactory Executor Executors]
           [java.net InetSocketAddress InetAddress]
           [io.netty.channel.socket.nio NioServerSocketChannel]
           [io.netty.channel
            Channel
            ChannelHandler
            ChannelHandlerAdapter
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

(def channel-options
  {:allocator ChannelOption/ALLOCATOR
   :allow-half-closure ChannelOption/ALLOW_HALF_CLOSURE
   :auto-read ChannelOption/AUTO_READ
   :connect-timeout-ms ChannelOption/CONNECT_TIMEOUT_MILLIS
   :ip-multicast-addr ChannelOption/IP_MULTICAST_ADDR
   :ip-multicast-if ChannelOption/IP_MULTICAST_IF
   :ip-multicast-loop-disabled ChannelOption/IP_MULTICAST_LOOP_DISABLED
   :ip-multicast-ttl ChannelOption/IP_MULTICAST_TTL
   :ip-tos ChannelOption/IP_TOS
   :max-messages-per-read ChannelOption/MAX_MESSAGES_PER_READ
   :message-size-estimator ChannelOption/MESSAGE_SIZE_ESTIMATOR
   :rcvbuf-allocator ChannelOption/RCVBUF_ALLOCATOR
   :backlog ChannelOption/SO_BACKLOG
   :broadcast ChannelOption/SO_BROADCAST
   :keep-alive ChannelOption/SO_KEEPALIVE
   :linger ChannelOption/SO_LINGER
   :rcvbuf ChannelOption/SO_RCVBUF
   :reuse-addr ChannelOption/SO_REUSEADDR
   :sndbuf ChannelOption/SO_SNDBUF
   :timeout ChannelOption/SO_TIMEOUT
   :tcp-no-delay ChannelOption/TCP_NODELAY
   :write-buffer-high-water-mark ChannelOption/WRITE_BUFFER_HIGH_WATER_MARK
   :write-buffer-low-water-mark ChannelOption/WRITE_BUFFER_LOW_WATER_MARK
   :write-spin-count ChannelOption/WRITE_SPIN_COUNT})

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

(defn channel-handler
  [handlers]
  (proxy [ChannelHandlerAdapter] []
    (channelActive [ctx]
      ((:active handlers) ctx))
    (channelInactive [ctx]
      ((:inactive handlers) ctx))
    (channelRead [ctx msg]
      ((:read handlers) ctx msg))))

(defn downstream-traffic-handler
  [pipeline-name]
  )

(defmacro create-netty-pipeline
  [pipeline-name server? channel-group & stages]
  )

(defmacro pipe->
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
