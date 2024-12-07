(ns offheap.store
  [:require [clojure.java.io :as io]
            [clojure.edn :as edn]])

(def log-file "wal.log")

(defn log-operation [op]
  (with-open [w (io/writer log-file :append true)]
    (.write w (str (pr-str op) "\n"))))

(defn replay-log [state f]
  (with-open [r (io/reader log-file)]
    (reduce f state (map edn/read-string (line-seq r)))))