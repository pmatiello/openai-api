(ns me.pmatiello.openai-api.specs.config
  (:require [clojure.spec.alpha :as s]))

(s/def ::api-key string?)
(s/def ::base-url string?)
(s/def ::org-id string?)

(s/def ::params
  (s/keys* :req-un [::api-key]
           :opt-un [::base-url ::org-id]))

(s/def ::config
  (s/keys :req-un [::api-key ::base-url]
          :opt-un [::org-id]))
