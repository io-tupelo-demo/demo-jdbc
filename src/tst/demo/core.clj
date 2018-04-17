(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require 
    [clojure.string :as str]
    [clojure.java.io :as io] )
  (:import [demo Calc] ))

(dotest
  (is= (spyx (+ 2 3)))
  (throws? (/ 5 0))
  (isnt false)

  (let [crisis-txt (slurp (io/resource "thomas-paine.txt")) ]
    (is (truthy? (re-find #"THESE are the times" crisis-txt))))

  (is= 4   (mult 2 2))
  (is= 5.0 (mult 2 2.5))

  (is= 5.0 (spyx (Calc/add 2 3)))
  (let [result (spyxx (demo.Calc/incVals {"able" 1 "baker" 2} )) ]
    (is= {"able" 2 "baker" 3} result)
    (is= java.util.HashMap (type result)))
)

