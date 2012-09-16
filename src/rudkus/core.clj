(ns rudkus.core
  (:require [ring.adapter.jetty :as jetty]
            [clojure.tools.cli :as cli])
  (:gen-class))

(def handler
  (fn [request]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body "Hello, World!"}))

(def ^{:private true} server (atom nil))

(defn start [port]
  (swap! server #(if (not (nil? %))
                  (throw (IllegalStateException. "Server already started."))
                  (jetty/run-jetty handler
                                   {:port port
                                    :join? false}))))

(defn stop []
  (swap! server #(if (nil? %)
                  (throw (IllegalStateException. "Server already stopped."))
                  (do (.stop %)
                      nil))))

(defn -main [& args]
  (let [[options extra-args banner] (cli/cli args
                                             ["-p" "--port" "Port" :default 8080 :parse-fn #(Integer. %)])]
    (if (not-empty extra-args)
      (println banner)
      (start (:port options)))))
