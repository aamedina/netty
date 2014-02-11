(ns netty.server
  (:require [clojure.core.async :as a :refer [go chan <! >! put! go-loop]]
            [netty.core :as netty])
  (:import [io.netty.channel
            Channel
            ChannelHandler
            ChannelHandlerAdapter
            ChannelOption]
           [io.netty.channel.group DefaultChannelGroup]
           [io.netty.bootstrap ServerBootstrap]
           [io.netty.channel.socket.nio NioServerSocketChannel]
           [io.netty.channel.nio NioEventLoopGroup]
           [java.net InetSocketAddress]))

(def server-options
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

(def default-server-options
  {:reuse-addr true
   :keep-alive true
   :connect-timeout-ms 100
   :tcp-no-delay true})

(defn set-options!
  [bootstrap options]
  (doseq [[opt val] options]
    (.option bootstrap (get server-options opt) val)))

(defn set-child-options!
  [bootstrap options]
  (doseq [[opt val] child-options]
    (.childOption bootstrap (get server-options opt) val)))

(defn bootstrap
  ([] (bootstrap 8080))
  ([port] (bootstrap port {}))
  ([port options] (bootstrap port options {}))
  ([port options child-options]
     (let [boss-group (NioEventLoopGroup.)
           worker-group (NioEventLoopGroup.)]
       (try
         (let [b (doto (ServerBootstrap.)
                   (.group boss-group worker-group)
                   (.channel NioServerSocketChannel)
                   (.localAddress (int port))
                   (set-options! options)
                   (set-child-options! child-options))
               f (.sync (.bind b))
               channel (.channel f)]
           (.sync (.closeFuture channel)))
         (finally
           (.shutdownGracefully boss-group)
           (.shutdownGracefully worker-group)
           (.sync (.terminationFuture boss-group))
           (.sync (.terminationFuture worker-group)))))))

(defn start-server
  [server-name pipeline-generator {:keys [port host] :as options}]
  (let [exec (netty/cached-thread-executor options)
        channel-group (DefaultChannelGroup. exec)]
    channel-group))
