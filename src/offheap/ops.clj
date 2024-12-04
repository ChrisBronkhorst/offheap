(ns offheap.ops
  [:require [clojure.spec.alpha :as s]])

;; Define specs
(s/def ::op #{:get-in :assoc-in :update-in :cas :add :conj})
(s/def ::path-item (s/or :keyword keyword? :int integer? :string string?))
(s/def ::path (s/every ::path-item :kind vector?))
(s/def ::value any?)
(s/def ::tx (s/keys :req-un [::op ::path] :opt-un [::value]))

(s/def ::operation (s/keys :req-un [::op ::path]))

(s/def ::get-in (s/keys :req-un [::op ::path]))
(s/def ::assoc-in (s/keys :req-un [::op ::path ::value]))
(s/def ::update-in (s/keys :req-un [::op ::path ::value]))
(s/def ::cas (s/keys :req-un [::op ::path ::value]))
(s/def ::add (s/keys :req-un [::op ::path ::value]))

;; Functions to create op data
(defn get-in-op [path]
  (assert (s/valid? ::path path) "path must be a valid vector")
  {:op   :get-in
   :path path})

(defn assoc-in-op [path value]
  (assert (s/valid? ::path path) "path must be a valid vector")
  {:op    :assoc-in
   :path  path
   :value value})

(defn update-in-op [path op value]
  (assert (s/valid? ::path path) "path must be a valid vector")
  (assert (s/valid? ::op op) "op must be a valid operation")
  {:path  path
   :op    op
   :value value})

(defn cas-op [path expected new]
  (assert (s/valid? ::path path) "path must be a valid vector")
  {:op    :cas
   :path  path
   :value [expected new]})

(defn add-op [path value]
  (assert (s/valid? ::path path) "path must be a valid vector")
  {:op    :add
   :path  path
   :value value})

(defn conj-op [path value]
  (assert (s/valid? ::path path) "path must be a valid vector")
  {:op    :conj
   :path  path
   :value value})