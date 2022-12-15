(require '[cognitect.aws.client.api :as aws]
         '[taoensso.timbre :as log])
(load-file "creds.clj")

(def sqs (aws/client {:api :sqs
                      :region "eu-west-1"
                      :credentials-provider (get-creds)}))


(defn create-queue [queue-name]
  (log/info "creating queue:" queue-name)
  (:QueueUrl (aws/invoke sqs {:op :CreateQueue
                              :request {:QueueName queue-name}})))

(defn send-message [queue-url message-body]
  (log/info "sending message:" message-body)
  (aws/invoke sqs {:op :SendMessage
                   :request {:QueueUrl queue-url
                             :MessageBody message-body}}))

(defn receive-message [queue-url]
  (log/info "receiving messages from queue:" queue-url)
  (aws/invoke sqs {:op :ReceiveMessage
                   :request {:QueueUrl queue-url}}))

(defn- delete-message [queue-url receipt-handle]
  (log/info "deleting message:" (subs receipt-handle 0, 8))
  (aws/invoke sqs {:op :DeleteMessage
                   :request {:QueueUrl queue-url
                             :ReceiptHandle receipt-handle}}))

(defn delete-queue [queue-url]
  (log/info "deleting queue:" queue-url)
  (aws/invoke sqs {:op :DeleteQueue :request {:QueueUrl queue-url}}))


(defn process-messages [queue-url]
  (let [messages (:Messages (receive-message queue-url))]
    (log/info (format "received [%d] messages" (count messages)))
    (log/info "messages:" (->> messages (mapv :Body) str))
    (mapv #(delete-message queue-url (:ReceiptHandle %)) messages)))

(comment

  (let [queue (create-queue "my-clj-queue")]
    (send-message queue "hello from clojure")
    (process-messages queue)
    (delete-queue queue))
;; 2022-12-15T10:12:19.361Z jeuno.local INFO [user:11] - creating queue: my-clj-queue
;; 2022-12-15T10:12:19.627Z jeuno.local INFO [user:16] - sending message: hello from clojure
;; 2022-12-15T10:12:19.686Z jeuno.local INFO [user:22] - receiving messages from queue: https://sqs.eu-west-1...
;; 2022-12-15T10:12:19.731Z jeuno.local INFO [user:39] - received [1] messages
;; 2022-12-15T10:12:19.732Z jeuno.local INFO [user:40] - messages: ["hello from clojure"]
;; 2022-12-15T10:12:19.732Z jeuno.local INFO [user:27] - deleting message: AQEBSd2u
;; 2022-12-15T10:12:19.777Z jeuno.local INFO [user:34] - deleting queue https://sqs.eu-west-1...
  )
