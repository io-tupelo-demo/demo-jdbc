(ns tst.demo.jdbc
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [korma.db :as kdb]
    [korma.core :as korma]
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as str]
    [clojure.java.io :as io] )
  (:import [demo Calc] ))

(def db {:classname   "org.h2.Driver"
         :subprotocol "h2:mem"
        ;:subname     "./korma.db"
         :subname     "demo;DB_CLOSE_DELAY=-1" ; -1 very important!!!
                          ; http://www.h2database.com/html/features.html#in_memory_databases
                          ; http://makble.com/using-h2-in-memory-database-in-clojure
         :user "sa"
         :password ""
         })

(dotest
  (spyx (jdbc/db-do-commands db ["drop table if exists tags"]))
  (spy :create
       (jdbc/db-do-commands
         db
         (jdbc/create-table-ddl :tags
                                [[:id :serial]
                                 [:name "varchar not null"]])))
  (spy :insert
       (jdbc/insert! db :tags
                     {:name "Clojure"}
                     {:name "Java"}))
  ;; -> ({:name "Clojure", :id 1} {:name "Java", :id 2})

  (spy :query
       (jdbc/query db ["select * from tags where name='Clojure'"]))
  ;; -> ({:name "Clojure", :id 1})

  )


;(dotest
;  (is= 5 (spyx (+ 2 3)))        ; expected equality
;  (isnt= 9 (+ 2 3))             ; expected inequality
;  (throws? (/ 5 0))             ; expected error condition
;  (is true)                     ; expected truthy-ness
;  (isnt false)                  ; expected falsey-ness
;
;  ; basic clojure tests
;  (is= 4   (mult 2 2))
;  (is= 5.0 (mult 2 2.5))
;
;  ; resource access & regex
;  (let [crisis-txt (slurp (io/resource "thomas-paine.txt")) ]
;    (is (truthy? (re-find #"THESE are the times" crisis-txt))))
;
;  ; java interop
;  (is= 5.0 (spyx (Calc/add 2 3)))
;  (let [result (spyxx (demo.Calc/incVals {"able" 1 "baker" 2} )) ]
;    (is= {"able" 2 "baker" 3} result)
;    (is= java.util.HashMap (type result)))
;)

