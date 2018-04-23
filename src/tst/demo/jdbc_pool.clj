(ns tst.demo.jdbc-pool
  (:use demo.core
        tupelo.core
        tupelo.test)
  (:require
    [clojure.java.jdbc :as jdbc]
    [hikari-cp.core :as pool]
    ))

(def datasource-options-sample {:auto-commit        true
                                :read-only          false
                                :connection-timeout 30000
                                :validation-timeout 5000
                                :idle-timeout       600000
                                :max-lifetime       1800000
                                :minimum-idle       10
                                :maximum-pool-size  10
                                :pool-name          "db-pool"
                                :adapter            "h2" ; "postgresql"
                                :username           "sa"
                                :password           ""
                                :database-name      "database"
                                :server-name        "localhost"
                                :port-number        5432
                                :register-mbeans    false})

(def datasource-options {:adapter  "h2"
                         :url      "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1"
                         :username "sa"
                         :password ""})

(def ^:dynamic db-conn nil)

(defn with-connection-pool
  "Creates and uses a connection for test function"
  [tst-fn]
  (let [datasource (pool/make-datasource datasource-options)]
    (binding [db-conn {:datasource datasource}]
      (tst-fn)
      (pool/close-datasource datasource)))) ; close the connection - also closes/destroys the in-memory database

(use-fixtures
  :once with-connection-pool) ; use the same db connection pool for all tests

(dotest
  ; creates & drops a connection (& transaction) for each command
  (jdbc/db-do-commands db-conn ["drop table if exists langs"
                                "drop table if exists releases"])

  (jdbc/db-do-commands
    db-conn
    [(jdbc/create-table-ddl :langs
                            [[:id :serial]
                             [:lang "varchar not null"]])
     (jdbc/create-table-ddl :releases
                            [[:id :serial]
                             [:desc "varchar not null"]

                             [:langId "numeric"]])])
  (jdbc/insert-multi! db-conn :langs ; => ({:id 1} {:id 2})
                      [{:lang "Clojure"}
                       {:lang "Java"}])
  (let [result (jdbc/query db-conn ["select * from langs"])]
    (is= result [{:id 1, :lang "Clojure"}
                 {:id 2, :lang "Java"}]))

  ; Wraps all commands in a single transaction
  (jdbc/with-db-transaction
    [tx db-conn]
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
  (let [
        ; note cannot wrap select list in parens or get "bulk" output
        result-0 (jdbc/query db-conn ["select langs.lang, releases.desc
                                             from    langs join releases
                                             on     (langs.id = releases.langId)
                                             where  (lang = 'Clojure') "])
        result-1 (jdbc/query db-conn ["select l.lang, r.desc
                                             from    langs as l
                                                       join releases as r
                                             on     (l.id = r.langId)
                                             where  (l.lang = 'Clojure') "])
        result-2 (jdbc/query db-conn ["select langs.lang, releases.desc
                                              from    langs, releases
                                              where  ( (langs.id = releases.langId)
                                                and    (lang = 'Clojure') ) "])
        result-3 (jdbc/query db-conn ["select l.lang, r.desc
                                            from    langs as l, releases as r
                                            where  ( (l.id = r.langId)
                                              and    (l.lang = 'Clojure') ) "])]
    ;(sets= result-0 result-1 result-2 result-3  ; #todo use this
    ;       [{:lang "Clojure", :desc "1.8"}
    ;        {:lang "Clojure", :desc "1.9"}
    ;        {:lang "Clojure", :desc "ancients"}])
    (is (= (set [{:lang "Clojure", :desc "1.8"}
                 {:lang "Clojure", :desc "1.9"}
                 {:lang "Clojure", :desc "ancients"}])
           (set result-0)
           (set result-1)
           (set result-2)
           (set result-3))))
  )

