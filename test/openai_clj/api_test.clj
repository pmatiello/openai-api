(ns openai-clj.api-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [openai-clj.api :as api]
            [openai-clj.internal.http :as http]))

(mfn/deftest credentials-test
  (mfn/testing "returns credentials for API key"
    (is (= {:api-key "my-api-key"}
           (api/credentials "my-api-key"))))

  (mfn/testing "returns credentials for API key and organisation id"
    (is (= {:api-key "my-api-key"
            :org-id  "my-org-id"}
           (api/credentials "my-api-key" "my-org-id")))))

(mfn/deftest models-test
  (mfn/testing "retrieves models"
    (is (= 'response (api/models 'credentials)))
    (mfn/providing
      (http/get! "https://api.openai.com/v1/models" 'credentials) 'response)))
