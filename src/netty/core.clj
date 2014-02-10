(ns netty.core
  (:require [netty.udp :as udp]
            [netty.server :as server]
            [netty.client :as client]
            [netty.redis :as redis :refer [conn wcar]]))

(defn channel-remote-host-address
  [])

(defn channel-local-host-name
  [])

(defn channel-local-port
  [])

(defn wrap-netty-channel-future
  [])

(defn event-message
  [])

(defn wrap-network-channel
  [])

(defn set-channel-readable
  [])

(defn network-channel->netty-channel
  [])

(defn create-netty-pipeline
  [])

(defn current-options
  [])

(defn current-channel
  [])

(defn start-server
  [])

(defn server-message-handler
  [])

(defn create-client
  [])

(defn create-udp-socket
  [])