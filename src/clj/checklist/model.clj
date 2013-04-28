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

(defn insert_ckeck [check_text]
  (let [uuid (get-uuid)]
    (wcar (car/hset uuid "text" check_text))
    (wcar (car/hset uuid "done" false))
    uuid))

;(util/new-uuid)
;(get-uuid)
;(insert_ckeck "hello.jpg")
;
;(wcar (car/sadd "scheck" (util/new-uuid)))
;(wcar (car/hset "adca172b-79b5-487f-9d70-fbdbf7c52e4c"  "text" "hello.jpg"))
;(wcar (car/hset "adca172b-79b5-487f-9d70-fbdbf7c52e4c"  "done" false))
