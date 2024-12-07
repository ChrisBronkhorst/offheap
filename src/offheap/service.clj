(ns offheap.service
  [:require [offheap.core :refer [handle-tx handle-get]]
            [offheap.store :as store]])

(defn make-service [!state]
  {:handle-tx  handle-tx
   :handle-get handle-get
   :!state     !state})