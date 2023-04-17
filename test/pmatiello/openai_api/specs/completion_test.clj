(ns pmatiello.openai-api.specs.completion-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.completion :as specs.completion]))

(def ^:private params
  {:model "ada" :prompt "(println "})

(def ^:private result
  {:id      "cmpl-76SqijbqyCv7IH3LMQ5Jd1joCToK1"
   :object  "text_completion"
   :created 1681775220
   :model   "ada"
   :choices [{:text          "ˆ ˉ\n\nvalues.println ˆ ˉ"
              :index         0
              :logprobs      nil
              :finish-reason "length"}]
   :usage   {:prompt-tokens     3
             :completion-tokens 16
             :total-tokens      19}})

(deftest params-test
  (is (s/valid? ::specs.completion/params params)))

(deftest text-completion-test
  (is (s/valid? ::specs.completion/result result)))
