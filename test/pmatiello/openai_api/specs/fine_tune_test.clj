(ns pmatiello.openai-api.specs.fine-tune-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.fine-tune :as specs.fine-tune]))

(def ^:private description
  {:training-files   [{:object         "file"
                       :id             "file-DWRV2BYz7oRbodlyTjjqeNE9"
                       :purpose        "fine-tune"
                       :filename       "colors.txt"
                       :bytes          282
                       :created-at     1681918923
                       :status         "processed"
                       :status-details nil}]
   :updated-at       1681919472
   :events           [{:object     "fine-tune-event"
                       :level      "info"
                       :message    "Created fine-tune: ft-kFBYQDkRjwK0FlqvkPsZdzKC"
                       :created-at 1681919377}
                      {:object  "fine-tune-event"
                       :level   "info"
                       :message "Fine-tune costs $0.00" :created-at 1681919389}
                      {:object     "fine-tune-event"
                       :level      "info"
                       :message    "Fine-tune is in the queue. Queue number: 7"
                       :created-at 1681919472}]
   :organization-id  "org-5FTsTyevVVRw0XwK49sdK0G9"
   :hyperparams      {:n-epochs                 4
                      :batch-size               1
                      :prompt-loss-weight       0.01
                      :learning-rate-multiplier 0.1}
   :result-files     []
   :status           "pending"
   :id               "ft-kFBYQDkRjwK0FlqvkPsZdzKC"
   :validation-files []
   :created-at       1681919377
   :object           "fine-tune"
   :fine-tuned-model nil
   :model            "ada"})

(def ^:private description-list
  {:object "list"
   :data   [description]})

(def ^:private create-params
  {:training-file "file-DWRV2BYz7oRbodlyTjjqeNE9"
   :model         "ada"})

(def ^:private delete-result
  {:id      "ada:ft-personal-2023-04-19-16-14-34"
   :object  "model"
   :deleted true})

(deftest description-test
  (s/valid? ::specs.fine-tune/description description))

(deftest description-list-test
  (s/valid? ::specs.fine-tune/description-list description-list))

(deftest create-params-test
  (s/valid? ::specs.fine-tune/create-params create-params))

(deftest delete-result-test
  (s/valid? ::specs.fine-tune/delete-result delete-result))
