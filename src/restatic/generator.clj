(ns restatic.generator
  (:require [restatic.config :as config]
            [clostache.parser :as renderer]))


(defn generate-index 
  [basedir output-dir]
  (spit (str basedir "/"  output-dir "/index.html") (renderer/render (slurp (str basedir "/views/" "index.mustache")) {})))

