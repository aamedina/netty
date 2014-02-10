(ns netty.buffer
  (:import [io.netty.buffer ByteBuf Unpooled]
           [io.netty.util CharsetUtil]))

(def charset
  {:ascii CharsetUtil/US_ASCII
   :utf-8 CharsetUtil/UTF_8
   :utf-16 CharsetUtil/UTF_16})

(defn buffer
  [ctx bytes]
  (.buffer (.alloc ctx) bytes))

(defn unpooled
  [message]
  (Unpooled/copiedBuffer message (:utf-8 charset)))

