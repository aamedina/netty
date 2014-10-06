(defproject netty "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha2"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]
                 [io.netty/netty-all "5.0.0.Alpha2-SNAPSHOT"]]
  :jvm-opts ^:replace ["-server" "-Dio.netty.leakDetectionLevel=advanced"])
