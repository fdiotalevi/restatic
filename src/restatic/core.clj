(ns restatic.core
  (:gen-class)
  (:require [restatic.generator :as gen]
            [restatic.config :as config]
            [file-kit.core :as fk]
            [clojure.java.io :as io])
  (:import [com.typesafe.config ConfigFactory]
           [java.io File]))

(def ^:dynamic basedir)
(def ^:dynamic output-dir)
(def posts-path "contents/posts/")
(def pages-path "contents/pages/")

(defn generate-site []
  (let [posts-dir (io/file basedir posts-path)
        posts (fk/ls posts-dir)
        pages-dir (io/file basedir pages-path)
        pages (fk/ls pages-dir)] 
    (do
      (fk/rm-rf (io/file basedir output-dir))
      (fk/mkdir (io/file basedir output-dir))
      (fk/mkdir (io/file basedir output-dir "pages"))
      (gen/generate-index basedir output-dir)
      (gen/generate-posts basedir output-dir posts)
      (gen/generate-pages basedir output-dir pages)
      (fk/cp-r (io/file basedir "public") (io/file basedir output-dir)))))


(defn -main
  [& args]
  (let [base-directory (if (empty? args) "." (first args))
        conf-file (str base-directory "/site.conf")]
    (do
      (with-redefs [config/*config* (config/init conf-file)]
        (with-redefs [basedir base-directory
                      output-dir (config/get-string "output-dir")]
          (generate-site))))))
