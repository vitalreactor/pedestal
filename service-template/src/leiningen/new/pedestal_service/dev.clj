; Copyright 2013 Relevance, Inc.

; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0)
; which can be found in the file epl-v10.html at the root of this distribution.
;
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
;
; You must not remove this notice, or any other, from this software.

;; dev mode in repl (can get prod mode by passing prod options to dev-init
(ns dev
  (:require [io.pedestal.service.http :as bootstrap]
            [{{name}}.service :as service]
            [{{name}}.server :as server]))

(def service (-> service/service ;; start with production configuration
                 (merge  {:env :dev
                          ;; do not block thread that starts web server
                          ::bootstrap/join? false
                          ;; reload routes on every request
                          ::bootstrap/routes #(deref #'service/routes)
                          ;; all origins are allowed in dev mode
                          ::bootstrap/allowed-origins [#""]})
                 (bootstrap/default-interceptors)
                 (bootstrap/dev-interceptors)))

(defn start
  [& [opts]]
  (server/create-server (merge service opts))
  (bootstrap/start server/service-instance))

(defn stop
  []
  (bootstrap/stop server/service-instance))

(defn restart
  []
  (stop)
  (start))

