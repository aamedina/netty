(ns netty.format
  (:import [io.netty.buffer ByteBuf]))

(defprotocol AsInputStream
  (as-input-stream [_]))

(defprotocol AsByteBuffer
  (as-byte-buf [_]))

(defprotocol AsChannelBuffer
  (as-channel-buffer [_]))

(extend-protocol AsInputStream)

(extend-protocol AsByteBuffer)

(extend-protocol AsChannelBuffer)
