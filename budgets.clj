(require '[cognitect.aws.client.api :as aws])
(load-file "creds.clj")

(def budgets (aws/client {:api :budgets
                          :region "eu-west-1"
                          :credentials-provider (get-creds)}))

(comment

  (def accountId "34123123123") ;; insert actual accountid

;;   list available operations
  (-> (aws/ops budgets) keys sort)

;;  get single budget by name
  (-> (aws/invoke budgets {:op :DescribeBudget
                           :request {:AccountId accountId
                                     :BudgetName "my-50-dollar-budget"}})
      :Budget
      (select-keys [:BudgetName, :BudgetLimit :CalculatedSpend]))
  ;; => {:BudgetName "my-50-dollar-budget",
  ;;     :BudgetLimit {:Amount "50.0", :Unit "USD"},
  ;;     :CalculatedSpend {:ActualSpend {:Amount "13.994", :Unit "USD"}, :ForecastedSpend {:Amount "14.091", :Unit "USD"}}}

;;   create a budget with email alerts
  (aws/doc budgets :CreateBudget)
  (aws/invoke budgets
              {:op :CreateBudget
               :request {:AccountId accountId
                         :Budget {:CostFilters {}
                                  :TimePeriod {:Start #inst "2017-07-01T00:00:00.000-00:00",
                                               :End #inst "2022-12-15T00:00:00.000-00:00"},
                                  :TimeUnit "MONTHLY",
                                  :BudgetLimit {:Amount "5.0", :Unit "USD"},
                                  :BudgetType "COST",
                                  :BudgetName "my clojure aws budget"}
                         :NotificationsWithSubscribers
                         [{:Notification {:NotificationType "ACTUAL"
                                          :ComparisonOperator "GREATER_THAN",
                                          :ThresholdType "PERCENTAGE",
                                          :NotificationState "ALARM"},
                           :Subscribers [{:SubscriptionType "EMAIL", :Address "joakim.engeset@gmail.com"}]}]}})

;; list budgets
  (aws/doc budgets :DescribeBudget)
  (->> (aws/invoke budgets {:op :DescribeBudgets
                            :request {:AccountId accountId}})
       :Budgets
       (map #(select-keys % [:BudgetName :BudgetLimit :CalculatedSpend])))
  ;; => ({:BudgetName "my-50-dollar-budget",
  ;;      :BudgetLimit {:Amount "50.0", :Unit "USD"},
  ;;      :CalculatedSpend {:ActualSpend {:Amount "13.994", :Unit "USD"}, :ForecastedSpend {:Amount "14.091", :Unit "USD"}}}
  ;;     {:BudgetName "my clojure aws budget",
  ;;      :BudgetLimit {:Amount "5.0", :Unit "USD"},
  ;;      :CalculatedSpend {:ActualSpend {:Amount "13.994", :Unit "USD"}, :ForecastedSpend {:Amount "14.091", :Unit "USD"}}})

;;   Delete budget
  (aws/doc budgets :DeleteBudget)
  (aws/invoke budgets {:op :DeleteBudget
                       :request {:AccountId accountId
                                 :BudgetName "my clojure aws budget"}})
;;
  )
