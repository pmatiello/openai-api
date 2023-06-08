(ns me.pmatiello.openai-api.specs.completion-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [me.pmatiello.openai-api.specs.completion :as specs.completion]))

(def ^:private params
  {:model "ada" :prompt "(println "})

(def ^:private result-full
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

(def ^:private result-stream
  [{:id      "cmpl-7Ow9jEhyvcXRfACIgLNjYxkQjSiZF",
    :object  "text_completion",
    :created 1686177779,
    :choices [{:text "up", :index 0, :logprobs nil, :finish-reason nil}],
    :model   "ada"}
   {:id      "cmpl-7Ow9jEhyvcXRfACIgLNjYxkQjSiZF",
    :object  "text_completion",
    :created 1686177779,
    :choices [{:text "down", :index 0, :logprobs nil, :finish-reason "length"}],
    :model   "ada"}])

(deftest params-test
  (is (s/valid? ::specs.completion/params params)))

(deftest result-full-test
  (is (s/valid? ::specs.completion/result result-full)))

(deftest result-stream-test
  (is (s/valid? ::specs.completion/result result-stream)))
