(ns me.pmatiello.openai-api.internal.http-test
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [me.pmatiello.openai-api.internal.http :as http]
            [mockfn.clj-test :as mfn])
  (:import (java.io ByteArrayInputStream)))

(def ^:private config
  {:api-key "api-key" :base-url "base-url/"})

(def headers
  {"Authorization" "Bearer api-key"})

(def ^:private file
  (io/file "test/fixtures/image.png"))

(mfn/deftest get!-test
  (mfn/testing "makes get request to endpoint and returns the response body"
    (is (= {:data "ok"} (http/get! 'path nil config nil)))
    (mfn/providing
      (client/get "base-url/path" {:headers headers})
      {:status 200 :body "{\"data\":\"ok\"}"}))

  (mfn/testing "includes provided query params"
    (is (= {:data "ok"} (http/get! 'path 'query-params config nil)))
    (mfn/providing
      (client/get "base-url/path" {:headers headers :query-params 'query-params})
      {:status 200 :body "{\"data\":\"ok\"}"}))

  (mfn/testing "converts between clojure and json key style conventions"
    (is (= {:abc-xyz "ok"} (http/get! 'path nil config nil)))
    (mfn/providing
      (client/get "base-url/path" {:headers headers})
      {:status 200 :body "{\"abc_xyz\":\"ok\"}"}))

  (mfn/testing "returns unparsed results when options include {:parse? false}"
    (is (= "raw body" (http/get! 'path nil config {:parse? false})))
    (mfn/providing
      (client/get "base-url/path" {:headers headers})
      {:status 200 :body "raw body"})))

(mfn/deftest post!-test
  (mfn/testing "single part requests"
    (mfn/testing "posts body to endpoint and returns the response body"
      (is (= {:data "ok"}
             (http/post! 'path {:body {:k "v"}} config nil)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers      headers
                      :body         "{\"k\":\"v\"}"
                      :content-type :json})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "converts between clojure and json key style conventions"
      (is (= {:a-bc "ok"}
             (http/post! 'path {:body {:x-yz "ko"}} config nil)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers      headers
                      :body         "{\"x_yz\":\"ko\"}"
                      :content-type :json})
        {:status 200 :body "{\"a_bc\":\"ok\"}"}))

    (mfn/testing "returns unparsed results when options include {:parse? false}"
      (is (= "raw body" (http/post! 'path {:body {:k "v"}} config {:parse? false})))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers      headers
                      :body         "{\"k\":\"v\"}"
                      :content-type :json})
        {:status 200 :body "raw body"})))

  (mfn/testing "multipart requests"
    (mfn/testing "posts parameters to endpoint and returns the response body"
      (is (= {:data "ok"}
             (http/post! 'path {:multipart {:k1 "v1" :k2 2}} config nil)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers   headers
                      :multipart [{:name "k1", :content "v1"}
                                  {:name "k2", :content "2"}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "converts between clojure and json key style conventions"
      (is (= {:data "ok"}
             (http/post! 'path {:multipart {:k-1 "v"}} config nil)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers   headers
                      :multipart [{:name "k_1" :content "v"}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))

    (mfn/testing "uploads given files"
      (is (= {:data "ok"}
             (http/post! 'path {:multipart {:file file}} config nil)))
      (mfn/providing
        (client/post "base-url/path"
                     {:headers   headers
                      :multipart [{:name "file" :content file}]})
        {:status 200 :body "{\"data\":\"ok\"}"}))))

(mfn/deftest delete!-test
  (mfn/testing "makes get request to endpoint and returns the response body"
    (is (= {:data "ok"} (http/delete! 'path config)))
    (mfn/providing
      (client/delete "base-url/path" {:headers headers})
      {:status 200 :body "{\"data\":\"ok\"}"})))

(mfn/deftest authorization-test
  (mfn/testing "includes api-key header"
    (http/get! 'path nil config nil)
    (mfn/providing
      (client/get (mockfn.matchers/any)
                  {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{}"}))

  (mfn/testing "includes organization header if provided in config"
    (http/get! 'path nil (merge config {:org-id "org-id"}) nil)
    (mfn/providing
      (client/get (mockfn.matchers/any)
                  {:headers {"Authorization"       "Bearer api-key"
                             "OpenAI-Organization" "org-id"}})
      {:status 200 :body "{}"})))

(def ^:private config+http-opts
  (merge config {:http-opts {:connection-timeout 2500
                             :socket-timeout 2500}}))

(mfn/deftest config-http-opts-test
  (mfn/testing "includes timeout settings"
    (http/get! 'path nil config+http-opts nil)
    (mfn/providing
      (client/get (mockfn.matchers/any)
                  {:headers            {"Authorization" "Bearer api-key"}
                   :connection-timeout 2500
                   :socket-timeout     2500})
      {:status 200 :body "{}"}))

  (mfn/testing "ignores unknown settings"
    (http/get! 'path nil config {:http-opts {:unknown "unknown"}})
    (mfn/providing
      (client/get (mockfn.matchers/any)
                  {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{}"})))

(mfn/deftest params-http-opts-test
  (mfn/testing "includes format settings"
    (http/post! 'path {:http-opts {:as :stream}} config nil)
    (mfn/providing
      (client/post (mockfn.matchers/any)
                   {:headers {"Authorization" "Bearer api-key"}
                    :as      :stream})
      {:status 200 :body "{}"}))

  (mfn/testing "ignores unknown settings"
    (http/post! 'path {:http-opts {:unknown :unknown}} config nil)
    (mfn/providing
      (client/post (mockfn.matchers/any)
                   {:headers {"Authorization" "Bearer api-key"}})
      {:status 200 :body "{}"})))

(mfn/deftest streaming-test
  (mfn/testing "returns the response as a stream of smaller responses"
    (is (= [{:pt 1} {:pt 2}]
           (http/post! 'path {} config nil)))
    (mfn/providing
      (client/post "base-url/path" (mockfn.matchers/any))
      {:status 200
       :body   (->> "data: {\"pt\":1}\n\ndata: {\"pt\":2}\n\ndata: [DONE]"
                    .getBytes ByteArrayInputStream.)})))
