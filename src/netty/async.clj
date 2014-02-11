(ns netty.async
  (:require [clojure.core.async :as a :refer [go <! >! put! go-loop chan]]))

(defn error
  [])

(defn <?
  [port & {:keys [on-success on-error]
           :or {on-success identity
                on-error #(throw %)}}]
  (a/map< (fn [ret]
            (if-not (instance? Throwable ret)
              (on-success ret)
              (on-error ret))) port))

(defprotocol Pipeline
  (redirect [pipeline value])
  (restart [pipeline])
  (complete [pipeline]))

(defn pipeline
  [f & {:keys [on-error finally value timeout implicit? unwrap? with-bindings?]
        :or {on-error #(throw %)
             finally (fn [] nil)
             timeout 0
             implicit? false
             unwrap? false
             with-bindings? false}}]
  (reify Pipeline
    (redirect [pipeline value]
      (pipeline value))
    (restart [pipeline])
    (complete [pipeline])
    clojure.lang.IFn
    (invoke [pipeline arg]
      (f arg))))
