(ns
  ^{:doc "This namespace provides a wrapper around the OpenAI API, offering various
          functions for interacting with the API's capabilities. These include text
          generation, image generation and editing, embeddings, audio transcription
          and translation, file management, fine-tuning, and content moderation.

          Refer to the function specs and the official OpenAI documentation for
          details about the parameters required for these functions."}
  me.pmatiello.openai-api.api
  (:require [clojure.spec.alpha :as s]
            [me.pmatiello.openai-api.internal.http :as http]
            [me.pmatiello.openai-api.specs.audio :as specs.audio]
            [me.pmatiello.openai-api.specs.chat :as specs.chat]
            [me.pmatiello.openai-api.specs.completion :as specs.completion]
            [me.pmatiello.openai-api.specs.config :as specs.config]
            [me.pmatiello.openai-api.specs.moderation :as specs.moderation]
            [me.pmatiello.openai-api.specs.edit :as specs.edit]
            [me.pmatiello.openai-api.specs.embedding :as specs.embedding]
            [me.pmatiello.openai-api.specs.file :as specs.file]
            [me.pmatiello.openai-api.specs.fine-tune :as specs.fine-tune]
            [me.pmatiello.openai-api.specs.image :as specs.image]
            [me.pmatiello.openai-api.specs.model :as specs.model]))

(defn config
  "Create a config map for accessing the API."
  [& {:as params}]
  (merge {:base-url "https://api.openai.com"} params))

(s/fdef config
  :args (s/cat :params ::specs.config/params)
  :ret ::specs.config/config)

(defn models
  "Retrieve the list of available models."
  [config]
  (http/get! "/v1/models"
             config))

(s/fdef models
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.model/description-list)

(defn model
  "Retrieve the details of a specific model by its id."
  [id config]
  (http/get! (str "/v1/models/" (name id))
             config))

(s/fdef model
  :args (s/cat :id ::specs.model/id
               :config ::specs.config/config)
  :ret ::specs.model/description)

(defn completion
  "Generate a completion based on the given parameters."
  [params config]
  (http/post! "/v1/completions"
              {:body params} config))

(s/fdef completion
  :args (s/cat :params ::specs.completion/params
               :config ::specs.config/config)
  :ret ::specs.completion/result)

(defn chat
  "Generate a chat completion based on the given parameters."
  [params config]
  (http/post! "/v1/chat/completions"
              {:body params} config))

(s/fdef chat
  :args (s/cat :params ::specs.chat/params
               :config ::specs.config/config)
  :ret ::specs.chat/result)

(defn edit
  "Generate an edit based on the given parameters."
  [params config]
  (http/post! "/v1/edits"
              {:body params} config))

(s/fdef edit
  :args (s/cat :params ::specs.edit/params
               :config ::specs.config/config)
  :ret ::specs.edit/result)

(defn image-generation
  "Generate an image based on the given parameters."
  [params config]
  (http/post! "/v1/images/generations"
              {:body params} config))

(s/fdef image-generation
  :args (s/cat :params ::specs.image/generation-params
               :config ::specs.config/config)
  :ret ::specs.image/result)

(defn image-edit
  "Edit an image based on the given parameters."
  [params config]
  (http/post! "/v1/images/edits"
              {:multipart params} config))

(s/fdef image-edit
  :args (s/cat :params ::specs.image/edit-params
               :config ::specs.config/config)
  :ret ::specs.image/result)

(defn image-variation
  "Generate image variations based on the given parameters."
  [params config]
  (http/post! "/v1/images/variations"
              {:multipart params} config))

(s/fdef image-variation
  :args (s/cat :params ::specs.image/variation-params
               :config ::specs.config/config)
  :ret ::specs.image/result)

(defn embedding
  "Generate an embedding based on the given parameters."
  [params config]
  (http/post! "/v1/embeddings"
              {:body params} config))

(s/fdef embedding
  :args (s/cat :params ::specs.embedding/params
               :config ::specs.config/config)
  :ret ::specs.embedding/result)

(defn audio-transcription
  "Transcribe audio based on the given parameters."
  [params config]
  (http/post! "/v1/audio/transcriptions"
              {:multipart params} config))

(s/fdef audio-transcription
  :args (s/cat :params ::specs.audio/transcription-params
               :config ::specs.config/config)
  :ret ::specs.audio/result)

(defn audio-translation
  "Translate audio based on the given parameters."
  [params config]
  (http/post! "/v1/audio/translations"
              {:multipart params} config))

(s/fdef audio-translation
  :args (s/cat :params ::specs.audio/translation-params
               :config ::specs.config/config)
  :ret ::specs.audio/result)

(defn files
  "Retrieve the list of files associated with the provided config."
  [config]
  (http/get! "/v1/files"
             config))

(s/fdef files
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.file/description-list)

(defn file
  "Retrieve the details of a specific file by its id."
  [id config]
  (http/get! (str "/v1/files/" id)
             config))

(s/fdef file
  :args (s/cat :id ::specs.file/id
               :config ::specs.config/config)
  :ret ::specs.file/result)

(defn file-content
  "Retrieve the content of a specific file by its id."
  [id config]
  (http/get! (str "/v1/files/" id "/content")
             config {:parse? false}))

(s/fdef file-content
  :args (s/cat :id ::specs.file/id
               :config ::specs.config/config)
  :ret string?)

(defn file-upload!
  "Upload a file with the provided parameters."
  [params config]
  (http/post! "/v1/files"
              {:multipart params} config))

(s/fdef file-upload!
  :args (s/cat :params ::specs.file/upload-params
               :config ::specs.config/config)
  :ret ::specs.file/upload-result)

(defn file-delete!
  "Delete a specific file by its id."
  [id config]
  (http/delete! (str "/v1/files/" id)
                config))

(s/fdef file-delete!
  :args (s/cat :id ::specs.file/id
               :config ::specs.config/config)
  :ret ::specs.file/delete-result)

(defn fine-tunes
  "Retrieve the list of fine-tunes associated with the provided config."
  [config]
  (http/get! "/v1/fine-tunes" config))

(s/fdef fine-tunes
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.fine-tune/description-list)

(defn fine-tune
  "Retrieve the details of a specific fine-tune by its id."
  [id config]
  (http/get! (str "/v1/fine-tunes/" (name id))
             config))

(s/fdef fine-tune
  :args (s/cat :id ::specs.fine-tune/id
               :config ::specs.config/config)
  :ret ::specs.fine-tune/description)

(defn fine-tune-events
  "Retrieve the list of events for a specific fine-tune by its id."
  [id config]
  (http/get! (str "/v1/fine-tunes/" id "/events")
             config))

(s/fdef fine-tune-events
  :args (s/cat :id ::specs.fine-tune/id
               :config ::specs.config/config)
  :ret ::specs.fine-tune/event-list)

(defn fine-tune-create!
  "Create a new fine-tune with the provided parameters."
  [params config]
  (http/post! "/v1/fine-tunes"
              {:body params} config))

(s/fdef fine-tune-create!
  :args (s/cat :params ::specs.fine-tune/create-params
               :config ::specs.config/config)
  :ret ::specs.fine-tune/description)

(defn fine-tune-cancel!
  "Cancel a specific fine-tune by its id."
  [id config]
  (http/post! (str "/v1/fine-tunes/" id "/cancel")
              {} config))

(s/fdef fine-tune-cancel!
  :args (s/cat :id ::specs.fine-tune/id
               :config ::specs.config/config)
  :ret ::specs.fine-tune/description)

(defn fine-tune-delete!
  "Delete a specific fine-tuned model by its id."
  [model-id config]
  (http/delete! (str "/v1/models/" model-id) config))

(s/fdef fine-tune-delete!
  :args (s/cat :model ::specs.fine-tune/model
               :config ::specs.config/config)
  :ret ::specs.fine-tune/delete-result)

(defn moderation
  "Perform content moderation with the provided parameters."
  [params config]
  (http/post! "/v1/moderations"
              {:body params} config))

(s/fdef moderation
  :args (s/cat :params ::specs.moderation/params
               :config ::specs.config/config)
  :ret ::specs.moderation/classification)
