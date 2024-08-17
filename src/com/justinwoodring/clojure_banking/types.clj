(ns com.justinwoodring.clojure-banking.types
  "Type Declarations for Banking")

(defrecord Person [fname lname accounts])

(defrecord Account [id type records])

(defrecord Transaction [kind amount time])

(defrecord Deposit [from])

(defrecord Withdraw [to])
