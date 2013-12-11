(ns checklist.main
  (:require [enfocus.core :as ef]
            [jayq.core :as jq]
            [jayq.util :as ju])
  (:require-macros [jayq.macros :as jqm]
                   [enfocus.macros :as em])
  (:use [jayq.core :only [$ css inner]]))

(def ws (js-obj))

(em/defsnippet checklist-item "/fragments.html" ["#checklist-item"] 
  [uuid text done]
  ["input"] (em/set-attr :id uuid)
  ["span"] (em/content text)
  ["label"] (em/set-attr :class (if done "done" ""))
  ["a"] (em/set-attr :rel uuid)
  ["input"] (when done (em/set-attr :checked "checked")))

(defn setup-done-handler []
  (defn on-checkbox-click [e]
    (this-as this
           (let [self ($ this)
                 uuid (-> self (.attr "id"))
                 done (-> self (.is ":checked"))
                 data (js-obj "method" "set-done" "uuid" uuid "done" done)
                 serialized (.stringify js/JSON data)]
             (.send ws serialized))))
             
  (defn on-del-click [e]
    (this-as this
             (let [self ($ this)
                   uuid (-> self (.attr "rel"))
                   data (js-obj "method" "delete-check" "uuid" uuid)
                   serialized (.stringify js/JSON data)]
               (.send ws serialized))))
  (jq/unbind ($ "._check_list_item") "click" on-checkbox-click)
  (jq/bind ($ "._check_list_item") "click" on-checkbox-click)
  (jq/unbind ($ "._delete") "click" on-del-click)
  (jq/bind ($ "._delete") "click" on-del-click))

(defn press-enter [e] 
  (this-as this
           (let [self ($ this)
                 value (-> self (.val))]
             (when (= (.-which e) 13)
               (let [data (js-obj "method" "insert-check" "text" value)
                     serialized (.stringify js/JSON data)]
                 (.send ws serialized))))))

(defn load-initial-data []
  (jqm/let-ajax [check-list {:url "/checklist" :dataType :json}]
    (em/at js/document ["#my_checklist"]
           (em/content 
             (map 
               (fn [check-item]
                 (let [uuid (.-uuid check-item)
                       values (.-values check-item)
                       text (.-text values)
                       done (.-done values)]
                   (checklist-item uuid text done)))
               check-list)))
    (setup-done-handler)))

(defn insert-check [check]
  (-> ($ "#my_checklist") (.append (checklist-item
                                     (.-uuid check)
                                     (.-text check)
                                     (.-done check)))))

(defn delete-check [uuid]
  (-> ($ (str "#" uuid)) (.parent) (.parent) (.remove)))

(defn set-done [check]
  (let [item ($ (str "#" (.-uuid check)))]
    (if (.-done check)
      (-> item (.parent) (.addClass "done"))
      (-> item (.parent) (.removeClass "done")))))

(jqm/ready 
  (load-initial-data)
  (set! ws (js/WebSocket. "ws://localhost:8080/rpc"))
  (set! (.-onmessage ws) (fn [message] 
                           (let [responce (.parse js/JSON (.-data message))]
                             (case (.-method responce)
                               "insert-check" (insert-check 
                                                (.-result responce))
                               "set-done" (set-done 
                                            (.-result responce))
                               "delete-check" (delete-check 
                                                (.-result responce)))
                             (setup-done-handler))))
  (jq/bind ($ "#new_checklist") "keyup" press-enter))
