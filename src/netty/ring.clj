(ns netty.ring
  (:require [netty.util :refer [static-field-hash-map]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :refer [map-invert]])
  (:import [io.netty.channel ChannelHandlerContext]
           [io.netty.buffer ByteBufInputStream ByteBufOutputStream]
           [io.netty.handler.codec.http
            DefaultFullHttpRequest
            HttpHeaders
            HttpMethod
            HttpRequest
            HttpResponse
            HttpHeaders$Names]
           [clojure.core.protocols CollReduce]))

(defprotocol ResponseBody
  (response-body [body ^ChannelHandlerContext ctx ^HttpResponse response]))

(extend-protocol ResponseBody
  String
  (response-body [str ctx response]
    str)
  java.io.File
  (response-body [file ctx response]
    (response-body (slurp file) ctx response))
  java.io.InputStream
  (response-body [input-stream ctx response]
    (with-open [input (io/reader input-stream)]
      (response-body (line-seq input) ctx response)))
  clojure.lang.ISeq
  (response-body [body ctx response])
  CollReduce
  (response-body [body ctx response]))

(def methods (static-field-hash-map HttpMethod))

(def headers (static-field-hash-map HttpHeaders$Names))

(defn content-type
  [request]
  (-> (HttpHeaders/getHeader request (:content-type headers))
      (str/split #";")
      first
      str/trim
      str/lower-case))

(defn content-length
  [request]
  (if (pos? (HttpHeaders/getContentLength request))
    (HttpHeaders/getContentLength request)
    0))

(defn headers
  [request]
  (zipmap (map (comp str/lower-case key) (.getHeaders request))
          (map val (.getHeaders request))))

(defn request-map
  [^ChannelHandlerContext ctx ^DefaultFullHttpRequest req]
  {:body (ByteBufInputStream. (.content req))
   :uri (.getUri req)
   :query-string (second (str/split (.getUri req) #"\?"))
   :req-method (get (map-invert methods) (.getMethod req))
   :server-name (first (str/split (HttpHeaders/getHost req) #":"))
   :server-port (.getPort (.localAddress (.channel ctx)))
   :remote-addr (str (.remoteAddress (.channel ctx)))
   :scheme (keyword (HttpHeaders/getHeader req "X-Scheme" "http"))
   :content-type (content-type req)
   :content-length (content-length req)
   :character-encoding (HttpHeaders/getHeader req (:content-encoding headers))
   :headers (headers req)})

(defn response-map
  [^ChannelHandlerContext ctx ^HttpResponse response]
  {:status nil
   :headers nil
   :body nil})
