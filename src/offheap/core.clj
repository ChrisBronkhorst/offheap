(ns offheap.core
  [:require [offheap.ops :as ops]])

(def !state (atom {}))

(defn assoc-in-op [state {:keys [value path]}]
  (assoc-in state path value))

(def functions
  {:cas  (fn [current [expected new]]
           (if (= current expected)
             new (throw (ex-info "cas failed" {:current current
                                               :expected expected}))))
   :add  (fn [current val]
           (if (and (number? current) (number? val))
             (+ current val)
             (throw (ex-info "add failed" {:current current
                                           :val     val}))))
   :conj (fn [current val] (conj current val))})

(defn get-in-op [state {:keys [path]}]
  (get-in state path))

(defn update-in-op [state {:keys [op value path] :as tx}]
  (if-let [f (functions op)]
    (update-in state path f value)
    (throw (ex-info "op doesn't exist" {:tx tx}))))

(defn handle-tx [state tx]
  (case (:op tx)
    :assoc-in  (assoc-in-op state tx)
    :cas       (update-in-op state tx)
    :add       (update-in-op state tx)
    :conj      (update-in-op state tx)
    (throw (ex-info "op doesn't exist" {:tx tx}))))

(defn handle-get [state tx]
  (case (:op tx)
    :get-in (get-in-op state tx)
    (throw (ex-info "op doesn't exist" {:tx tx}))))

(comment
  (swap! !state handle-tx (ops/assoc-in-op [:a :b] 6))
  (swap! !state handle-tx (ops/cas-op [:a :b] 6 7))
  (swap! !state handle-tx (ops/add-op [:a :b] 6))
  (swap! !state handle-get    (ops/get-in-op [:a :b])))