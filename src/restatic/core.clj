(ns restatic.core
  (:gen-class)
  (:require [restatic.config :as config]))



(defn- run [conf-file]
    (with-redefs [config/*config* (config/init conf-file)]
      (println (config/get-string "test"))))


(defn -main
  [& args]
  (let [basedir (if (empty? args) "." (first args))
        conf-file (str basedir "/site.conf")]
    (run conf-file)))
