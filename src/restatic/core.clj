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
(def posts-path "/contents/posts/")
(def pages-path "/contents/pages/")

(defn generate-site []
  (do 
    (fk/rm-rf (io/file basedir  output-dir))
    (.mkdir (java.io.File. (str basedir "/" output-dir)))
    (.mkdir (java.io.File. (str basedir "/" output-dir "/pages")))
    (gen/generate-index basedir output-dir)
    (let [posts-dir (str basedir posts-path) posts (seq (.list (File. posts-dir)))]
      (gen/generate-posts basedir output-dir posts))
    (let [pages-dir (str basedir pages-path) pages (seq (.list (File. pages-dir)))]
      (gen/generate-pages basedir output-dir pages))
    (fk/cp-r (io/file basedir "public") (io/file basedir output-dir))))


(defn -main
  [& args]
  (let [base-directory (if (empty? args) "." (first args))
        conf-file (str base-directory "/site.conf")]
    (do
      (with-redefs [config/*config* (config/init conf-file)]
        (with-redefs [basedir base-directory
                      output-dir (config/get-string "output-dir")]
          (generate-site))))))
