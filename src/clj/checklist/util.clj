(ns checklist.util)

(defn new-uuid []
      (str (java.util.UUID/randomUUID)))

(new-uuid)
