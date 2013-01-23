(defproject cljq-upload "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.5"]
                 ;;[clj-json "0.5.2"]
                 [hiccup "1.0.2"]
                 [org.clojure/data.json "0.2.0"]
                 ]

  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler cljq-upload.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
