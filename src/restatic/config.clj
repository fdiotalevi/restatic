(ns restatic.config
  (:require [clojure.java.io :as io]))

(def ^:dynamic *config* (java.util.Properties.))

(defn init [filename]
  (.load *config* (java.io.FileInputStream. (io/file filename)))
  *config*)

(defn get-string [name]
  (try
    (.getProperty *config* name)
    (catch java.lang.Throwable t nil)))

