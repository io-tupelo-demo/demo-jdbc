(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [clojure.string :as str]
    [clojure.java.io :as io] ) )

(dotest
  (is= 5 (+ 2 3))        ; expected equality
  (isnt= 9 (+ 2 3))             ; expected inequality
  (throws? (/ 5 0))             ; expected error condition
  (is true)                     ; expected truthy-ness
  (isnt false)                  ; expected falsey-ness

  ; resource access & regex
  (let [crisis-txt (slurp (io/resource "thomas-paine.txt")) ]
    (is (truthy? (re-find #"THESE are the times" crisis-txt))))

)

