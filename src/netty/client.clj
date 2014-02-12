(ns netty.client
  (:require [netty.core :as netty]
            [netty.channel :as channel]
            [clojure.core.async :refer [<! >! put! go go-loop chan]])
  (:import [java.util.concurrent Executors ThreadFactory]
           [io.netty.channel ChannelHandlerAdapter]
           [io.netty.bootstrap Bootstrap]
           [io.netty.handler.ssl SslHandler SslHandshakeCompletionEvent]
           [io.netty.channel.socket.nio NioSocketChannel]
           [javax.net.ssl X509TrustManager SSLContext]
           [java.net InetSocketAddress]))

(defn uri-options
  [uri]
  (let [uri (if (instance? java.net.URI uri) uri (java.net.URI. uri))
        path (.getPath uri)]
    {:scheme (.getScheme uri)
     :server-name (.getHost uri)
     :server-port (.getPort uri)
     :uri (if (empty? path) "/" path)
     :user-info (.getUserInfo uri)
     :query-string (.getQuery uri)}))

(defn uri
  [{:keys [scheme user-info server-name server-port uri query-string]}]
  (java.net.URI. scheme user-info server-name server-port uri query-string ""))

(def default-client-options
  {:tcp-nodelay true
   :so-reuseaddr true
   :connect-timeout-millis 3000})

(def thread-factory
  (letfn [(run [runnable]
            (try
              (.run runnable)
              (catch Throwable e
                (println e str "error in I/O thread"))))]
    (reify ThreadFactory
      (^Thread newThread [_ ^Runnable runnable]
        (doto (Thread. (run runnable))
          (.setDaemon true))))))

(defn client-message-handler
  [channel options]
  )

(def naive-trust-manager
  (reify X509TrustManager
    (checkClientTrusted [_ _ _])
    (checkServerTrusted [_ _ _])
    (getAcceptedIssuers [_])))

(defn ssl-context
  [{:keys [ignore-ssl-certs?]}]
  (if ignore-ssl-certs?
    (doto (SSLContext/getInstance "TLS")
      (.init nil (into-array [naive-trust-manager]) nil))
    (SSLContext/getDefault)))

(defn ssl-engine
  [{:keys [name port] :as options}]
  (doto (.createSSLEngine (ssl-context options) name port)
    (.setUseClientMode true)))

(defn ssl-handler
  [options]
  (doto (SslHandler. (ssl-engine options))
    (.setIssueHandshake true)
    (.setCloseOnSSLException true)))

(defn bootstrap-client
  [client-name pipeline-generator options]
  (let [bootstrap (Bootstrap.)
        options (merge default-client-options options)]
    ))
