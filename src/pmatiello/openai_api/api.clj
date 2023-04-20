(ns pmatiello.openai-api.api
  ^{:doc "This namespace provides a wrapper around the OpenAI API, offering various
  functions for interacting with the API's capabilities. These include text
  generation, image generation and editing, embeddings, audio transcription and
  translation, file management, fine-tuning, and content moderation.

  Refer to the function specs and the official OpenAI documentation for details about
  the parameters required for these functions."}
  (:require [clojure.spec.alpha :as s]
            [pmatiello.openai-api.internal.http :as http]
            [pmatiello.openai-api.specs.audio :as specs.audio]
            [pmatiello.openai-api.specs.chat :as specs.chat]
            [pmatiello.openai-api.specs.completion :as specs.completion]
            [pmatiello.openai-api.specs.credentials :as specs.credentials]
            [pmatiello.openai-api.specs.moderation :as specs.moderation]
            [pmatiello.openai-api.specs.edit :as specs.edit]
            [pmatiello.openai-api.specs.embedding :as specs.embedding]
            [pmatiello.openai-api.specs.file :as specs.file]
            [pmatiello.openai-api.specs.fine-tune :as specs.fine-tune]
            [pmatiello.openai-api.specs.image :as specs.image]
            [pmatiello.openai-api.specs.model :as specs.model]))

(defn credentials
  "Create a credentials map with the given API key and an optional organization id."
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
  "Retrieve the list of available models."
  [credentials]
  (http/get! "https://api.openai.com/v1/models"
             credentials))

(s/fdef models
  :args (s/cat :credentials ::specs.credentials/credentials)
  :ret ::specs.model/description-list)

(defn model
  "Retrieve the details of a specific model by its id."
  [id credentials]
  (http/get! (str "https://api.openai.com/v1/models/" (name id))
             credentials))

(s/fdef model
  :args (s/cat :id ::specs.model/id
               :credentials ::specs.credentials/credentials)
  :ret ::specs.model/description)

(defn completion
  "Generate a completion based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/completions"
              {:body params} credentials))

(s/fdef completion
  :args (s/cat :params ::specs.completion/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.completion/result)

(defn chat
  "Generate a chat completion based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/chat/completions"
              {:body params} credentials))

(s/fdef chat
  :args (s/cat :params ::specs.chat/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.chat/result)

(defn edit
  "Generate an edit based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/edits"
              {:body params} credentials))

(s/fdef edit
  :args (s/cat :params ::specs.edit/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.edit/result)

(defn image-generation
  "Generate an image based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/images/generations"
              {:body params} credentials))

(s/fdef image-generation
  :args (s/cat :params ::specs.image/generation-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.image/result)

(defn image-edit
  "Edit an image based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/images/edits"
              {:multipart params} credentials))

(s/fdef image-edit
  :args (s/cat :params ::specs.image/edit-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.image/result)

(defn image-variation
  "Generate image variations based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/images/variations"
              {:multipart params} credentials))

(s/fdef image-variation
  :args (s/cat :params ::specs.image/variation-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.image/result)

(defn embedding
  "Generate an embedding based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/embeddings"
              {:body params} credentials))

(s/fdef embedding
  :args (s/cat :params ::specs.embedding/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.embedding/result)

(defn audio-transcription
  "Transcribe audio based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/audio/transcriptions"
              {:multipart params} credentials))

(s/fdef audio-transcription
  :args (s/cat :params ::specs.audio/transcription-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.audio/result)

(defn audio-translation
  "Translate audio based on the given parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/audio/translations"
              {:multipart params} credentials))

(s/fdef audio-translation
  :args (s/cat :params ::specs.audio/translation-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.audio/result)

(defn files
  "Retrieve the list of files associated with the provided credentials."
  [credentials]
  (http/get! "https://api.openai.com/v1/files"
             credentials))

(s/fdef files
  :args (s/cat :credentials ::specs.credentials/credentials)
  :ret ::specs.file/description-list)

(defn file
  "Retrieve the details of a specific file by its id."
  [id credentials]
  (http/get! (str "https://api.openai.com/v1/files/" id)
             credentials))

(s/fdef file
  :args (s/cat :id ::specs.file/id
               :credentials ::specs.credentials/credentials)
  :ret ::specs.file/result)

(defn file-content
  "Retrieve the content of a specific file by its id."
  [id credentials]
  (http/get! (str "https://api.openai.com/v1/files/" id "/content")
             credentials {:parse? false}))

(s/fdef file-content
  :args (s/cat :id ::specs.file/id
               :credentials ::specs.credentials/credentials)
  :ret string?)

(defn file-upload!
  "Upload a file with the provided parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/files"
              {:multipart params} credentials))

(s/fdef file-upload!
  :args (s/cat :params ::specs.file/upload-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.file/upload-result)

(defn file-delete!
  "Delete a specific file by its id."
  [id credentials]
  (http/delete! (str "https://api.openai.com/v1/files/" id)
                credentials))

(s/fdef file-delete!
  :args (s/cat :id ::specs.file/id
               :credentials ::specs.credentials/credentials)
  :ret ::specs.file/delete-result)

(defn fine-tunes
  "Retrieve the list of fine-tunes associated with the provided credentials."
  [credentials]
  (http/get! "https://api.openai.com/v1/fine-tunes" credentials))

(s/fdef fine-tunes
  :args (s/cat :credentials ::specs.credentials/credentials)
  :ret ::specs.fine-tune/description-list)

(defn fine-tune
  "Retrieve the details of a specific fine-tune by its id."
  [id credentials]
  (http/get! (str "https://api.openai.com/v1/fine-tunes/" (name id))
             credentials))

(s/fdef fine-tune
  :args (s/cat :id ::specs.fine-tune/id
               :credentials ::specs.credentials/credentials)
  :ret ::specs.fine-tune/description)

(defn fine-tune-events
  "Retrieve the list of events for a specific fine-tune by its id."
  [id credentials]
  (http/get! (str "https://api.openai.com/v1/fine-tunes/" id "/events")
             credentials))

(s/fdef fine-tune-events
  :args (s/cat :id ::specs.fine-tune/id
               :credentials ::specs.credentials/credentials)
  :ret ::specs.fine-tune/event-list)

(defn fine-tune-create!
  "Create a new fine-tune with the provided parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/fine-tunes"
              {:body params} credentials))

(s/fdef fine-tune-create!
  :args (s/cat :params ::specs.fine-tune/create-params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.fine-tune/description)

(defn fine-tune-cancel!
  "Cancel a specific fine-tune by its id."
  [id credentials]
  (http/post! (str "https://api.openai.com/v1/fine-tunes/" id "/cancel")
              {} credentials))

(s/fdef fine-tune-cancel!
  :args (s/cat :id ::specs.fine-tune/id
               :credentials ::specs.credentials/credentials)
  :ret ::specs.fine-tune/description)

(defn fine-tune-delete!
  "Delete a specific fine-tuned model by its id."
  [model-id credentials]
  (http/delete! (str "https://api.openai.com/v1/models/" model-id) credentials))

(s/fdef fine-tune-delete!
  :args (s/cat :model ::specs.fine-tune/model
               :credentials ::specs.credentials/credentials)
  :ret ::specs.fine-tune/delete-result)

(defn moderation
  "Perform content moderation with the provided parameters."
  [params credentials]
  (http/post! "https://api.openai.com/v1/moderations"
              {:body params} credentials))

(s/fdef moderation
  :args (s/cat :params ::specs.moderation/params
               :credentials ::specs.credentials/credentials)
  :ret ::specs.moderation/classification)
