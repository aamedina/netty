(defproject netty "0.1.0-SNAPSHOT"
  :description "netty"
  :url "http://github.com/aamedina/netty"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories
  {"sonatype" "http://oss.sonatype.org/content/repositories/snapshots"}
  :source-paths ["src" "dev"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [io.netty/netty-all "5.0.0.Alpha2-SNAPSHOT"]
                 [com.taoensso/carmine "2.4.6"]])
