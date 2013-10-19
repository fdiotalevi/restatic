(ns restatic.generator
  (:import [java.io File ByteArrayInputStream]
           [java.text SimpleDateFormat])
  (:require [restatic.config :as config]
            [clostache.parser :as renderer]
            [file-kit.core :as fk]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [restatic.config :as config]))

(defn- get-template
  [basedir name]
  (slurp (str basedir "/views/" name)))

(defn- render-template
  [basedir name data]
  (let [main-template (get-template basedir "main.mustache")
        inner-template (get-template basedir name)
        rendered-inner-template (renderer/render inner-template data)]
    (renderer/render main-template (assoc data :body rendered-inner-template))))

(defn- get-link
  "generate the link to a post starting from the file name"
  [file]
  (.. (.getName file) (replaceFirst "-" "/") (replaceFirst "-" "/") (replaceFirst "-" "/") (replaceFirst ".html" "/")))

(defn- folder-and-file-name 
  "maps a file name 2013-01-01-sometthing to 2013/01/01/something"
  [basedir output-dir file]
  (let [relPostFile (str (get-link file) "index.html")
        absPostFile (io/file basedir output-dir relPostFile)]
    (hash-map :folder (.getParent absPostFile) :file-name (.getName absPostFile))))

(defn generate-posts
  [basedir output-dir files pages]
  (do 
    (doseq [fileDef files]
      (let [postFile (folder-and-file-name basedir output-dir (:file fileDef))]
        (do
          (fk/mkdir-p (:folder postFile))
          (spit (io/file (:folder postFile) (:file-name postFile))
                (render-template basedir "post.mustache" {:post (fileDef :content) :pages pages :posts files})))))
    (println "generated" (count files) "posts")))

(defn generate-pages
  [basedir output-dir posts files]
  (doseq [fileDef files]
    (let [file-name  (.getName (fileDef :file))
          custom-template  (or (config/get-string (str "templates." file-name)) "page.mustache")
          dir-name (str basedir "/blog/pages/" (.replaceFirst file-name ".html" "/"))]
      (do
        (fk/mkdir-p dir-name)
        (spit (io/file dir-name "index.html")
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

(defn- format-date-for-feed
  [date-string]
  (try 
    (let [date-obj (.parse (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") date-string)
          rss-format (SimpleDateFormat. "EEE, dd MMM yyyy HH:mm:ss Z")]
      (.format rss-format date-obj))
    (catch Throwable t date-string)))

(defn extract-meta 
  [htmlcontent]
  (let [parsed-html (html/html-resource (ByteArrayInputStream. (.getBytes (slurp htmlcontent))))]
    (hash-map :title (content-of parsed-html [:.entry-title])
              :author-name (content-of parsed-html [:.author :.fn])
              :published (content-of parsed-html [:.published])
              :updated (content-of parsed-html [:.updated])
              :published-in-feed (format-date-for-feed (:datetime (:attrs (first (html/select parsed-html [:.published]))))))))

(defn read-articles
  [directory]
  (doall (map #(hash-map :file %
                         :link (get-link (io/file %))
                         :content (slurp %)
                         :meta (extract-meta %))
              (fk/ls directory))))


(defn generate-rss
  [basedir posts]
  (let [datamap {:feed {:feed_title (config/get-string "feed_title")
                        :feed_link (config/get-string "feed_link")
                        :feed_copyright (config/get-string "feed_copyright")
                        :items posts}}
        feedContent (renderer/render (slurp "src/templates/rss.mustache") datamap)]
    (spit
     (io/file basedir (config/get-string "output-dir") "rss.xml")
     feedContent)))
