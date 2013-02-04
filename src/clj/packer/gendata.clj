(ns packer.gendata
  [:use [clojure.java.io]]
  [:require
    [serializable.fn :as serfn]
    [clj-json.core :as json]
    [clojure-csv.core :as csv]
    [clojure.java.io :refer [writer]]
    [clojure.pprint :refer [pprint]]])

; a thin abstraction to json parsing
(def clj->js json/generate-string)
(defn js->clj [x]
  (json/parse-string x true))

(defn read-csv
  "Takes file name and reads data."
  [fname]
  (with-open [file (java.io.FileReader. fname)]
    (csv/parse-csv (slurp file))))

(defn write-json [d fname]
  (with-open [wrtr (writer fname)]
    (.write wrtr (clj->js d))))

(defn write-edn [d fname]
  (with-open [wrtr (writer (str fname))]
    (pprint d wrtr)))

; define a serializable function
(def to-records-raw (serfn/fn [src]
  (let [alicia  (map keyword (first src))]
    (vec (map #(zipmap alicia %) (rest src))))))

(def to-records-str (pr-str to-records-raw))

; use locally via eval
(def to-records (eval (read-string to-records-str)))

(defn -main []
  (let [src     (read-csv "public/data/source.csv")
        records (to-records src)
        recs30  (vec (take 30 records))
        recs7   (vec (take 7 records))
        flat    {:data src :datafun to-records-str}
        flat30  {:data (vec (take 31 src)) :datafun to-records-str}
        flat7   {:data (vec (take 7 src)) :datafun to-records-str}]
    (write-json records "public/data/year.json")
    (write-json recs30 "public/data/month.json")
    (write-json recs7 "public/data/week.json")
    (write-edn records "public/data/year.edn")
    (write-edn recs30 "public/data/month.edn")
    (write-edn recs7 "public/data/week.edn")
    (write-edn flat "public/data/year-flat.edn")
    (write-edn flat30 "public/data/month-flat.edn")
    (write-edn flat7 "public/data/week-flat.edn")
    ))
