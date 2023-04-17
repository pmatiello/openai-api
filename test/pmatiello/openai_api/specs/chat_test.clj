(ns pmatiello.openai-api.specs.chat-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.chat :as specs.chat]))

(def ^:private params
  {:model    "gpt-3.5-turbo"
   :messages [{:role "user" :content "(println \"hello"}]})

(def ^:private result
  {:id      "chatcmpl-76Sy80A1EGHGXRYuo1014ktROIlt9"
   :object  "chat.completion"
   :created 1681775680
   :model   "gpt-3.5-turbo-0301"
   :usage   {:prompt-tokens     12
             :completion-tokens 4
             :total-tokens      16}
   :choices [{:message       {:role "assistant" :content "world\")\nHello world"}
              :finish-reason "stop"
              :index         0}]})

(deftest params-test
  (is (s/valid? ::specs.chat/params params)))

(deftest result-test
  (is (s/valid? ::specs.chat/result result)))
