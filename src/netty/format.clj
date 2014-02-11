(ns netty.format
  (:require [clojure.core.async :as a :refer [put! chan <! >! take!]])
  (:import [io.netty.buffer ByteBuf]
           [io.netty.channel ChannelFutureListener]
           [io.netty.channel.group ChannelGroup DefaultChannelGroup]
           [io.netty.util.concurrent Future]
           [java.util.concurrent Executor Executors ThreadFactory]
           [java.net
            URLDecoder URLEncoder
            SocketAddress InetSocketAddress InetAddress]))

(defprotocol AsInputStream
  (as-input-stream [_]))

(defprotocol AsByteBuffer
  (as-byte-buf [_]))

(defprotocol AsChannelBuffer
  (as-channel-buffer [_]))

(defprotocol ChannelCoercible
  (as-chan [_]))

(extend-protocol AsInputStream)

(extend-protocol AsByteBuffer)

(extend-protocol AsChannelBuffer)

(extend-protocol ChannelCoercible
  Future
  (as-chan [future]
    (let [port (chan 1)]
      (.addListener future (proxy [ChannelFutureListener] []
                             (operationComplete [__future]
                               (when (identical? future __future)
                                 (put! port __future)))))
      port)))
