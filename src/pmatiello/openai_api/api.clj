(ns pmatiello.openai-api.api
  (:require [clojure.spec.alpha :as s]
            [pmatiello.openai-api.internal.http :as http]
            [pmatiello.openai-api.specs.chat :as specs.chat]
            [pmatiello.openai-api.specs.completion :as specs.completion]
            [pmatiello.openai-api.specs.credentials :as specs.credentials]
            [pmatiello.openai-api.specs.edits :as specs.edits]
            [pmatiello.openai-api.specs.model :as specs.model]))

(defn credentials
  ([api-key]
   {:api-key api-key})
  ([api-key org-id]
   {:api-key api-key
    :org-id  org-id}))

(s/fdef credentials
  :args (s/cat :api-key ::specs.credentials/api-key
               :org-id (s/? ::specs.credentials/org-id))
  :ret ::specs.credentials/credentials)

(defn models
  [credentials]
  (http/get! "https://api.openai.com/v1/models"
             credentials))

(s/fdef models
  :args (s/cat :credentials ::specs.credentials/credentials)
  :ret ::specs.model/result-list)

(defn model
  [model credentials]
  (http/get! (str "https://api.openai.com/v1/models/" (name model))
             credentials))

(s/fdef model
  :args (s/cat :model ::specs.model/model
               :credentials ::specs.credentials/credentials)
  :ret ::specs.model/result)

(defn completion
  [params credentials]
  (http/post! "https://api.openai.com/v1/completions"
              params credentials))

(s/fdef completion
  :args (s/cat :params ::specs.completion/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.completion/result)

(defn chat
  [params credentials]
  (http/post! "https://api.openai.com/v1/chat/completions"
              params credentials))

(s/fdef chat
  :args (s/cat :params ::specs.chat/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.chat/result)

(defn edits
  [params credentials]
  (http/post! "https://api.openai.com/v1/edits"
              params credentials))

(s/fdef edits
  :args (s/cat :params ::specs.edits/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.edits/result)
