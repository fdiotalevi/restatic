(ns restatic.generator
  (:require [restatic.config :as config]
            [clostache.parser :as renderer]))

(defn- template
  [basedir name]
  (slurp (str basedir "/views/" name)))

(defn generate-index 
  [basedir output-dir]
  (do
    (spit (str basedir "/"  output-dir "/index.html") (renderer/render (template basedir "index.mustache") {}))
    (println "Generated index.html")))

