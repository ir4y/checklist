(ns checklist.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [checklist.model :as model]))

(use 
  'ring.middleware.json
  'ring.util.response)

(defroutes app-routes
  (GET  "/checklist" [] (response (model/get-checklists)))
  (POST "/set_done" [uuid done] {:result (response (model/set-done uuid done))})
  (POST "/new_check" [text] (response (model/insert-check text)))
  (POST "/delete" [uuid] (response {:result (model/delete-check uuid)}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-json-response (handler/site app-routes)))
