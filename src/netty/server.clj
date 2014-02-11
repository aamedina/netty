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
   :no-delay ChannelOption/TCP_NODELAY
   :write-buffer-high-water-mark ChannelOption/WRITE_BUFFER_HIGH_WATER_MARK
   :write-buffer-low-water-mark ChannelOption/WRITE_BUFFER_LOW_WATER_MARK
   :write-spin-count ChannelOption/WRITE_SPIN_COUNT})
