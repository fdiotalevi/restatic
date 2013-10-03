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
    (do
      (let [content (slurp (str basedir "/contents/posts/" (first files)))]
        (spit (str basedir "/blog/" (first files)) (render-template basedir "post.mustache" {:post content})))
      (generate-posts basedir output-dir (rest files)))))

(defn generate-pages
  [basedir output-dir files]
  (when (not (empty? files))
    (do
      (let [content (slurp (str basedir "/contents/pages/" (first files)))]
        (spit (str basedir "/blog/pages/" (first files)) (render-template basedir "page.mustache" {:post content})))
      (generate-pages basedir output-dir (rest files)))))

(defn generate-index 
  [basedir output-dir]
  (do
    (spit (str basedir "/"  output-dir "/index.html") (render-template basedir "index.mustache" {}))
    (println "Generated index.html")))
