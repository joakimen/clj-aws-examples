(require '[cognitect.aws.client.api :as aws]
         '[taoensso.timbre :as log])
(load-file "creds.clj")

(def client (aws/client {:api :lambda
                         :region "eu-west-1"
                         :credentials-provider (get-creds)}))

(comment

;;   create lambda
  (-> (aws/ops client) keys sort)

  (aws/doc client :CreateFunction)
  (aws/invoke client {:op :CreateFunction
                      :request {:FunctionName "my-clj-lambda-fn"
                                :Code {:ZipFile "fn.zip"}
                                :Role "demo-clj-lambda-role
"}})


;; 
  )
