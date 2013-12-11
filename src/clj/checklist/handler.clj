(ns checklist.handler
  (:use compojure.core
        ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [cheshire.core :as json]
            [org.httpkit.server :as kit]
            [checklist.model :as model]))


(defn rpc-handler [data]
  (let [json (json/parse-string data)]
    (json/generate-string
      {:method (json "method")
       :result (case (json "method")
                 "set-done" (model/set-done (json "uuid") (json "done"))
                 "insert-check" (model/insert-check (json "text"))
                 "delete-check" (model/delete-check (json "uuid")))})))


(defn api-handler [request]
 (kit/with-channel request channel
  (kit/on-receive channel (fn [data]
                            (kit/send! channel (rpc-handler data))))))


(defroutes app-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/rpc"  [] api-handler)
  (GET  "/checklist" [] (response (json/generate-string (model/get-checklists))))
  (route/resources "/")
  (route/not-found "Not Found"))


(defn -main [& args]
  (kit/run-server (handler/site #'app-routes) {:port 8080}))
