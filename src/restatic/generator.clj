(ns restatic.generator
  (:import [java.io File ByteArrayInputStream])
  (:require [restatic.config :as config]
            [clostache.parser :as renderer]
            [file-kit.core :as fk]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]))

(defn- get-template
  [basedir name]
  (slurp (str basedir "/views/" name)))

(defn- render-template
  [basedir name data]
  (let [main-template (get-template basedir "main.mustache")
        inner-template (get-template basedir name)
        rendered-inner-template (renderer/render inner-template data)]
    (renderer/render main-template (assoc data :body rendered-inner-template))))

(defn generate-posts
  [basedir output-dir files pages]
  (do 
    (doseq [fileDef files]
      (spit (str basedir "/blog/" (.getName (fileDef :file)))
            (render-template basedir "post.mustache" {:post (fileDef :content) :pages pages :posts files})))
    (println "generated" (count files) "posts")))

(defn generate-pages
  [basedir output-dir posts files]
  (doseq [fileDef files]
    (let [file-name  (.getName (fileDef :file))
          custom-template  (or (config/get-string (str "templates." file-name)) "page.mustache")]
      (do
        (spit (str basedir "/blog/pages/" file-name)
              (render-template basedir custom-template {:post (fileDef :content) :pages files :posts posts}))
        (println "generated" file-name "with template" custom-template)))))

(defn generate-index 
  [basedir output-dir posts pages]
  (do
   (spit (str basedir "/"  output-dir "/index.html")
          (render-template basedir "index.mustache" {:posts posts :firstposts (take 1 posts) :pages pages :config config/*config*}))
    (println "generated index.html")))

(defn- content-of 
  [parsed-html selector]
  (apply str (:content (first (html/select parsed-html selector)))))

(defn extract-meta 
  [htmlcontent]
  (let [parsed-html (html/html-resource (ByteArrayInputStream. (.getBytes (slurp htmlcontent))))]
    (hash-map :title (content-of parsed-html [:.entry-title])
              :author-name (content-of parsed-html [:.author :.fn])
              :published (content-of parsed-html [:.published])
              :updated (content-of parsed-html [:.updated]))))

(defn read-articles
  [directory]
  (doall (map #(hash-map :file %
                         :link (.getName %)
                         :content (slurp %)
                         :meta (extract-meta %))
              (fk/ls directory))))


(defn generate-rss
  [basedir posts]
  (let [feedContent (renderer/render (slurp "src/templates/rss.mustache") {:feed {:items posts}})]
    (spit  (io/file basedir (config/get-string "output-dir") "feed.xml") feedContent)))
