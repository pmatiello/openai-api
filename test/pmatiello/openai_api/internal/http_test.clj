(ns pmatiello.openai-api.internal.http-test
  (:require [clj-http.client :as client]
            [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.openai-api.internal.http :as http]))

(def ^:private credentials
  {:api-key "api-key"})

(mfn/deftest get!-test
  (mfn/testing "makes get request to endpoint and returns the response body"
    (is (= {:data "ok"} (http/get! 'endpoint credentials)))
    (mfn/providing
      (client/get 'endpoint {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{\"data\":\"ok\"}"}))

  (mfn/testing "includes organization in credentials if provided"
    (http/get! 'endpoint (merge credentials {:org-id "org-id"}))
    (mfn/providing
      (client/get 'endpoint {:headers {"Authorization"       "Bearer api-key"
                                       "OpenAI-Organization" "org-id"}})
      {:status 200 :body "{}"})))

(mfn/deftest post!-test
  (mfn/testing "posts body to endpoint and returns the response body"
    (is (= {:data "ok"}
           (http/post! 'endpoint {:k "v"} credentials)))
    (mfn/providing
      (client/post 'endpoint
                   {:headers      {"Authorization" "Bearer api-key"}
                    :content-type :json
                    :body         "{\"k\":\"v\"}"})
      {:status 200 :body "{\"data\":\"ok\"}"})))
