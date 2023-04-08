(ns pmatiello.openai-api.integration-tests
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]
            [pmatiello.openai-api.api :as api]))

(stest/instrument)

(def ^:private api-key
  (System/getenv "OPENAI_API_KEY"))

(def ^:private credentials
  (api/credentials api-key))

(deftest credentials-test
  (is (s/valid? :pmatiello.openai-api.specs.credentials/credentials
                (api/credentials api-key))))

(deftest models-test
  (is (s/valid? :pmatiello.openai-api.specs.model/result-list
                (api/models credentials))))

(deftest model-test
  (is (s/valid? :pmatiello.openai-api.specs.model/result
                (api/model :gpt-3.5-turbo credentials))))

(deftest completion-test
  (is (s/valid? :pmatiello.openai-api.specs.completion/result
                (api/completion {:model "ada" :prompt "(println "} credentials))))

(deftest chat-test
  (is (s/valid? :pmatiello.openai-api.specs.chat/result
                (api/chat {:model    "gpt-3.5-turbo"
                           :messages [{:role "user" :content "Hello!"}]}
                          credentials))))

(deftest edit-test
  (is (s/valid? :pmatiello.openai-api.specs.edit/result
                (api/edit {:model        "code-davinci-edit-001"
                            :instruction "Fix."
                            :input       "(println \"hello)"}
                          credentials))))
