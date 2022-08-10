(defproject demo  "0.1.0-SNAPSHOT"
  :description    "FIXME: write description"
  :url            "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
     [com.h2database/h2 "2.1.214"]
     [hikari-cp "2.14.0"]
     [org.clojure/clojure "1.11.1"]
     [org.clojure/java.jdbc "0.7.12"]
     [prismatic/schema "1.3.5"]
     [tupelo "22.07.25a"]
     ]
  :plugins      [[com.jakemccrary/lein-test-refresh "0.25.0"]]

  :global-vars {*warn-on-reflection* false}
  :main ^:skip-aot demo.core

  :source-paths ["src"]
  :test-paths ["src"]
  :target-path "target/%s"
  :jvm-opts ["-Xms500m" "-Xmx2g"]
  )
