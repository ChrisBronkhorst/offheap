(ns user
  [:require [offheap.core :refer [handle-tx handle-get]]
            [offheap.ops :as ops]])

#_ (clojure.repl.deps/sync-deps)

(def !state (atom {}))

(comment
  (swap! !state handle-tx (ops/assoc-in-op [:a :b] 6)))