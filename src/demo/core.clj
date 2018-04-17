(ns demo.core
  "Contains the core functions for namespace `demo.core`."
  (:use tupelo.core)
  (:require
    [clojure.string :as str]
    [schema.core :as s]
  ))

(s/defn mult :- s/Num
  "Multiply two numbers"
  [a :- s/Num
   b :- s/Num ]
  (* a b))

(defn -main []
  (println "main - enter")
  )

