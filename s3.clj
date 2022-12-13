(require '[cognitect.aws.client.api :as aws]
         '[clojure.java.io :refer [delete-file]]
         '[cheshire.core :as json])
(load-file "creds.clj")

(def s3 (aws/client {:api :s3
                     :region "eu-west-1"
                     :credentials-provider (get-creds)}))



(comment

;; View Operations
  (-> (aws/ops s3) keys sort)

;; List buckets
  (->> (aws/invoke s3 {:op :ListBuckets})
       :Buckets
       (mapv #(:Name %)))
;; => ["342191859277-terraform-state" "cf-templates-1guxnciaxmcqx-eu-west-1"]

;; Create bucket
  (aws/doc s3 :DeleteBucket)
  (aws/invoke s3 {:op :CreateBucket
                  :request {:Bucket "my-clj-bucket"
                            :CreateBucketConfiguration {:LocationConstraint "eu-west-1"}
                            :ACL "private"}})
;; => {:Location "http://my-clj-bucket.s3.amazonaws.com/"}

;; Delete bucket
  (aws/invoke s3 {:op :DeleteBucket :request {:Bucket "my-clj-bucket"}})


;; Upload file to s3
  (aws/doc s3 :PutObject)
  (spit "data.json" (-> {:data [{:name "vivi ornitier" :job "black mage"}
                                {:name "garnet til alexandros" :job "white mage"}
                                {:name "zidane tribal" :job "thief"}]}
                        (json/generate-string {:pretty true})))

  (aws/invoke s3 {:op :PutObject
                  :request {:Bucket "my-clj-bucket"
                            :Key "data.json"
                            :StorageClass "STANDARD_IA"
                            :Body (slurp "data.json")
                            :ServerSideEncryption "AES256"}})
;; => {:ETag "\"77821522b23b939bb657dde54b66d09b\""}
  (delete-file "data.json")

;; Get file from s3
  (-> (aws/invoke s3 {:op :GetObject
                      :request {:Bucket "my-clj-bucket"
                                :Key "data.json"}})
      :Body
      slurp
      (json/parse-string true))
  ;; => {:data
  ;;     [{:name "vivi ornitier", :job "black mage"}
  ;;      {:name "garnet til alexandros", :job "white mage"}
  ;;      {:name "zidane tribal", :job "thief"}]}

;;  Delete file from s3
  (aws/invoke s3 {:op :DeleteObject
                  :request {:Bucket "my-clj-bucket"
                            :Key "data.json"}})
;; => {:ETag "\"77821522b23b939bb657dde54b66d09b\""}

;; 
  )
