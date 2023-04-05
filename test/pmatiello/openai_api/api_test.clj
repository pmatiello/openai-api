(ns pmatiello.openai-api.api-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.openai-api.api :as api]
            [pmatiello.openai-api.internal.http :as http]))

(mfn/deftest credentials-test
  (mfn/testing "returns credentials for API key"
    (is (= {:api-key "my-api-key"}
           (api/credentials "my-api-key"))))

  (mfn/testing "returns credentials for API key and organisation id"
    (is (= {:api-key "my-api-key"
            :org-id  "my-org-id"}
           (api/credentials "my-api-key" "my-org-id")))))

(mfn/deftest models-test
  (mfn/testing "retrieves list of models"
    (is (= 'response (api/models 'credentials)))
    (mfn/providing
      (http/get! "https://api.openai.com/v1/models" 'credentials) 'response)))

(deftest model-test
  (mfn/testing "retrieves the given model"
    (is (= 'response (api/model :teapot 'credentials)))
    (mfn/providing
      (http/get! "https://api.openai.com/v1/models/teapot" 'credentials) 'response)))


(deftest completion-test
  (mfn/testing "retrieves completion"
    (is (= 'response (api/completion {:model :chamomile :k :v} 'credentials)))
    (mfn/providing
      (http/post! "https://api.openai.com/v1/completions"
                  {:model :chamomile :k :v} 'credentials) 'response)))
