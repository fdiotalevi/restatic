(ns restatic.generator
  (:import [java.io File])
  (:require [restatic.config :as config]
            [clostache.parser :as renderer]))

(defn- get-template
  [basedir name]
  (slurp (str basedir "/views/" name)))

(defn- render-template
  [basedir name data]
  (let [main-template (get-template basedir "main.mustache")
        inner-template (get-template basedir name)
        rendered-inner-template (renderer/render inner-template data)]
    (renderer/render main-template {:body rendered-inner-template})))

(defn generate-posts
  [basedir output-dir files]
  (when (not (empty? files))
    (doseq [file files]
      (spit (str basedir "/blog/" (.getName file)) (render-template basedir "post.mustache" {:post (slurp file)})))))

(defn generate-pages
  [basedir output-dir files]
  (doseq [file files]
    (spit (str basedir "/blog/pages/" (.getName file)) (render-template basedir "page.mustache" {:post (slurp file)}))))

(defn generate-index 
  [basedir output-dir]
  (do
    (spit (str basedir "/"  output-dir "/index.html") (render-template basedir "index.mustache" {}))
    (println "Generated index.html")))
