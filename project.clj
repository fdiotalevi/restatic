(defproject restatic "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [com.typesafe/config "1.0.2"]
                 [org.clojars.fdiotalevi/file-kit "0.2.0-SNAPSHOT"]
                 [enlive "1.1.4"]]
  :main restatic.core
  :profiles {:uberjar {:aot :all}})
