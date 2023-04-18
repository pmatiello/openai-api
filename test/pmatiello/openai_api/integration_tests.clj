(ns pmatiello.openai-api.integration-tests
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]
            [pmatiello.openai-api.api :as api]))

(stest/instrument)

(def ^:private api-key
  (System/getenv "OPENAI_API_KEY"))

(def ^:private credentials
  (api/credentials api-key))

(deftest credentials-test
  (is (s/valid?
        :pmatiello.openai-api.specs.credentials/credentials
        (api/credentials api-key))))

(deftest fine-tunes-test
  (testing "setup"
    (doseq [each (->> credentials api/files :data (map :id))]
      (api/file-delete! each credentials))

    (doseq [each (->> credentials api/models :data
                      (filter #(->> % :owned-by (re-matches #"user-.*"))) (map :id))]
      (api/fine-tune-delete! each credentials))

    (api/file-upload! {:file    (io/file "test/fixtures/colors.txt")
                       :purpose "fine-tune"}
                      credentials)

    (while
      (->> credentials api/files :data (map :status) (every? #{"processed"}) not)
      (Thread/sleep 1000)))

  (testing "fine-tune-create!"
    (is (s/valid?
          :pmatiello.openai-api.specs.fine-tune/result
          (api/fine-tune-create!
            {:training-file (->> credentials api/files :data first :id)
             :model         "ada"}
            credentials))))

  (testing "fine-tunes"
    (is (s/valid?
          :pmatiello.openai-api.specs.fine-tune/result-list
          (api/fine-tunes credentials))))

  (while
    (->> credentials api/fine-tunes :data (map :status)
         (every? #{"succeeded" "failed"}) not)
    (Thread/sleep 1000))

  (testing "fine-tune"
    (is (s/valid?
          :pmatiello.openai-api.specs.fine-tune/result
          (api/fine-tune
            (-> credentials api/fine-tunes :data first :id)
            credentials))))

  (testing "fine-tune-delete!"
    (is (s/valid?
          :pmatiello.openai-api.specs.fine-tune/delete-result
          (api/fine-tune-delete!
            (->> credentials api/models :data
                 (filter #(->> % :owned-by (re-matches #"user-.*"))) first :id)
            credentials)))))
