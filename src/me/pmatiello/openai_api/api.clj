(ns
  ^{:doc "This namespace provides a wrapper around the OpenAI API, offering various
          functions for interacting with the API's capabilities. These include text
          generation, image generation and editing, embeddings, audio transcription
          and translation, file management, fine-tuning, and content moderation.

          Refer to the official OpenAI documentation for details about the parameters
          required for these functions.

          OpenAI API Reference: https://platform.openai.com/docs/api-reference"}
  me.pmatiello.openai-api.api
  (:require [clojure.spec.alpha :as s]
            [me.pmatiello.openai-api.internal.http :as http]
            [me.pmatiello.openai-api.specs.config :as specs.config]))

; config

(defn config
  "Creates a config map for accessing the API.

  The parameters are a map with the following keys:
  :api-key (required), :base-url and :org-id.

  An :http-opts entry is also accepted as a map with the following keys:
  :connection-timeout and :socket-timeout (both in msec).

  Example:
  (openai/config :api-key api-key)"
  [& {:as params}]
  (merge {:base-url "https://api.openai.com"} params))

(s/fdef config
  :args (s/cat :params ::specs.config/params)
  :ret ::specs.config/config)

; audio

(defn audio-transcription
  "Transcribes audio based on the given parameters.

  Example:
  (openai/audio-transcription
    {:model \"whisper-1\"
     :file  (io/file \"audio.m4a\")}
    config)"
  [params config]
  (http/post! "/v1/audio/transcriptions"
              {:multipart params} config))

(defn audio-translation
  "Translates audio based on the given parameters.

  Example:
  (openai/audio-translation
    {:model \"whisper-1\"
     :file  (io/file \"audio.m4a\")}
    config)"
  [params config]
  (http/post! "/v1/audio/translations"
              {:multipart params} config))

; chat

(defn ^:private params->http-params
  [params]
  (let [body      {:body params}
        http-opts (when (:stream params) {:http-opts {:as :stream}})]
    (merge body http-opts)))

(defn chat
  "Generates a chat completion based on the given parameters.

  Example:
  (openai/chat
    {:model    \"gpt-3.5-turbo\"
     :messages [{:role \"user\" :content \"hello\"}]}
    config)"
  [params config]
  (http/post! "/v1/chat/completions"
              (params->http-params params)
              config))

; embeddings

(defn embedding
  "Generates an embedding based on the given parameters.

  Example:
  (openai/embedding
    {:model \"text-embedding-ada-002\"
     :input \"hello\"}
    config)"
  [params config]
  (http/post! "/v1/embeddings"
              {:body params} config))

; fine-tuning

(defn fine-tuning-jobs
  "List fine-tuning jobs.

  Example:
  (openai/fine-tuning-jobs {} config)"
  [params config]
  (http/get! "/v1/fine_tuning/jobs" params config nil))

(defn fine-tuning-job
  "Retrieves the details of a specific fine-tuning by its id.

  Example:
  (openai/fine-tuning-job \"ft-id\" config)"
  [id config]
  (http/get! (str "/v1/fine_tuning/jobs/" (name id)) nil config nil))

(defn fine-tuning-jobs-create!
  "Creates a new fine tuning job with the provided parameters.

  Example:
  (openai/fine-tuning-jobs-create!
    {:training-file \"file-id\"
     :model         \"model\"}
    config)"
  [params config]
  (http/post! "/v1/fine_tuning/jobs"
              {:body params} config))

(defn fine-tuning-jobs-cancel!
  "Cancels a specific fine-tune by its id.

  Example:
  (openai/fine-tuning-jobs-cancel! \"ft-id\" config)"
  [id config]
  (http/post! (str "/v1/fine_tuning/jobs/" id "/cancel")
              {} config))

(defn fine-tuning-jobs-events
  [id params config]
  (http/get! (str "/v1/fine_tuning/jobs/" (name id) "/events")
             params config nil))

; files

(defn files
  "Retrieves the list of files associated with the provided config.

  Example:
  (openai/files config)"
  [config]
  (http/get! "/v1/files" nil config nil))

(defn file
  "Retrieves the details of a specific file by its id.

  Example:
  (openai/file \"file-id\" config)"
  [id config]
  (http/get! (str "/v1/files/" id) nil config nil))

(defn file-content
  "Retrieves the content of a specific file by its id.

  Example:
  (openai/file-content \"file-id\" config)"
  [id config]
  (http/get! (str "/v1/files/" id "/content") nil config {:parse? false}))

(defn file-upload!
  "Uploads a file with the provided parameters.

  Example:
  (openai/file-upload!
    {:file    (io/file \"file.txt\")
     :purpose \"fine-tune\"}
    config)"
  [params config]
  (http/post! "/v1/files"
              {:multipart params} config))

(defn file-delete!
  "Deletes a specific file by its id.

  Example:
  (openai/file-delete! \"file-id\" config)"
  [id config]
  (http/delete! (str "/v1/files/" id)
                config))

; images

(defn image-generation
  "Generates an image based on the given parameters.

  Example:
  (openai/image-generation {:prompt \"wall\"} config)"
  [params config]
  (http/post! "/v1/images/generations"
              {:body params} config))

(defn image-edit
  "Edits an image based on the given parameters.

  Example:
  (openai/image-edit
    {:image  (io/file \"wall.png\")
     :prompt \"add brick\"}
     config)"
  [params config]
  (http/post! "/v1/images/edits"
              {:multipart params} config))

(defn image-variation
  "Generates image variations based on the given parameters.

  Example:
  (openai/image-variation {:image (io/file \"image.png\")} config)"
  [params config]
  (http/post! "/v1/images/variations"
              {:multipart params} config))

; models

(defn models
  "Retrieves the list of available models.

  Example:
  (openai/models config)"
  [config]
  (http/get! "/v1/models" nil config nil))

(defn model
  "Retrieves the details of a specific model by its id.

  Example:
  (openai/model \"gpt-3.5-turbo\" config)"
  [id config]
  (http/get! (str "/v1/models/" (name id)) nil config nil))

(defn model-delete!
  "Deletes a specific fine-tuned model by its id.

  Example:
  (openai/model-delete! \"model-id\" config)"
  [model-id config]
  (http/delete! (str "/v1/models/" model-id) config))

; moderations

(defn moderation
  "Performs content moderation with the provided parameters.

  Example:
  (openai/moderation {:input \"some text\"} config)"
  [params config]
  (http/post! "/v1/moderations"
              {:body params} config))

; deprecated

(defn ^:deprecated completion
  "Generates a completion based on the given parameters.

  Example:
  (openai/completion {:model \"ada\" :prompt \"hello\"} config)"
  [params config]
  (http/post! "/v1/completions"
              (params->http-params params)
              config))

(defn ^:deprecated edit
  "Generates an edit based on the given parameters.

  Example:
  (openai/edit
    {:model       \"code-davinci-edit-001\"
     :instruction \"fix\"
     :input       \"println hello\"}
    config)"
  [params config]
  (http/post! "/v1/edits"
              {:body params} config))

(defn ^:deprecated fine-tunes
  "Retrieves the list of fine-tunes associated with the provided config.

  Example:
  (openai/fine-tunes config)"
  [config]
  (http/get! "/v1/fine-tunes" nil config nil))

(defn ^:deprecated fine-tune
  "Retrieves the details of a specific fine-tune by its id.

  Example:
  (openai/fine-tune \"ft-id\" config)"
  [id config]
  (http/get! (str "/v1/fine-tunes/" (name id)) nil config nil))

(defn ^:deprecated fine-tune-events
  "Retrieves the list of events for a specific fine-tune by its id.

  Example:
  (openai/fine-tune-events \"ft-id\" config)"
  [id config]
  (http/get! (str "/v1/fine-tunes/" id "/events") nil config nil))

(defn ^:deprecated fine-tune-create!
  "Creates a new fine-tune with the provided parameters.

  Example:
  (openai/fine-tune-create!
    {:training-file \"file-id\"
     :model         \"ada\"}
    config)"
  [params config]
  (http/post! "/v1/fine-tunes"
              {:body params} config))

(defn ^:deprecated fine-tune-cancel!
  "Cancels a specific fine-tune by its id.

  Example:
  (openai/fine-tune-cancel! \"ft-id\" config)"
  [id config]
  (http/post! (str "/v1/fine-tunes/" id "/cancel")
              {} config))

(defn ^:deprecated fine-tune-delete!
  "Deletes a specific fine-tuned model by its id.

  Example:
  (openai/fine-tune-delete! \"model-id\" config)"
  [model-id config]
  (http/delete! (str "/v1/models/" model-id) config))
