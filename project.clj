(defproject lucene-cascalog "1.0.0-SNAPSHOT"
  :description "demo project using lucene from cascalog"
  :javac-options {:debug "true" :fork "true"}
  :source-path "src/clj"
  :aot :all
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [cascalog "1.8.2"]
                 [org.apache.lucene/lucene-core "3.4.0"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]]
  :repositories {"conjars.org" "http://conjars.org/repo"})

