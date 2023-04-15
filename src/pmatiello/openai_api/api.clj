(ns pmatiello.openai-api.api
  (:require [clojure.spec.alpha :as s]
            [pmatiello.openai-api.internal.http :as http]
            [pmatiello.openai-api.specs.audio :as specs.audio]
            [pmatiello.openai-api.specs.chat :as specs.chat]
            [pmatiello.openai-api.specs.completion :as specs.completion]
            [pmatiello.openai-api.specs.credentials :as specs.credentials]
            [pmatiello.openai-api.specs.edit :as specs.edit]
            [pmatiello.openai-api.specs.embedding :as specs.embedding]
            [pmatiello.openai-api.specs.file :as specs.file]
            [pmatiello.openai-api.specs.image :as specs.image]
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
              {:body params} credentials))

(s/fdef completion
  :args (s/cat :params ::specs.completion/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.completion/result)

(defn chat
  [params credentials]
  (http/post! "https://api.openai.com/v1/chat/completions"
              {:body params} credentials))

(s/fdef chat
  :args (s/cat :params ::specs.chat/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.chat/result)

(defn edit
  [params credentials]
  (http/post! "https://api.openai.com/v1/edits"
              {:body params} credentials))

(s/fdef edit
  :args (s/cat :params ::specs.edit/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.edit/result)

(defn image-generation
  [params credentials]
  (http/post! "https://api.openai.com/v1/images/generations"
              {:body params} credentials))

(s/fdef image-generation
  :args (s/cat :params ::specs.image/generation-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.image/result)

(defn image-edit
  [params credentials]
  (http/post! "https://api.openai.com/v1/images/edits"
              {:multipart params} credentials))

(s/fdef image-edit
  :args (s/cat :params ::specs.image/edit-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.image/result)

(defn image-variation
  [params credentials]
  (http/post! "https://api.openai.com/v1/images/variations"
              {:multipart params} credentials))

(s/fdef image-variation
  :args (s/cat :params ::specs.image/variation-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.image/result)

(defn embedding
  [params credentials]
  (http/post! "https://api.openai.com/v1/embeddings"
              {:body params} credentials))

(s/fdef embedding
  :args (s/cat :params ::specs.embedding/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.embedding/result)

(defn audio-transcription
  [params credentials]
  (http/post! "https://api.openai.com/v1/audio/transcriptions"
              {:multipart params} credentials))

(s/fdef audio-transcription
  :args (s/cat :params ::specs.audio/transcription-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.audio/result)

(defn audio-translation
  [params credentials]
  (http/post! "https://api.openai.com/v1/audio/translations"
              {:multipart params} credentials))

(s/fdef audio-translation
  :args (s/cat :params ::specs.audio/translation-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.audio/result)

(defn files
  [credentials]
  (http/get! "https://api.openai.com/v1/files"
             credentials))

(s/fdef files
  :args (s/cat :credentials ::specs.credentials/credentials)
  :ret ::specs.file/result-list)

(defn file-upload!
  [params credentials]
  (http/post! "https://api.openai.com/v1/files"
              {:multipart params} credentials))

(s/fdef file-upload!
  :args (s/cat :params ::specs.file/upload-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.file/upload-result)

(defn file-delete!
  [id credentials]
  (http/delete! (str "https://api.openai.com/v1/files/" id)
                credentials))

(s/fdef file-delete!
  :args (s/cat :id ::specs.file/id
               :credentials ::specs.credentials/credentials)
  :ret ::specs.file/delete-result)
