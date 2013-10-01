(ns restatic.config
    (:import [com.typesafe.config ConfigFactory]))

(def ^:dynamic *config* (ConfigFactory/load))

(defn init [filename] (with-redefs [*config* (ConfigFactory/parseFile (java.io.File. filename))] *config*))

(defn get-string [name] (.getString *config* name))
