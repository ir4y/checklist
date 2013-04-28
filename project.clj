(defproject checklist "0.1.0-SNAPSHOT"
  :description "Simple ajax checklist application"
  :url "http://checklist.allsol.ru/"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [com.taoensso/carmine "1.6.0"]
                 [enfocus "1.0.1"]
                 [jayq "2.3.0"]
                 ]
  :plugins [[lein-ring "0.8.3"]
            [lein-cljsbuild "0.3.0"]
            [lein-tarsier "0.10.0"]]
  :ring {:handler checklist.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}}
  
  
  :aot []
  :source-paths ["src/clj" "src/cljs"]
  
  :cljsbuild
  {:builds
   [
    {:source-paths ["src/cljs"],
     :id "main",
     :compiler
     {:pretty-print true,
      :output-to "resources/public/js/main.js",
      :warnings true,
      :externs ["externs/jquery-1.9.js"],
      ;; :optimizations :advanced,
      :optimizations :whitespace,
      :print-input-delimiter false}}]})
