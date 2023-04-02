(ns openai-clj.internal.http-test
  (:require [clj-http.client :as client]
            [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [openai-clj.internal.http :as http]))

(mfn/deftest get!-test
  (mfn/testing "makes get request to endpoint and returns the response body"
    (is (= {:data "ok"}
           (http/get! 'endpoint {:api-key "api-key"})))
    (mfn/providing
      (client/get 'endpoint {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{\"data\":\"ok\"}"}))

  (mfn/testing "includes organization in credentials if provided"
    (http/get! 'endpoint {:api-key "api-key" :org-id "org-id"})
    (mfn/providing
      (client/get 'endpoint {:headers {"Authorization"       "Bearer api-key"
                                       "OpenAI-Organization" "org-id"}})
      {:status 200 :body "{}"})))
