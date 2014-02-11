(ns netty.channel
  (:refer-clojure :exclude [read flush])
  (:require [clojure.core.async :as a :refer [go go-loop <! >! put! chan]])
  (:import [io.netty.channel
            ChannelHandlerAdapter
            ChannelHandlerContext
            ChannelInitializer]
           [io.netty.channel.socket SocketChannel]))

(defn channel-handler
  [port]
  (proxy [ChannelHandlerAdapter] []
    (channelActive [^ChannelHandlerContext ctx]
      (put! port {:event :channel-active
                  :ctx ctx
                  :channel (.channel ctx)})
      (proxy-super channelActive ctx))
    (channelInactive [^ChannelHandlerContext ctx]
      (put! port {:event :channel-inactive
                  :ctx ctx
                  :channel (.channel ctx)})
      (proxy-super channelInactive ctx))
    (channelRead [^ChannelHandlerContext ctx ^Object msg]
      (put! port {:event :channel-read
                  :ctx ctx
                  :msg msg
                  :channel (.channel ctx)})
      (proxy-super channelRead ctx))
    (exceptionCaught [^ChannelHandlerContext ctx ^Throwable cause]
      (put! port cause)
      (proxy-super exceptionCaught ctx))
    (handlerAdded [^ChannelHandlerContext ctx]
      (put! port {:event :handler-added
                  :ctx ctx
                  :channel (.channel ctx)})
      (proxy-super handledAdded ctx))
    (handlerRemoved [^ChannelHandlerContext ctx]
      (put! port {:event :handler-removed
                  :ctx ctx
                  :channel (.channel ctx)})
      (proxy-super handledRemoved ctx))
    (isSharable [] true)))

(defn channel-initializer
  [port]
  (proxy [ChannelInitializer] []
    (initChannel [^SocketChannel ch]
      (put! port {:event :init-channel :channel ch}))))
