(ns checklist.main
  (:require [enfocus.core :as ef]
            [jayq.core :as jq]
            [jayq.util :as ju])
  (:require-macros [jayq.macros :as jqm]
                   [enfocus.macros :as em])
  (:use [jayq.core :only [$ css inner]]))

(em/defsnippet checklist-item "/fragments.html" ["#checklist-item"] 
  [text]
  ["li"] (em/set-attr :id "")
  ["span"] (em/content text))


(defn setup-done-handler []
  (defn on-ckeckbox-click [e]
    (this-as this
           (let [self ($ this)]
             (if (-> self (.prop "checked"))
               (-> self (.parent) (.addClass "done"))
               (-> self (.parent) (.removeClass "done"))))))
  (jq/unbind ($ "._ckeck_list_item") "click" on-ckeckbox-click)
  (jq/bind ($ "._ckeck_list_item") "click" on-ckeckbox-click))

(defn press-enter [e] 
  (this-as this
           (let [self ($ this)
                 value (-> self (.val))]
             (when (= (.-which e) 13)
               (do
                  (-> ($ "#my_checklist") (.append (checklist-item value)))
                  (setup-done-handler))))))

(jqm/ready 
  (setup-done-handler)
  (jq/bind ($ "#new_checklist") "keyup" press-enter))
