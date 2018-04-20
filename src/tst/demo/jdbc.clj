(ns tst.demo.jdbc
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as str]
    [clojure.java.io :as io] ))

(def db {:classname   "org.h2.Driver"
         :subprotocol "h2:mem" ; the prefix `jdbc:` is added automatically
         :subname     "demo;DB_CLOSE_DELAY=-1" ; -1 very important!!!
                          ; http://www.h2database.com/html/features.html#in_memory_databases
                          ; http://makble.com/using-h2-in-memory-database-in-clojure
         :user "sa"           ; "system admin"
         :password ""         ; empty string by default
        })

(dotest
  (spyx (jdbc/db-do-commands db ["drop table if exists langs"]))
  (spyx (jdbc/db-do-commands db ["drop table if exists releases"]))
  (spy :create
       (jdbc/db-do-commands
         db
         (jdbc/create-table-ddl :langs
                                [[:id :serial]
                                 [:lang "varchar not null"]])))
  (spy :create
       (jdbc/db-do-commands
         db
         (jdbc/create-table-ddl :releases
                                [[:id :serial]
                                 [:desc "varchar not null"]
                                 [:langId "numeric"]])))
  (spy :insert
       (jdbc/insert-multi! db :langs
                           [{:lang "Clojure"}
                            {:lang "Java"}]))
  ;; -> ({:lang "Clojure", :id 1} {:lang "Java", :id 2})
  (spyx-pretty (jdbc/query db ["select * from langs"]))

  (let [clj-id (grab :id (only (jdbc/query db ["select id from langs where lang='Clojure'"])))]
    (spyx clj-id)
    (spy :insert-rel
         (jdbc/insert-multi! db :releases
                             [{:desc "ancients" :langId clj-id}
                              {:desc "1.8" :langId clj-id}
                              {:desc "1.9" :langId clj-id}])) )
  (let [java-id (grab :id (only (jdbc/query db ["select id from langs where lang='Java'"])))]
    (spyx java-id)
    (spy :insert-rel
         (jdbc/insert-multi! db :releases
                             [{:desc "dusty" :langId java-id}
                              {:desc "8" :langId java-id}
                              {:desc "9" :langId java-id}
                              {:desc "10" :langId java-id}])) )
  (spyx-pretty
   (jdbc/query db [
   "select langs.lang, releases.desc
       from langs inner join releases
       on (langs.id = releases.langId)
       where lang = 'Clojure' "]) )

  )


