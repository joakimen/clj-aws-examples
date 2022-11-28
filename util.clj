(require '[clojure.edn :as edn])

(comment

;;   get latest api versions
  (->> "https://raw.githubusercontent.com/cognitect-labs/aws-api/main/latest-releases.edn"
       slurp
       edn/read-string
       (into [])))
