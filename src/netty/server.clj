(ns netty.server
  (:require [clojure.core.async :as a :refer [go chan <! >! put! go-loop]]
            [netty.core :as netty]
            [netty.channel :as channel])
  (:import [io.netty.channel
            Channel
            ChannelHandler
            ChannelHandlerAdapter
            ChannelHandlerContext
            ChannelInitializer
            ChannelOption]
           [io.netty.channel.group DefaultChannelGroup]
           [io.netty.bootstrap ServerBootstrap]
           [io.netty.channel.socket.nio NioServerSocketChannel]
           [io.netty.channel.nio NioEventLoopGroup]
           [io.netty.channel.socket SocketChannel]
           [java.net InetSocketAddress]))

(def default-server-options
  {:reuse-addr true
   :keep-alive true
   :tcp-no-delay true})

(def default-server-child-options
  {:reuse-addr true
   :keep-alive true
   :connect-timeout-ms 100
   :tcp-no-delay true})

(defn set-options!
  [bootstrap options]
  (doseq [[opt val] options]
    (.option bootstrap (get netty/channel-options opt) val)))

(defn set-child-options!
  [bootstrap child-options]
  (doseq [[opt val] child-options]
    (.childOption bootstrap (get netty/channel-options opt) val)))

(defn executor
  [type options]
  (case type
    :global (io.netty.util.concurrent.GlobalEventExecutor/INSTANCE)
    :immediate (io.netty.util.concurrent.ImmediateEventExecutor/INSTANCE)
    :default (io.netty.util.concurrent.DefaultEventExecutor.)
    :cached (netty/cached-thread-executor options)))

(defn bootstrap
  [handler & {:keys [port options child-options] :or {port 8080}}]
  (let [boss-group (NioEventLoopGroup.)
        worker-group (NioEventLoopGroup.)
        options (merge default-server-options options)
        child-options (merge default-server-child-options child-options)
        channel-group (DefaultChannelGroup. (executor :immediate options))]
    (try
      (let [b (doto (ServerBootstrap.)
                (.group boss-group worker-group)
                (.channel NioServerSocketChannel)
                (.localAddress (int port))
                (.childHandler handler)
                (set-options! options)
                (set-child-options! child-options))
            f (.sync (.bind b))
            channel (.channel f)]
        (.sync (.closeFuture channel)))
      (finally
        (.shutdownGracefully boss-group)
        (.shutdownGracefully worker-group)
        (.sync (.terminationFuture boss-group))
        (.sync (.terminationFuture worker-group))))))

(defn start-server
  [server-name pipeline-generator {:keys [port host] :as options}]
  (let [exec (netty/cached-thread-executor options)
        channel-group (DefaultChannelGroup. exec)]
    channel-group))
