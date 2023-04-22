(ns me.pmatiello.openai-api.specs.chat
  (:require [clojure.spec.alpha :as s]))

(s/def ::model string?)

(s/def ::frequency-penalty number?)
(s/def ::logit-bias map?)
(s/def ::max-tokens integer?)
(s/def ::n integer?)
(s/def ::presence-penalty number?)
(s/def ::stop
  (s/or :string string?
        :array (s/coll-of string?)))
(s/def ::stream boolean?)
(s/def ::temperature number?)
(s/def ::top-p number?)
(s/def ::user string?)

(s/def ::content string?)
(s/def ::role #{"assistant" "system" "user"})
(s/def ::message
  (s/keys :req-un [::content ::role]))
(s/def ::messages
  (s/coll-of ::message))

(s/def ::params
  (s/keys :req-un [::model]
          :opt-un [::frequency-penalty ::logit-bias ::max-tokens ::messages ::n
                   ::presence-penalty ::stop ::stream ::temperature ::top-p ::user]))

(s/def ::created number?)
(s/def ::id string?)
(s/def ::object string?)

(s/def ::finish-reason string?)
(s/def ::index integer?)
(s/def ::choice
  (s/keys :req-un [::finish-reason ::index ::message]))
(s/def ::choices
  (s/coll-of ::choice))

(s/def ::completion-tokens integer?)
(s/def ::prompt-tokens integer?)
(s/def ::total-tokens integer?)
(s/def ::usage
  (s/keys ::req-un [::completion-tokens ::prompt-tokens ::total-tokens]))

(s/def ::result
  (s/keys :req-un [::choices ::created ::id ::model ::object ::usage]))
