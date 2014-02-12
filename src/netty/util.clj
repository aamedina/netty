(ns netty.util
  (:require [clojure.reflect :refer [reflect]]
            [clojure.string :as str]))

(defn resolve-static-fields
  [class]
  (->> (:members (reflect class))
       (map (juxt :name :flags))
       (filter (fn [[method-or-field flags]]
                 (and (contains? flags :static)
                      (contains? flags :public))))
       (map first)))

(defn resolve-static-field
  [class static-field]
  (try (eval `(. ~class ~(symbol (str "-" static-field))))
       (catch Throwable _)))

(defn keywordize-field
  [field]
  (-> (name field) (.toLowerCase) (str/escape {\_ \-}) keyword))

(defn static-field-hash-map
  [class]
  (let [static-fields (resolve-static-fields class)
        static-keys (map keywordize-field static-fields)
        static-hash-map (zipmap static-keys static-fields)]
    (reduce (fn [m [k v]]
              (if-let [static-field (resolve-static-field class v)]
                (assoc m k static-field)
                m))
            static-hash-map
            static-hash-map)))
