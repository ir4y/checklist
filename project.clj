(defproject checklist "0.1.0-SNAPSHOT"
  :description "Simple ajax checklist application"
  :url "http://checklist.allsol.ru/"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [com.taoensso/carmine "2.4.0"]
                 [enfocus "1.0.1"]
                 [jayq "2.3.0"]
                 [cheshire "5.2.0"]
                 [http-kit "2.1.13"]]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :source-paths ["src/clj" "src/cljs"]
  :main checklist.handler
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}}
  :cljsbuild {:builds
              [
               {:source-paths ["src/cljs"],
                :id "main",
                :compiler {:pretty-print true,
                           :output-to "resources/public/js/main.js",
                           :warnings true,
                           :externs ["externs/jquery-1.9.js"],
                           :optimizations :whitespace,
                           :print-input-delimiter false}}]})
