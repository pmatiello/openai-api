(ns pmatiello.openai-api.specs.embedding-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.embedding :as specs.embedding]))

(def ^:private params
  {:model "text-embedding-ada-002"
   :input "brick wall"})

(def ^:private result
  {:object "list"
   :data   [{:object    "embedding"
             :index     0
             :embedding [-0.021928852 0.011067995 -0.017648032 -0.0065731322
                         -0.013298165 0.010018503 -0.021376489 -0.022177417
                         0.009852794 -0.032202825 -0.0016959303 -0.003098417
                         0.020106051 0.028778167 -0.00782976 0.002254336
                         ; remainder omitted for brevity
                         ]}]
   :model  "text-embedding-ada-002-v2"
   :usage  {:prompt-tokens 2 :total-tokens 2}})

(deftest params-test
  (is (s/valid? ::specs.embedding/params params)))

(deftest result-test
  (is (s/valid? ::specs.embedding/result result)))
