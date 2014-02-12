(ns netty.http
  (:require [netty.util :refer [static-field-hash-map]]
            [clojure.set :as set :refer [map-invert]])
  (:import [io.netty.handler.codec.http
            HttpMethod
            HttpRequest
            HttpResponse
            HttpHeaders
            HttpHeaders$Names]))

(def methods (static-field-hash-map HttpMethod))

(def headers (static-field-hash-map HttpHeaders$Names))

