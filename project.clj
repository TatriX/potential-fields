(defproject potential-fields "0.1.1-SNAPSHOT"
  :description "Potential fields pathfindind demo"

  :url "https://github.com/tatrix"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [rm-hull/monet "0.3.0"]]
  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.4-7"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.10"]]}}

  :plugins [[lein-cljsbuild "1.1.4"
             :exclusions [org.clojure/clojure]]
            [lein-figwheel "0.5.4-7"]]

  :clean-targets ^{:protect false} ["resources/public/js/out"
                                    "resources/public/js/bundle.js"
                                    :target-path]

  :source-paths ["src"]

  :cljsbuild {
              :builds [{:id "potential-fields-demo"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:optimizations :none
                                   :main potential-fields.core
                                   :asset-path "js/out"
                                   :output-to "resources/public/js/bundle.js"
                                   :output-dir "resources/public/js/out"
                                   :pretty-print true
                                   :source-map-timestamp true}}]}

  :figwheel { :css-dirs ["resources/public/css"]
             :open-file-command "emacsclient"
             ;; Load CIDER, refactor-nrepl and piggieback middleware
             :nrepl-middleware ["cider.nrepl/cider-middleware"
                                "refactor-nrepl.middleware/wrap-refactor"
                                "cemerick.piggieback/wrap-cljs-repl"]})
