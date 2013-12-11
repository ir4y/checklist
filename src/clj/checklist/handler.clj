(ns checklist.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [org.httpkit.server :as kit]
            [checklist.model :as model]))

(use 
  'ring.middleware.json
  'ring.util.response)

(defroutes app-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET  "/checklist" [] (response (model/get-checklists)))
  (POST "/set_done" [uuid done] (response {:result (model/set-done uuid done)}))
  (POST "/new_check" [text] (response (model/insert-check text)))
  (POST "/delete" [uuid] (response {:result (model/delete-check uuid)}))
  (route/resources "/")
  (route/not-found "Not Found"))


(defn -main [& args]
  (kit/run-server (wrap-json-response
                    (handler/site #'app-routes)) {:port 8080}))
