(ns pmatiello.openai-api.specs.fine-tune
  (:require [clojure.spec.alpha :as s]
            [pmatiello.openai-api.specs.file :as specs.file]))

(s/def ::training-file string?)

(s/def ::batch-size (s/nilable integer?))
(s/def ::classification-betas
  (s/coll-of number?))
(s/def ::classification-n-classes integer?)
(s/def ::classification-positive-class string?)
(s/def ::compute-classification-metrics boolean?)
(s/def ::learning-rate-multiplier (s/nilable number?))
(s/def ::model string?)
(s/def ::n-epochs integer?)
(s/def ::prompt-loss-weight number?)
(s/def ::suffix string?)
(s/def ::validation-file string?)

(s/def ::create-params
  (s/keys :req-un [::training-file]
          :opt-un [::batch-size ::classification-betas ::classification-n-classes
                   ::classification-positive-class ::compute-classification-metrics
                   ::learning-rate-multiplier ::model ::n-epochs ::prompt-loss-weight
                   ::suffix ::validation-file]))

(s/def ::created-at integer?)

(s/def ::level string?)
(s/def ::message string?)
(s/def ::object string?)
(s/def ::event
  (s/keys :req-un [::created-at ::level ::message ::object]))
(s/def ::events
  (s/coll-of ::event))

(s/def ::fine-tuned-model any?)

(s/def ::hyperparams
  (s/keys :req-un [::batch-size ::learning-rate-multiplier ::n-epochs
                   ::prompt-loss-weight]))

(s/def ::id string?)
(s/def ::organization-id string?)
(s/def ::result-files
  (s/coll-of ::specs.file/result))
(s/def ::status string?)
(s/def ::training-files
  (s/coll-of ::specs.file/result))
(s/def ::updated-at integer?)
(s/def ::validation-files
  (s/coll-of ::specs.file/result))

(s/def ::result
  (s/keys :req-un [::created-at ::events ::fine-tuned-model ::hyperparams ::id
                   ::model ::object ::organization-id ::result-files ::status
                   ::training-files ::updated-at ::validation-files]))

(s/def ::data*
  (s/keys :req-un [::created-at ::fine-tuned-model ::hyperparams ::id
                   ::model ::object ::organization-id ::result-files ::status
                   ::training-files ::updated-at ::validation-files]))
(s/def ::data
  (s/coll-of ::data*))

(s/def ::result-list
  (s/keys :req-un [::data ::object]))

(s/def ::deleted boolean?)

(s/def ::delete-result
  (s/keys :req-un [::deleted ::id ::object]))
