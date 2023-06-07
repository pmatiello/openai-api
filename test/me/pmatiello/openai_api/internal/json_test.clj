(ns me.pmatiello.openai-api.internal.json-test
  (:require [clojure.test :refer :all]
            [me.pmatiello.openai-api.internal.json :as json]))

(deftest read-test
  (testing "reads json value"
    (is (= {:key1 "value" :key2 1.0}
           (json/read "{\"key1\":\"value\",\"key2\":1.0}"))))

  (testing "converts underscores to dashes in keys"
    (is (= {:a-key "some_value"}
           (json/read "{\"a_key\":\"some_value\"}")))))

(deftest write-test
  (testing "writes json value"
    (is (= "{\"key1\":\"value\",\"key2\":1.0}"
           (json/write {:key1 "value" :key2 1.0}))))

  (testing "convertes dashes to underscores in keys"
    (is (= "{\"a_key\":\"some_value\"}"
           (json/write {:a-key "some_value"})))))
