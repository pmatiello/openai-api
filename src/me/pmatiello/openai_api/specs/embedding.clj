(ns me.pmatiello.openai-api.specs.embedding
  (:require [clojure.spec.alpha :as s]))

(s/def ::input string?)
(s/def ::model string?)

(s/def ::user string?)

(s/def ::params
  (s/keys :req-un [::input ::model]
          :opt-un [::user]))

(s/def ::embedding
  (s/coll-of number?))
(s/def ::index integer?)
(s/def ::object string?)

(s/def ::data*
  (s/keys :req-un [::embedding ::index ::object]))

(s/def ::data
  (s/coll-of ::data*))

(s/def ::prompt-tokens integer?)
(s/def ::total-tokens integer?)

(s/def ::usage
  (s/keys :req-un [::prompt-tokens ::total-tokens]))

(s/def ::result
  (s/keys :req-un [::data ::model ::object ::usage]))
