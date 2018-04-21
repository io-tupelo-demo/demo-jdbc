(ns tst.demo.jdbc
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [clojure.java.jdbc :as jdbc]
   ))

(def db
  {:classname   "org.h2.Driver"
   :subprotocol "h2:mem"      ; the prefix `jdbc:` is added automatically
   :subname     "demo;DB_CLOSE_DELAY=-1" ; `;DB_CLOSE_DELAY=-1` very important!!!
                   ; http://www.h2database.com/html/features.html#in_memory_databases
                   ; http://makble.com/using-h2-in-memory-database-in-clojure
   :user        "sa"          ; "system admin"
   :password    ""            ; empty string by default
  })

(dotest
  ; creates & drops a connection (& transaction) for each command
  (jdbc/db-do-commands db ["drop table if exists langs"
                           "drop table if exists releases"]) ; => (0 0)

  ; Creates and uses a connection for all commands
  (jdbc/with-db-connection
    [conn db]
    (jdbc/db-do-commands
      conn
      [(jdbc/create-table-ddl :langs
                              [[:id :serial]
                               [:lang "varchar not null"]])
       (jdbc/create-table-ddl :releases
                              [[:id :serial]
                               [:desc "varchar not null"]
                               [:langId "numeric"]]) ]))

  (jdbc/insert-multi! db :langs ; => ({:id 1} {:id 2})
                      [{:lang "Clojure"}
                       {:lang "Java"}])

  (let [result (jdbc/query db ["select * from langs"])]
    (is= result [{:id 1, :lang "Clojure"} {:id 2, :lang "Java"}]))

  ; Wraps all commands in a single transaction
  (jdbc/with-db-transaction
    [tx db]
    (let [clj-id (grab :id (only (jdbc/query tx ["select id from langs where lang='Clojure'"])))]
      (jdbc/insert-multi! tx :releases
                          [{:desc "ancients" :langId clj-id}
                           {:desc "1.8" :langId clj-id}
                           {:desc "1.9" :langId clj-id}]))
    (let [java-id (grab :id (only (jdbc/query tx ["select id from langs where lang='Java'"])))]
      (jdbc/insert-multi! tx :releases
                          [{:desc "dusty" :langId java-id}
                           {:desc "8" :langId java-id}
                           {:desc "9" :langId java-id}
                           {:desc "10" :langId java-id}])))

  (let [result (jdbc/query db ["select langs.lang, releases.desc
                                   from   langs inner join releases
                                   on     (langs.id = releases.langId)
                                   where  (lang = 'Clojure') "])]
    (sets= result [{:lang "Clojure", :desc "1.8"}
                   {:lang "Clojure", :desc "1.9"}
                   {:lang "Clojure", :desc "ancients"}] )))


