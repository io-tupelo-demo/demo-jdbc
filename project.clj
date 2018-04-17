(defproject demo  "0.1.0-SNAPSHOT"
  :description    "FIXME: write description"
  :url            "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [criterium "0.4.4"]
   ;[instaparse "1.4.8"]
    [org.clojure/clojure "1.9.0"]
    [org.clojure/spec.alpha "0.1.143"]
    [org.clojure/test.check "0.9.0"]
   ;[org.clojure/core.async "0.4.474"]
   ;[org.clojure/data.avl "0.0.17"]
   ;[org.clojure/data.json "0.2.6"]
   ;[org.clojure/data.xml             "0.2.0-alpha5"]
   ;[org.clojure/math.combinatorics "0.1.4"]
   ;[org.clojure/tools.analyzer       "0.6.9"]
    [prismatic/schema "1.1.7"]
    [tupelo "0.9.71"]
  ]
  :profiles {:dev {:dependencies []
                   :plugins [
                             [com.jakemccrary/lein-test-refresh   "0.22.0"]
                             [lein-ancient                        "0.6.15"]
                             [lein-codox                          "0.10.3"] ] }
             :uberjar {:aot :all}}

  :global-vars {*warn-on-reflection* false}
  :main ^:skip-aot demo.core

  :source-paths       ["src"]
  :test-paths         ["src"]
  :java-source-paths  ["src-java"]
  :target-path        "target/%s"
  :jvm-opts           ["-Xms500m" "-Xmx2g"]
)
