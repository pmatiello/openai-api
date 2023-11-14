(ns ^:deprecated me.pmatiello.openai-api.specs.edit
  (:require [clojure.spec.alpha :as s]))

(s/def ::instruction string?)
(s/def ::model string?)

(s/def ::input string?)
(s/def ::n integer?)
(s/def ::temperature number?)
(s/def ::top-p number?)

(s/def ::params
  (s/keys :req-un [::instruction ::model]
          :opt-un [::input ::n ::temperature ::top-p]))

(s/def ::created number?)
(s/def ::object string?)

(s/def ::index integer?)
(s/def ::text string?)
(s/def ::choice
  (s/keys :req-un [::index ::text]))
(s/def ::choices
  (s/coll-of ::choice))

(s/def ::completion-tokens integer?)
(s/def ::prompt-tokens integer?)
(s/def ::total-tokens integer?)
(s/def ::usage
  (s/keys ::req-un [::completion-tokens ::prompt-tokens ::total-tokens]))

(s/def ::result
  (s/keys :req-un [::choices ::created ::object ::usage]))
