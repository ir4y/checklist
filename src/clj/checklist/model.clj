(ns checklist.model
    (:require [taoensso.carmine :as car]
              [checklist.util :as util]))

(def pool         (car/make-conn-pool)) ; See docstring for additional options
(def spec-server1 (car/make-conn-spec))

(defmacro wcar [& body] `(car/with-conn pool spec-server1 ~@body))

(defn get_links []
  (wcar (car/lrange "links" 0 -1)))

(defn get-uuid []
  (loop[uuid (util/new-uuid)]
    (if (= (wcar (car/sadd "scheck" uuid)) 1)
      uuid
      (recur util/new-uuid))))

(defn insert-check [check_text]
  (let [uuid (get-uuid)]
    (wcar (car/hset uuid "text" check_text))
    (wcar (car/hset uuid "done" false))
    uuid))

(defn get-check [uuid]
  (wcar
    [(car/hget uuid "text") (car/hget uuid "done")]))

(defn set-done [uuid done]
  (wcar (car/hset uuid "done" done)))

(defn to-hash [uuid]
  (let [result (get-check uuid)]
    {:lookup "fake" :uuid uuid :values {:text (get result 0) :done (get result 1)}}))

(defn get-checklists []
  (map to-hash (wcar (car/smembers "scheck"))))

;(defn test-destruct [{uuid :uuid {text :text done :done} :values}]
;  (println uuid text done)
;  uuid)

;(map test-destruct (get-checklists))
;(set-done "0eeefa01-43a5-4e35-acd1-c93eab8d6fc4" true)
;(get-check "0eeefa01-43a5-4e35-acd1-c93eab8d6fc4")
;(set-done "0eeefa01-43a5-4e35-acd1-c93eab8d6fc4" false)
;(get-check "0eeefa01-43a5-4e35-acd1-c93eab8d6fc4")
;(util/new-uuid)
;(get-uuid)
;(insert_ckeck "hello.jpg")
;
;(wcar (car/sadd "scheck" (util/new-uuid)))
;(wcar (car/hset "adca172b-79b5-487f-9d70-fbdbf7c52e4c"  "text" "hello.jpg"))
;(wcar (car/hset "adca172b-79b5-487f-9d70-fbdbf7c52e4c"  "done" false))
