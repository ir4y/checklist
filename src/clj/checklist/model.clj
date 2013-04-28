(ns checklist.model
    (:require [taoensso.carmine :as car]
              [checklist.util :as util]))


(def pool         (car/make-conn-pool)) ; See docstring for additional options
(def spec-server1 (car/make-conn-spec))


(defmacro wcar [& body] `(car/with-conn pool spec-server1 ~@body))


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


(defn delete-check [uuid]
  (wcar (car/srem "scheck" uuid)
        (car/hdel uuid "text")
        (car/hdel uuid "done")))
