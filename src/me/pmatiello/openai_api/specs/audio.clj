(ns me.pmatiello.openai-api.specs.audio
  (:require [clojure.spec.alpha :as s])
  (:import (java.io File)))

(s/def ::file #(instance? File %))
(s/def ::model string?)

(s/def ::language string?)
(s/def ::prompt string?)
(s/def ::response-format string?)
(s/def ::temperature number?)

(s/def ::transcription-params
  (s/keys :req-un [::file ::model]
          :opt-un [::language ::prompt ::response-format ::temperature]))

(s/def ::translation-params
  (s/keys :req-un [::file ::model]
          :opt-un [::prompt ::response-format ::temperature]))

(s/def ::text string?)

(s/def ::result
  (s/keys :req-un [::text]))
