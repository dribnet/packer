(ns packer
  (:require [cljs.reader :refer [read-string]]))

(def d3 (this-as ct (aget ct "d3")))

; poor man's scheduling - not smart with stack usage, etc.
(defn process-next-fn [q]
  "calls next fn in queue, expects to be called back"
  (if (seq q)
    ((first q) (rest q))))

(defn process-json [s q]
  (-> d3 (.text (str "data/" s ".json") 
    (fn [error, txt]
      (-> d3 (.select (str ".json .size ." s)) (.text (str (count txt))))
      (let [time1 (js/Date.)
            json (.parse js/JSON txt)
            time2 (js/Date.)
            clj  (js->clj json)
            time3 (js/Date.)]
        (-> d3 (.select (str ".json .deserialize ." s)) 
          (.text (str (- (.getTime time2) (.getTime time1)))))
        (-> d3 (.select (str ".json .translate ." s)) 
          (.text (str (- (.getTime time3) (.getTime time2)))))
      )
      (process-next-fn q)
    ))))

(defn process-edn [s q]
  (-> d3 (.text (str "data/" s ".edn") 
    (fn [error, txt]
      (-> d3 (.select (str ".edn .size ." s)) (.text (str (count txt))))
      (let [time1 (js/Date.)
            edn (read-string txt)
            time2 (js/Date.)
            json  (clj->js edn)
            time3 (js/Date.)]
        (-> d3 (.select (str ".edn .deserialize ." s)) 
          (.text (str (- (.getTime time2) (.getTime time1)))))
        (-> d3 (.select (str ".edn .translate ." s)) 
          (.text (str (- (.getTime time3) (.getTime time2)))))
      )
      (process-next-fn q)
    ))))

(def to-records-fn (fn [src]
  (let [alicia  (map keyword (first src))]
    (vec (map #(zipmap alicia %) (rest src))))))

(defn process-edn-flat [s q]
  (-> d3 (.text (str "data/" s "-flat.edn") 
    (fn [error, txt]
      (-> d3 (.select (str ".ednflat .size ." s)) (.text (str (count txt))))
      (let [time1 (js/Date.)
            edn1 (read-string txt)
            time2 (js/Date.)
            edn2  (to-records-fn edn1)
            time3 (js/Date.)]
        (-> d3 (.select (str ".ednflat .deserialize ." s)) 
          (.text (str (- (.getTime time2) (.getTime time1)))))
        (-> d3 (.select (str ".ednflat .translate ." s)) 
          (.text (str (- (.getTime time3) (.getTime time2)))))
      )
      (process-next-fn q)
    ))))

(def function-queue [
  (partial process-json "year")
  (partial process-json "month")
  (partial process-json "week")
  (partial process-edn "year")
  (partial process-edn "month")
  (partial process-edn "week")
  (partial process-edn-flat "year")
  (partial process-edn-flat "month")
  (partial process-edn-flat "week")
  ])

(process-next-fn function-queue)

