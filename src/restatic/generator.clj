(ns restatic.generator
  (:import [java.io File ByteArrayInputStream])
  (:require [restatic.config :as config]
            [clostache.parser :as renderer]
            [file-kit.core :as fk]
            [net.cgrand.enlive-html :as html]))

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
  (doseq [fileDef files]
    (spit (str basedir "/blog/" (.getName (fileDef :file)))
          (render-template basedir "post.mustache" {:post (fileDef :content)}))))

(defn generate-pages
  [basedir output-dir files]
  (doseq [fileDef files]
    (spit (str basedir "/blog/pages/" (.getName (fileDef :file)))
          (render-template basedir "page.mustache" {:post (fileDef :content)}))))

(defn generate-index 
  [basedir output-dir]
  (do
    (spit (str basedir "/"  output-dir "/index.html") (render-template basedir "index.mustache" {}))
    (println "Generated index.html")))

(defn extract-meta 
  [htmlcontent]
  (let [parsed-html (html/html-resource (ByteArrayInputStream. (.getBytes (slurp htmlcontent))))]
    (hash-map :title (:content (first (html/select parsed-html [:.entry-title]))))))

(defn read-articles
  [directory]
  (map #(hash-map :file %
                  :content (slurp %)
                  :meta (extract-meta %))
       (fk/ls directory)))

