(ns me.pmatiello.openai-api.specs.moderation
  (:require [clojure.spec.alpha :as s]))

(s/def ::input string?)

(s/def ::model string?)

(s/def ::params
  (s/keys :req-un [::input]
          :opt-un [::model]))

(s/def ::id string?)
(s/def ::model string?)

(s/def ::flagged boolean?)
(s/def ::categories
  (s/every-kv keyword? boolean?))
(s/def ::category-scores
  (s/every-kv keyword? number?))

(s/def ::result
  (s/keys :req-un [::categories ::category-scores ::flagged]))

(s/def ::results
  (s/coll-of ::result))

(s/def ::classification
  (s/keys :req-un [::id ::model ::results]))
