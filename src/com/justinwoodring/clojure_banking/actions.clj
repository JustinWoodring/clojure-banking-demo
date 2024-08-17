(ns com.justinwoodring.clojure-banking.actions
  (:import (com.justinwoodring.clojure_banking.types Account Deposit Transaction Withdraw)
           (java.time LocalDateTime)))

(defrecord Action [name desc func])

(defn deposit
  "Action to deposit into accounts."
  [session]
  (let [account-id (do (println "Enter the account ID into which to deposit:") (read-line)),
        amount (do (println "Enter the amount you wish to deposit:") (parse-double (read-line))),
        update (fn [acc]
                 (if (= account-id (str (get acc :id)))
                   (assoc acc :records (cons (Transaction. (Deposit. "Cash") amount (LocalDateTime/now)) (get acc :records)))
                   acc))]
    (assoc session :accounts (map update (get session :accounts)))
    ))

(defn withdraw
  "Action to withdraw into accounts."
  [session]
  (let [account-id (do (println "Enter the account ID from which to withdraw:") (read-line)),
        amount (do (println "Enter the amount you wish to withdraw:") (parse-double (read-line))),
        update (fn [acc]
                 (if (= account-id (str (get acc :id)))
                   (assoc acc :records (cons (Transaction. (Withdraw. "Cash") amount (LocalDateTime/now)) (get acc :records)))
                   acc))]
    (assoc session :accounts (map update (get session :accounts)))
    ))

(defn create-account
  "Create an account."
  [session]
  (let [kind (do (println "Enter the kind of account:") (read-line)),
        amount (do (println "Enter the amount you wish to initially deposit:") (parse-double (read-line))),
        update (fn [] (Account. (random-uuid) kind (seq [(Transaction. (Deposit. "Cash") amount (LocalDateTime/now))])))]
    (assoc session :accounts (cons (update) (get session :accounts)))
    ))

(defn view-statement
  "View account statement."
  [session]
  (let [account-id (do (println "Enter the account ID from which to withdraw:") (read-line)),
        print-records (fn [records]
                        (println
                             "-"
                             (str (if (instance? Deposit (get (first records) :kind))
                                    "Deposit"
                                    "Withdraw"))
                             (str "$" (get (first records) :amount))
                             (str (get (first records) :time)))
                           (if-not (empty? (rest records))
                             (recur (rest records)))),
        index (fn [acc]
                (if (= account-id (str (get acc :id)))
                  (print-records (get acc :records))))]
    (doall (map index (get session :accounts)))
    session))

(defn actions [] (seq [(Action. "deposit" "Deposit money into an account." deposit),
                       (Action. "withdraw" "Withdraw money from an account." withdraw),
                       (Action. "create-account" "Open a new account." create-account),
                       (Action. "view-statement" "View transactions for an account" view-statement)]))