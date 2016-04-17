(defproject clj-base-crm "0.2.2"
  :description "Simple library to interact with the Base CRM API."
  :url "https://github.com/RyanBertrand/clj-base-crm"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]

                 ;HTTP Client
                 [clj-http "2.0.0"]

                 ;JSON
                 [cheshire "5.5.0"]

                 ;Logging
                 [org.clojure/tools.logging "0.3.1"]]
  :plugins [[lein-codox "0.9.4"]]
  :codox {:output-path "doc"
          :metadata {:doc/format :markdown}}
  :deploy-repositories [["clojars" {:sign-releases false}]]
  :lein-release {:deploy-via :clojars}
  :profiles {:uberjar {:aot :all}})
