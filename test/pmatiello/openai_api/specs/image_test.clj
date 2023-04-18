(ns pmatiello.openai-api.specs.image-test
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.image :as specs.image]))

(def ^:private generation-params
  {:prompt "brick wall" :response-format "url"})

(def ^:private edit-params
  {:image           (io/file "test/fixtures/image.png")
   :mask            (io/file "test/fixtures/mask.png")
   :prompt          "brick wall with a graffiti"
   :response-format "url"})

(def ^:private variation-params
  {:image (io/file "test/fixtures/image.png")})

(def ^:private result
  {:created 1681859884,
   :data    [{:url "https://oaidalleapiprodscus.blob.core.windows.net/private/..."}]})

(deftest generation-params-test
  (is (s/valid? ::specs.image/generation-params generation-params)))

(deftest edit-params-test
  (is (s/valid? ::specs.image/edit-params edit-params)))

(deftest variation-params-test
  (is (s/valid? ::specs.image/variation-params variation-params)))

(deftest result-test
  (is (s/valid? ::specs.image/result result)))
