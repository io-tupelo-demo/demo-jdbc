(defproject demo  "0.1.0-SNAPSHOT"
  :description    "FIXME: write description"
  :url            "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
     [com.h2database/h2 "1.4.198"]
     [hikari-cp "2.7.0"]
     [org.clojure/java.jdbc "0.7.9"]
     [org.clojure/clojure "1.10.0"]
     [org.clojure/test.check "0.9.0"]
     [org.postgresql/postgresql "42.2.5"] ; https://mvnrepository.com/artifact/org.postgresql/postgresql
     [prismatic/schema "1.1.10"]
     [tupelo "0.9.130"]
     ]
  :profiles {:dev     {:dependencies []
                       :plugins      [[com.jakemccrary/lein-test-refresh "0.22.0"]
                                      [lein-ancient "0.6.15"]
                                      [lein-codox "0.10.3"]]}
             :uberjar {:aot :all}}

  :global-vars {*warn-on-reflection* false}
  :main ^:skip-aot demo.core

  :source-paths ["src"]
  :test-paths ["src"]
  :libs [ "libs/"]
  :java-source-paths ["src-java"]
  :target-path "target/%s"
  :jvm-opts ["-Xms500m" "-Xmx2g"]
  )

