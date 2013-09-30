(ns restatic.config
    (:import [com.typesafe.config ConfigFactory]))

(def ^:dynamic *config* (ConfigFactory/load))

(defn init [filename] (ConfigFactory/parseFile (java.io.File. filename)))

(defn get-string [name] (.getString *config* name))
