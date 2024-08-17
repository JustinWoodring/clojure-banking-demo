(ns com.justinwoodring.clojure-banking.core
  "Banking Demo in Clojure"
  (:require
    [com.justinwoodring.clojure-banking.types :as types]
    [com.justinwoodring.clojure-banking.actions :as actions]
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.java.io :as io])
  (:import
    (com.justinwoodring.clojure_banking.types Account Deposit Person Transaction)
    (java.time LocalDateTime)
    (java.util UUID)))

(defn new-account
  "Prompt user for details creating an account."
  [account]
  (println "How much would you like to open an account with:")
  (let [nameStrings (str/split account #" "),
        openingSum (parse-double (read-line))
        accounts (seq [(Account. (random-uuid) "checking" (seq [(Transaction. (Deposit. "Cash") openingSum (LocalDateTime/now))]))])]
    (defn userAccount [] (Person. (first nameStrings) (last nameStrings) accounts))))

(defn initialize
  "Open a user account or create account."
  []
  (println "Epic Banking by JW Solutions :)")
  (println "Type name of the account holder: ")
  (let [account (read-line)]
    (if (.exists (io/file account))
      (load-file account)
      (new-account account))))

(defn print-header
  [session]
  (println "Account Holder:" (str (str/upper-case (get session :fname)) ",") (str/upper-case (get session :lname))))

(defn sum-records
  [records]
  (let [
        summation (fn [a, b] (if (instance? Deposit (get b :kind))
                      (+ a (get b :amount))
                      (- a (get b :amount))))
        ]
  (reduce summation 0.0 records)))

(defn print-accounts
  [session]
  (println "Accounts:")
  (let [accounts (get session :accounts),
        x (fn [account_list]
            (println
              "-"
              (str (get (first account_list) :id))
              (str/capitalize (get (first account_list) :type))
              (str "$" (sum-records (get (first account_list) :records))))
            (if-not (empty? (rest account_list))
              (recur (rest account_list))))
        ]
      (x accounts)
    ))

(defn print-actions
  [actions]
  (println "Actions:")
  (let [x (fn [actions]
            (println
              "-"
              (get (first actions) :name)
              "---"
              (get (first actions) :desc))
            (if-not (empty? (rest actions))
              (recur (rest actions))))
        ]
    (x actions)))

(defn exec
  "Execute an action"
  [session, choice, actions]
  (if (= choice (get (first actions) :name))
    ((get (first actions) :func) session)
    (if-not (empty? (rest actions))
      (recur session choice (rest actions))
      session)))

(defn close-session
  []
  (println "Goodbye. Have a nice day!"))

(defn event-loop
  "Event Loop"
  [session]
  (print-header session)
  (print-accounts session)
  (print-actions (actions/actions))
  (let [choice (read-line)]
    (if-not (= choice "quit")
      (recur (exec session choice (actions/actions)))
      (close-session))))

(defn main
  "Startup"
  []
  (initialize)
  (event-loop (userAccount)))

(main)


