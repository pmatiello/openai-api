(ns pmatiello.openai-api.specs.credentials
  (:require [clojure.spec.alpha :as s]))

(s/def ::api-key string?)
(s/def ::org-id string?)
(s/def ::credentials
  (s/keys :req-un [::api-key]
          :opt-un [::org-id]))
