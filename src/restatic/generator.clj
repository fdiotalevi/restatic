(ns restatic.generator
  (:import [java.io File])
  (:require [restatic.config :as config]
            [clostache.parser :as renderer]))

(defn- template
  [basedir name]
  (slurp (str basedir "/views/" name)))

(defn create-posts
  [basedir output-dir files]
  (when (not (empty? files))
    (do
      (spit (str basedir "/blog/" (first files)) "somecontent")
      (create-posts basedir output-dir (rest files)))))


(defn generate-index 
  [basedir output-dir]
  (do
    (spit (str basedir "/"  output-dir "/index.html") (renderer/render (template basedir "index.mustache") {}))
    (println "Generated index.html")))

(defn generate-posts
  [basedir output-dir]  
  (let [x (str basedir "/contents/posts") files (seq (.list (File. x)))]
    (create-posts basedir output-dir files)))


