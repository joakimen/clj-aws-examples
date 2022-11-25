(require '[cognitect.aws.client.api :as aws])
(load-file "creds.clj")

(comment

;;   create client
  (def iam (aws/client {:api :iam
                        :region "eu-west-1"
                        :credentials-provider (get-creds)}))


;; view all available operations for the client we've assembled
  (-> (aws/ops iam) keys sort)

;; view docs for a single operation
  (aws/doc iam :ListUsers)

;; invoke an operation
  (aws/invoke iam {:op :ListUsers})


;; list users
  (aws/doc iam :ListUsers) ;; docs
  (aws/invoke iam {:op :ListUsers}) ;; invoke
;;   {:Users
;;     [{:Path "/",
;;     :UserName "carl",
;;     :UserId "MIDA87LBZMZG9XFBKB32C",
;;     :Arn "arn:aws:iam::342121959177:user/carl",
;;     ...

;; get user
  (aws/doc iam :GetUser) ;; show docs
  (aws/invoke iam {:op :GetUser}) ;; get current user
  (-> (aws/invoke iam {:op :GetUser}) :User :Arn) ;; get get my ARN
  (aws/invoke iam {:op :GetUser :request {:UserName "yojimbo"}}) ;; get by username

;; list roles
  (->> (aws/invoke iam {:op :ListRoles}) :Roles (map :RoleName)))

;; generate credentialreport
(aws/invoke iam {:op :GenerateCredentialReport})

;; download credentialreport
(->>
 (aws/invoke iam {:op :GetCredentialReport})
 :Content
 (slurp)
 (spit "credential_report.csv"))
