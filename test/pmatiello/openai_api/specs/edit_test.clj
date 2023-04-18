(ns pmatiello.openai-api.specs.edit-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.edit :as specs.edit]))

(def ^:private params
  {:model       "code-davinci-edit-001"
   :instruction "Fix clojure code."
   :input       "(println \"hello)"})

(def ^:private result
  {:object  "edit"
   :created 1681859564
   :choices [{:text "(println \"hello\")\n" :index 0}]
   :usage   {:prompt-tokens 22 :completion-tokens 19 :total-tokens 41}})

(deftest params-test
  (is (s/valid? ::specs.edit/params params)))

(deftest text-completion-test
  (is (s/valid? ::specs.edit/result result)))
