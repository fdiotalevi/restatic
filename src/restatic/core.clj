(ns restatic.core
  (:gen-class)
  (:require [restatic.generator :as gen]
            [restatic.config :as config])
  (:import [com.typesafe.config ConfigFactory]
           [java.io File]))

(def ^:dynamic basedir)
(def ^:dynamic output-dir)
(def posts-path "/contents/posts/")

(defn generate-site []
  (do 
    (.mkdir (java.io.File. (str basedir "/" output-dir)))
    (gen/generate-index basedir output-dir)
    (let [posts-dir (str basedir posts-path) posts (seq (.list (File. posts-dir)))]
      (gen/generate-posts basedir output-dir posts))))


(defn -main
  [& args]
  (let [base-directory (if (empty? args) "." (first args))
        conf-file (str base-directory "/site.conf")]
    (do
      (with-redefs [config/*config* (config/init conf-file)]
        (with-redefs [basedir base-directory
                      output-dir (config/get-string "output-dir")]
          (generate-site))))))
