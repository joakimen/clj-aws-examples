(require '[cognitect.aws.credentials :as credentials]
         '[clojure.edn :as edn])

(defn read-config
  "read config-file into map"
  [] (edn/read-string (slurp "config.edn")))

(defn get-creds
  "configure basic credentials (id/key)"
  []
  (let [config (read-config)]
    (credentials/basic-credentials-provider
     {:access-key-id    (:access-key-id config)
      :secret-access-key (:secret-access-key config)})))


(comment
;; for credentials, i've put them into a gitignored config-file named config.edn.
;; create a config.edn with values that look like this, and use it here.
;; should of course be read from env, but i don't know the idiomatic way to do that yet
  {:access-key-id "my-id"
   :secret-access-key "my-key"})
