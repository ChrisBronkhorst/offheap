(ns offheap.server
  (:require [org.httpkit.server :refer [run-server]]
            [clojure.edn :as edn]
            [offheap.core :refer [handle-tx handle-get]])
  (:import (clojure.lang ExceptionInfo)))

(defn wrap-errors [handler]
  (fn [req]
    (try
      (handler req)
      (catch ExceptionInfo e
        {:status  400
         :headers {"Content-Type" "application/edn"}
         :body    (pr-str {:error (ex-message e)
                           :data  (ex-data e)})})
      (catch Exception e
        {:status  400
         :headers {"Content-Type" "application/edn"}
         :body    (let [error (Throwable->map e)]
                    {:error (:cause error)})})
      (catch Throwable e
        {:status  500
         :headers {"Content-Type" "application/edn"}
         :body    (pr-str {:error "Internal server error"})}))))

(defn make-handler [!state]
  (fn handler [req]
    (case (:request-method req)
      :put (let [tx (edn/read-string (slurp (:body req)))]
             (swap! !state handle-tx tx)
             {:status  200
              :headers {"Content-Type" "application/edn"}
              :body    (pr-str {:value true})})

      :get (let [tx (edn/read-string (slurp (:body req)))]
             {:status  200
              :headers {"Content-Type" "application/edn"}
              :body    (pr-str {:value (handle-get @!state tx)})})
      (throw (ex-info "method not allowed" {:method (:request-method req)})))))

(defonce server (atom nil))

(defn stop-server []
  (when @server
    (@server)
    (reset! server nil)
    (println "Server stopped.")))

(defn start-server [{:keys [port !state] :as opts}]
  (when-let [server @server]
    (stop-server))
  (reset! server (run-server (-> (make-handler !state)
                                 (wrap-errors))
                             opts))
  (println (str "Server started on localhost:" port)))

(comment
  ;; Start the server
  (let [!state (atom {})]
    (start-server {:port 3000 :!state !state}))

  ;; Stop the server
  (stop-server)

  nil)