(ns me.pmatiello.openai-api.specs.completion
  (:require [clojure.spec.alpha :as s]))

(s/def ::model string?)

(s/def ::best-of integer?)
(s/def ::echo boolean?)
(s/def ::frequency-penalty number?)
(s/def ::logit-bias map?)
(s/def ::logprobs (s/nilable integer?))
(s/def ::max-tokens integer?)
(s/def ::n integer?)
(s/def ::presence-penalty number?)
(s/def ::prompt
  (s/or :string string?
        :array (s/coll-of string?)))
(s/def ::stop
  (s/or :string string?
        :array (s/coll-of string?)))
(s/def ::stream boolean?)
(s/def ::suffix string?)
(s/def ::temperature number?)
(s/def ::top-p number?)
(s/def ::user string?)

(s/def ::params
  (s/keys :req-un [::model]
          :opt-un [::best-of ::echo ::frequency-penalty ::logit-bias ::logprobs
                   ::max-tokens ::n ::presence-penalty ::prompt ::stop ::stream
                   ::suffix ::temperature ::top-p ::user]))

(s/def ::created number?)
(s/def ::id string?)
(s/def ::object string?)

(s/def ::finish-reason (s/nilable string?))
(s/def ::index integer?)
(s/def ::text string?)
(s/def ::choice
  (s/keys :req-un [::finish-reason ::index ::logprobs ::text]))
(s/def ::choices
  (s/coll-of ::choice))

(s/def ::completion-tokens integer?)
(s/def ::prompt-tokens integer?)
(s/def ::total-tokens integer?)
(s/def ::usage
  (s/keys ::req-un [::completion-tokens ::prompt-tokens ::total-tokens]))

(s/def ::result-full
  (s/keys :req-un [::choices ::created ::id ::model ::object ::usage]))

(s/def ::result-stream*
  (s/keys :req-un [::choices ::created ::id ::model ::object]))
(s/def ::result-stream
  (s/coll-of ::result-stream*))

(s/def ::result
  (s/or :result-full ::result-full
        :result-stream ::result-stream))
