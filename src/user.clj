(ns user
  [:require [offheap.core :refer [handle-tx handle-get]]
            [offheap.ops :as ops]
            [offheap.store :as store]
            [editscript.core :as e]])

#_(clojure.repl.deps/sync-deps)

(def !state (atom {}))

(defn do-tx [state tx]
  (let [new-state (handle-tx state tx)]
    (store/log-operation tx)
    new-state))

(comment

  (let [txes [(ops/assoc-in-op [:a :c] {:hello "hi"})
              (ops/assoc-in-op [:users :chris] {:name "chris"})
              (ops/assoc-in-op [:users :kaveh] {:name "kaveh"})]]
    (swap! !state (partial reduce do-tx) txes))

  (swap! (atom {})
    store/replay-log handle-tx)

  (let [a {:the :data}
        b (assoc a :the :new-data)]
    (e/diff a b))


  nil)