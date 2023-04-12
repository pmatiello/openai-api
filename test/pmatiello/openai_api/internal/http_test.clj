(ns pmatiello.openai-api.internal.http-test
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.openai-api.internal.http :as http]))

(def ^:private credentials
  {:api-key "api-key"})

(def ^:private file
  (io/file "test/fixtures/image.png"))

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
      {:status 200 :body "{}"}))

  (mfn/testing "converts between clojure and json key style conventions"
    (is (= {:abc-xyz "ok"} (http/get! 'endpoint credentials)))
    (mfn/providing
      (client/get 'endpoint {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{\"abc_xyz\":\"ok\"}"})))

(mfn/deftest post!-test
  (mfn/testing "single part requests"
    (mfn/testing "posts body to endpoint and returns the response body"
      (is (= {:data "ok"}
             (http/post! 'endpoint {:body {:k "v"}} credentials)))
      (mfn/providing
        (client/post 'endpoint
                     {:headers      {"Authorization" "Bearer api-key"}
                      :body         "{\"k\":\"v\"}"
                      :content-type :json})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "converts between clojure and json key style conventions"
      (is (= {:a-bc "ok"}
             (http/post! 'endpoint {:body {:x-yz "ko"}} credentials)))
      (mfn/providing
        (client/post 'endpoint
                     {:headers      {"Authorization" "Bearer api-key"}
                      :body         "{\"x_yz\":\"ko\"}"
                      :content-type :json})
        {:status 200 :body "{\"a_bc\":\"ok\"}"})))

  (mfn/testing "multipart requests"
    (mfn/testing "posts parameters to endpoint and returns the response body"
      (is (= {:data "ok"}
             (http/post! 'endpoint {:multipart {:k1 "v1" :k2 2}} credentials)))
      (mfn/providing
        (client/post 'endpoint
                     {:headers   {"Authorization" "Bearer api-key"}
                      :multipart [{:name "k1", :content "v1"}
                                  {:name "k2", :content "2"}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "converts between clojure and json key style conventions"
      (is (= {:data "ok"}
             (http/post! 'endpoint {:multipart {:k-1 "v"}} credentials)))
      (mfn/providing
        (client/post 'endpoint
                     {:headers   {"Authorization" "Bearer api-key"}
                      :multipart [{:name "k_1" :content "v"}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "uploads given files"
      (is (= {:data "ok"}
             (http/post! 'endpoint {:multipart {:file file}} credentials)))
      (mfn/providing
        (client/post 'endpoint
                     {:headers   {"Authorization" "Bearer api-key"}
                      :multipart [{:name "file" :content file}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))))
