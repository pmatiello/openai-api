(ns me.pmatiello.openai-api.internal.http-test
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [me.pmatiello.openai-api.internal.http :as http]))

(def ^:private config
  {:api-key "api-key" :base-url "base-url/"})

(def ^:private file
  (io/file "test/fixtures/image.png"))

(mfn/deftest get!-test
  (mfn/testing "makes get request to endpoint and returns the response body"
    (is (= {:data "ok"} (http/get! 'path config)))
    (mfn/providing
      (client/get "base-url/path" {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{\"data\":\"ok\"}"}))

  (mfn/testing "includes organization header if provided in config"
    (http/get! 'path (merge config {:org-id "org-id"}))
    (mfn/providing
      (client/get "base-url/path" {:headers {"Authorization"       "Bearer api-key"
                                             "OpenAI-Organization" "org-id"}})
      {:status 200 :body "{}"}))

  (mfn/testing "converts between clojure and json key style conventions"
    (is (= {:abc-xyz "ok"} (http/get! 'path config)))
    (mfn/providing
      (client/get "base-url/path" {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{\"abc_xyz\":\"ok\"}"}))

  (mfn/testing "returns unparsed results when options include {:parse? false}"
    (is (= "raw body" (http/get! 'path config {:parse? false})))
    (mfn/providing
      (client/get "base-url/path" {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "raw body"})))

(mfn/deftest post!-test
  (mfn/testing "single part requests"
    (mfn/testing "posts body to endpoint and returns the response body"
      (is (= {:data "ok"}
             (http/post! 'path {:body {:k "v"}} config)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers      {"Authorization" "Bearer api-key"}
                      :body         "{\"k\":\"v\"}"
                      :content-type :json})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "converts between clojure and json key style conventions"
      (is (= {:a-bc "ok"}
             (http/post! 'path {:body {:x-yz "ko"}} config)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers      {"Authorization" "Bearer api-key"}
                      :body         "{\"x_yz\":\"ko\"}"
                      :content-type :json})
        {:status 200 :body "{\"a_bc\":\"ok\"}"})))

  (mfn/testing "multipart requests"
    (mfn/testing "posts parameters to endpoint and returns the response body"
      (is (= {:data "ok"}
             (http/post! 'path {:multipart {:k1 "v1" :k2 2}} config)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers   {"Authorization" "Bearer api-key"}
                      :multipart [{:name "k1", :content "v1"}
                                  {:name "k2", :content "2"}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "converts between clojure and json key style conventions"
      (is (= {:data "ok"}
             (http/post! 'path {:multipart {:k-1 "v"}} config)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers   {"Authorization" "Bearer api-key"}
                      :multipart [{:name "k_1" :content "v"}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "uploads given files"
      (is (= {:data "ok"}
             (http/post! 'path {:multipart {:file file}} config)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers   {"Authorization" "Bearer api-key"}
                      :multipart [{:name "file" :content file}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))))

(mfn/deftest delete!-test
  (mfn/testing "makes get request to endpoint and returns the response body"
    (is (= {:data "ok"} (http/delete! 'path config)))
    (mfn/providing
      (client/delete "base-url/path" {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{\"data\":\"ok\"}"})))
