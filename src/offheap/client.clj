(ns offheap.client
  [:require [org.httpkit.client :as http]
            [offheap.ops :as ops]
            [clojure.edn :as edn]]
  (:import (clojure.lang ExceptionInfo)))

(defn make-client [url]
  {:url url})

(defn query [client query]
  (let [req (assoc client :method :get
                          :as   :stream
                          :body (pr-str query))
        res @(http/request req)]
    (case (:status res)
      200 (-> res :body slurp edn/read-string :value)
      400 (-> res :body slurp edn/read-string)
      500 (-> res :body slurp edn/read-string))))

(defn transact [client op]
  (let [req (assoc client :method :put
                          :as     :stream
                          :body   (pr-str op))
        res @(http/request req)]
    (case (:status res)
      200 (-> res :body slurp edn/read-string :value)
      400 (-> res :body slurp edn/read-string)
      500 (-> res :body slurp edn/read-string))))

(defn info [client]
  (let [req (-> client
                (update :url (fn [base] (str base "/info")))
                (assoc  :method :get
                        :as   :stream
                        :body (pr-str query)))]
     (-> @(http/request req)
         :body slurp edn/read-string)))

(comment
  (def client (make-client "http://localhost:3000"))

  (transact client (ops/assoc-in-op [:a :b] 6))
  (transact client (ops/assoc-in-op [:a :b] {:some {:really :deep} :value {:here 5}}))
  (transact client (ops/cas-op [:a :b] 7 8))
  (query client (ops/get-in-op [:a :b :c]))
  (query client (ops/get-in-op [:a :b]))
  (query client {:op :get-in :path 5})
  (info client)

  nil)