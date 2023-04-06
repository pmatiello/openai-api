(ns pmatiello.openai-api.api
  (:require [clojure.spec.alpha :as s]
            [pmatiello.openai-api.internal.common :as common]
            [pmatiello.openai-api.internal.http :as http]))

(s/def ::model keyword?)
(s/def ::completion-params
  (s/keys :req-un [::model]))
(s/def ::chat-params
  (s/keys :req-un [::model]))

(defn credentials
  ([api-key]
   {:api-key api-key})
  ([api-key org-id]
   {:api-key api-key
    :org-id  org-id}))

(s/fdef credentials
  :args (s/cat :api-key ::common/api-key :org-id (s/? ::common/org-id))
  :ret ::common/credentials)

(defn models
  [credentials]
  (http/get! "https://api.openai.com/v1/models"
             credentials))

(s/fdef models
  :args (s/cat :credentials ::common/credentials))

(defn model
  [model credentials]
  (http/get! (str "https://api.openai.com/v1/models/" (name model))
             credentials))

(s/fdef model
  :args (s/cat :model ::model :credentials ::common/credentials))

(defn completion
  [params credentials]
  (http/post! "https://api.openai.com/v1/completions"
              params credentials))

(s/fdef completion
  :args (s/cat :params ::completion-params :credentials ::common/credentials))

(defn chat
  [params credentials]
  (http/post! "https://api.openai.com/v1/chat/completions"
              params credentials))

(s/fdef chat
  :args (s/cat :params ::chat-params :credentials ::common/credentials))
