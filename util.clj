(require '[clojure.edn :as edn]
         '[clojure.string :as str])

(defn get-aws-apiversions []
  (->> (slurp "https://raw.githubusercontent.com/cognitect-labs/aws-api/main/latest-releases.edn")
       edn/read-string
       (map second)
       (filter #(not (str/blank? (get-in % [:aws/serviceFullName]))))))

(comment

      ;;  (map second)


      ;;  
  (->> (slurp "https://raw.githubusercontent.com/cognitect-labs/aws-api/main/latest-releases.edn")
       edn/read-string
       (filter #(not (str/blank? (get-in (second %) [:aws/serviceFullName]))))
       (filter #(re-find #"Lambda" (:aws/serviceFullName (second %)))))

  (->> (slurp "https://raw.githubusercontent.com/cognitect-labs/aws-api/main/latest-releases.edn")
       edn/read-string
       (filter #(not (str/blank? (get-in (second %) [:aws/serviceFullName])))))
;;   get latest api versions
  ;; (def resp (slurp  "https://raw.githubusercontent.com/cognitect-labs/aws-api/main/latest-releases.edn"))
  ;; (->> resp
  (->> (get-aws-apiversions)
       (filter #(re-find #"S3" (:aws/serviceFullName %)))
       (filter #(re-find #"S3" (:aws/serviceFullName %)))
;; 
       ))
