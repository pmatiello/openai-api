(ns me.pmatiello.openai-api.specs.fine-tuning-jobs
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::after ::id)
(s/def ::limit integer?)
(s/def ::params
  (s/keys :opt-un [::after ::limit]))


(s/def ::created-at integer?)
(s/def ::fine-tuned-model
  (s/nilable string?))
(s/def ::finished-at
  (s/nilable integer?))
(s/def ::model string?)
(s/def ::object string?)
(s/def ::organization-id string?)
(s/def ::result-files
  (s/coll-of string?))
(s/def ::status string?)
(s/def ::trained-tokens
  (s/nilable integer?))
(s/def ::training-file string?)
(s/def ::validation-file
  (s/nilable string?))

(s/def ::code string?)
(s/def ::message string?)
(s/def ::param string?)
(s/def ::error*
  (s/keys :req-un [::code ::message ::param]))
(s/def ::error
  (s/nilable ::error*))

(s/def ::hyperparam (s/or :integer integer? :string string?))
(s/def ::batch-size ::hyperparam)
(s/def ::learning-rate-multiplier ::hyperparam)
(s/def ::n-epochs ::hyperparam)
(s/def ::hyperparams
  (s/keys :req-un [::batch-size ::learning-rate-multiplier ::n-epochs]))

(s/def ::data*
  (s/keys :req-un [::created-at ::error ::fine-tuned-model ::finished-at
                   ::hyperparameters ::id ::model ::object ::organization-id
                   ::result-files ::status ::trained-tokens ::training-file
                   ::validation-file]))
(s/def ::data
  (s/coll-of ::data*))

(s/def ::description ::data*)

(s/def ::has-more boolean?)

(s/def ::description-list
  (s/keys :req-un [::data ::has-more ::object]))

(s/def ::suffix
  (s/nilable string?))

(s/def ::create-params
  (s/keys :req-un [::model ::training-file]
          :opt-un [::hyperparameters ::suffix ::validation-file]))
