(ns netty.channel
  (:refer-clojure :exclude [read flush])
  (:import [io.netty.channel Channel ChannelHandler ChannelHandlerAdapter]))

(defprotocol IBindable
  (bind [_ ctx local-address promise]))

(defprotocol IConnectable
  (connect [_ ctx remote-address local-address promise])
  (disconnect [_ ctx remote-address local-address promise]))

(defprotocol IWriteable
  (write [_ ctx message promise])
  (flush [_ ctx])
  (write-and-flush [_ ctx message promise]))

(defprotocol IReadable
  (read [_ ctx]))

(defprotocol IChannelRead
  (channel-read [_ ctx]))

(defprotocol IChannelActive
  (channel-active [_ ctx])
  (channel-inactive [_ ctx]))

(defprotocol IChannelRegistered
  (channel-registered [_ ctx])
  (channel-unregistered [_ ctx]))

