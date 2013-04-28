(ns checklist.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [checklist.model :as model]))
 ;           [ring.middleware.json :only [wrap-json-responce]]
;            [ring.util.response :only [response]]))

(use 
  'ring.middleware.json
  'ring.util.response)

(defroutes app-routes
  (GET "/checklist" [] (response (model/get-checklists)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-json-response (handler/site app-routes)))
