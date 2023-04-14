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

(mfn/deftest model-test
  (mfn/testing "retrieves the given model"
    (is (= 'response (api/model :teapot 'credentials)))
    (mfn/providing
      (http/get! "https://api.openai.com/v1/models/teapot" 'credentials) 'response)))


(mfn/deftest completion-test
  (mfn/testing "retrieves completion"
    (is (= 'response (api/completion 'params 'credentials)))
    (mfn/providing
      (http/post! "https://api.openai.com/v1/completions"
                  {:body 'params} 'credentials) 'response)))

(mfn/deftest chat-test
  (mfn/testing "retrieves completion"
    (is (= 'response (api/chat 'params 'credentials)))
    (mfn/providing
      (http/post! "https://api.openai.com/v1/chat/completions"
                  {:body 'params} 'credentials) 'response)))

(mfn/deftest edit-test
  (mfn/testing "retrieves edit"
    (is (= 'response (api/edit 'params 'credentials)))
    (mfn/providing
      (http/post! "https://api.openai.com/v1/edits"
                  {:body 'params} 'credentials) 'response)))

(mfn/deftest image-generation-test
  (mfn/testing "retrieves a generated image"
    (is (= 'response (api/image-generation 'params 'credentials)))
    (mfn/providing
      (http/post! "https://api.openai.com/v1/images/generations"
                  {:body 'params} 'credentials) 'response)))

(mfn/deftest image-edit-test
  (mfn/testing "retrieves an edited image"
    (is (= 'response (api/image-edit 'params 'credentials)))
    (mfn/providing
      (http/post! "https://api.openai.com/v1/images/edits"
                  {:multipart 'params} 'credentials) 'response)))

(deftest image-variation-test
  (mfn/testing "retrieves an image variation"
    (is (= 'response (api/image-variation 'params 'credentials)))
    (mfn/providing
      (http/post! "https://api.openai.com/v1/images/variations"
                  {:multipart 'params} 'credentials) 'response)))
