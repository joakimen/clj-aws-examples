(require '[cognitect.aws.client.api :as aws])
(import java.util.Base64)
(load-file "creds.clj")

(def ec2 (aws/client {:api :ec2
                      :region "eu-west-1"
                      :credentials-provider (get-creds)}))

;; Helper-function to extract interesting information from an EC2 instance
(defn destructure [{:keys [Tags InstanceId PublicIpAddress State]}]
  {:Name (->> Tags (filter #(= (:Key %) "Name")) (map :Value) (apply str))
   :Id InstanceId
   :PublicIp PublicIpAddress
   :State (:Name State)})

(defn to-base64 [s]
  (.encodeToString (Base64/getEncoder) (.getBytes s)))

(comment
  (-> (aws/ops ec2) keys sort)

;; Create EC2 instance with default storage and an init-script from project
;; NB: doesn't configure security groups and access to public ip
  (aws/invoke ec2 {:op :RunInstances
                   :request
                   {:ImageId "ami-01cae1550c0adea9c",
                    :UserData (-> "ec2-userdata.sh" slurp to-base64),
                    :InstanceType "t2.micro"
                    :InstanceInitiatedShutdownBehavior "stop"
                    :TagSpecifications [{:ResourceType "instance"
                                         :Tags [{:Key "Name" :Value "3 my clj ec2 instance"}]}]
                    :MinCount 1,
                    :MaxCount 1}})

  (aws/doc ec2 :DescribeInstances)

;;  List all instances
  (defn list-instances []
    (->> (aws/invoke ec2 {:op :DescribeInstances})
         :Reservations
         (map :Instances)
         (apply concat)
         (map destructure)))
  ;; => ({:Name "my example instance", :Id "i-041a0ed0d07202bdcd", :PublicIp "3.219.244.184", :State "running"}
  ;;     {:Name "my second instance", :Id "i-0d26b26bb5ca96a2c", :PublicIp nil, :State "terminated"}

  (defn terminate-instances [instances]
    (aws/invoke ec2 {:op :TerminateInstances
                     :request {:InstanceIds instances}}))

;; List all running instances
  (->> (list-instances)
       (filter #(= (:State %) "running")))

  ;; WARNING - Terminate all EC2 instances
  (->> (list-instances)
       (mapv #(:Id %))
       (terminate-instances))

;; Delete (terminate) instance
  (aws/invoke ec2 {:op :TerminateInstances
                   :request {:InstanceIds ["i-039e96188898d8d95"]}})
  ;; => {:TerminatingInstances
  ;;     [{:CurrentState {:Code 32, :Name "shutting-down"},
  ;;       :InstanceId "i-038e96188898d8d95",
  ;;       :PreviousState {:Code 16, :Name "running"}}]}
  )
