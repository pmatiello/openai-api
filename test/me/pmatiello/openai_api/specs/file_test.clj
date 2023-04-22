(ns me.pmatiello.openai-api.specs.file-test
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [me.pmatiello.openai-api.specs.file :as specs.file]))

(def ^:private description
  {:object         "file"
   :id             "file-gHPocKviwaGsR30QYXTZBW7F"
   :purpose        "fine-tune"
   :filename       "colors.txt"
   :bytes          282
   :created-at     1681861371
   :status         "processed"
   :status-details nil})

(def ^:private description-list
  {:object "list"
   :data   [description]})

(def ^:private upload-params
  {:file    (io/file "test/fixtures/colors.txt")
   :purpose "fine-tune"})

(def ^:private upload-result
  {:object         "file"
   :id             "file-gHPocKviwaGsR30QYXTZBW7F"
   :purpose        "fine-tune"
   :filename       "colors.txt"
   :bytes          282
   :created-at     1681861371
   :status         "uploaded"
   :status-details nil})

(def ^:private delete-result
  {:object  "file"
   :id      "file-gHPocKviwaGsR30QYXTZBW7F"
   :deleted true})

(deftest description-test
  (is (s/valid? ::specs.file/description description)))

(deftest description-list-test
  (is (s/valid? ::specs.file/description-list description-list)))

(deftest upload-params-test
  (is (s/valid? ::specs.file/upload-params upload-params)))

(deftest upload-result-test
  (is (s/valid? ::specs.file/upload-result upload-result)))

(deftest delete-result-test
  (is (s/valid? ::specs.file/delete-result delete-result)))
