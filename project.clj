(defproject packer "0.0.1-SNAPSHOT"
  :description "packer: cljs unpacking tests"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clojure-csv/clojure-csv "2.0.0-alpha1"]
                 [serializable-fn "1.1.3"]
                 [clj-json "0.5.1"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.2.10"]]

  :main packer.gendata

  :cljsbuild {
    :builds [{
      :source-path "src/cljs"
      :compiler {
        :output-to "public/out/packer.js"
        :optimizations :whitespace
        :pretty-print true 
        ; :optimizations :simple
        }}]})
