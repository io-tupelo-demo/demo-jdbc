(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [korma.db :as kdb]
    [korma.core :as korma]
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as str]
    [clojure.java.io :as io] )
  (:import [demo Calc] ))

(dotest
  (is= 5 (spyx (+ 2 3)))        ; expected equality
  (isnt= 9 (+ 2 3))             ; expected inequality
  (throws? (/ 5 0))             ; expected error condition
  (is true)                     ; expected truthy-ness
  (isnt false)                  ; expected falsey-ness

  ; basic clojure tests
  (is= 4   (mult 2 2))
  (is= 5.0 (mult 2 2.5))

  ; resource access & regex
  (let [crisis-txt (slurp (io/resource "thomas-paine.txt")) ]
    (is (truthy? (re-find #"THESE are the times" crisis-txt))))

  ; java interop
  (is= 5.0 (spyx (Calc/add 2 3)))
  (let [result (spyxx (demo.Calc/incVals {"able" 1 "baker" 2} )) ]
    (is= {"able" 2 "baker" 3} result)
    (is= java.util.HashMap (type result)))
)

