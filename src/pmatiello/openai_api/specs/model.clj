(ns pmatiello.openai-api.specs.model
  (:require [clojure.spec.alpha :as s]))

(s/def ::created number?)
(s/def ::id string?)
(s/def ::object string?)
(s/def ::owned-by string?)
(s/def ::parent any?)
(s/def ::root string?)

(s/def ::permission* map?)
(s/def ::permission
  (s/coll-of ::permission*))

(s/def ::description
  (s/keys :req-un [::created ::id ::object ::owned-by ::parent ::permission ::root]))

(s/def ::data
  (s/coll-of ::description))
(s/def ::description-list
  (s/keys :req-un [::data ::object]))
