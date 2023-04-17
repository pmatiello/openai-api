(ns pmatiello.openai-api.specs.model-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.model :as specs.model]))

(def ^:private description
  {:id         "gpt-3.5-turbo"
   :object     "model"
   :created    1677610602
   :owned-by   "openai"
   :permission [{:allow-create-engine  false
                 :allow-fine-tuning    false
                 :group                nil
                 :allow-sampling       true
                 :allow-view           true
                 :created              1681343255
                 :allow-logprobs       true
                 :organization         "*"
                 :is-blocking          false
                 :allow-search-indices false
                 :id                   "modelperm-BmdmcAa1aQwToDxri3DFbZw9"
                 :object               "model_permission"}]
   :root       "gpt-3.5-turbo"
   :parent     nil})

(def ^:private description-list
  {:object "list"
   :data   [description]})

(deftest description-test
  (is (s/valid? ::specs.model/description description)))

(deftest description-list-test
  (is (s/valid? ::specs.model/description-list description-list)))
