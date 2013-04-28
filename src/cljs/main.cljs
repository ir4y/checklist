(ns checklist.main
  (:require [enfocus.core :as ef]
            [jayq.core :as jq]
            [jayq.util :as ju])
  (:require-macros [jayq.macros :as jqm]
                   [enfocus.macros :as em])
  (:use [jayq.core :only [$ css inner]]))

(em/defsnippet checklist-item "/fragments.html" ["#checklist-item"] 
  ;[{uuid :uuid {text :text done :done} :values}]
  ;stupid clojure script bug 
  ;http://stackoverflow.com/questions/15764262/different-behavior-of-get-in-clojure-and-in-clojurescript/15768293#15791175
  [uuid text done]
  ["input"] (em/set-attr :id uuid)
  ["span"] (em/content text)
  ["label"] (em/set-attr :class (if (= "true" done) "done" ""))
  ["a"] (em/set-attr :rel uuid)
  ["input"] (when (= "true" done) (em/set-attr :checked "checked")))


(defn setup-done-handler []
  (defn on-checkbox-click [e]
    (this-as this
           (let [self ($ this)
                 uuid (-> self (.attr "id"))
                 done (-> self (.is ":checked"))]
             (jqm/let-ajax [_ {:url "/set_done" :type :post :data {:uuid uuid :done done}}]
             (if done
               (-> self (.parent) (.addClass "done"))
               (-> self (.parent) (.removeClass "done")))))))
  (defn on-del-click [e]
    (this-as this
             (let [self ($ this)
                   uuid (-> self (.attr "rel"))]
               (jqm/let-ajax [_ {:url "/delete" :type :post :data {:uuid uuid}}]
                 (-> self (.parent) (.remove))))))
  (jq/unbind ($ "._check_list_item") "click" on-checkbox-click)
  (jq/bind ($ "._check_list_item") "click" on-checkbox-click)
  (jq/unbind ($ "._delete") "click" on-del-click)
  (jq/bind ($ "._delete") "click" on-del-click))





(defn press-enter [e] 
  (this-as this
           (let [self ($ this)
                 value (-> self (.val))]
             (when (= (.-which e) 13)
               (jqm/let-ajax [uuid {:url "/new_check" :type :post :data {:text value}}]
                  (-> ($ "#my_checklist") (.append (checklist-item  uuid value "false")))
                  (setup-done-handler))))))


(defn destruct-proxy [check-item]
  (let [uuid (.-uuid check-item)
        values (.-values check-item)
        text (.-text values)
        done (.-done values)]
  (checklist-item uuid text done)))


(defn load-initial-data []
  (jqm/let-ajax [check-list {:url "/checklist" :dataType :json}]
    (em/at js/document
           ["#my_checklist"] (em/content (map destruct-proxy check-list)))
    (setup-done-handler)))


(jqm/ready 
  (load-initial-data)
  (jq/bind ($ "#new_checklist") "keyup" press-enter))
