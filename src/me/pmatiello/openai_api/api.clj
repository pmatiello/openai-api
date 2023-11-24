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
            [me.pmatiello.openai-api.specs.edit :as specs.edit]
            [me.pmatiello.openai-api.specs.embedding :as specs.embedding]
            [me.pmatiello.openai-api.specs.file :as specs.file]
            [me.pmatiello.openai-api.specs.fine-tune :as specs.fine-tune]
            [me.pmatiello.openai-api.specs.fine-tuning-jobs :as specs.fine-tuning-jobs]
            [me.pmatiello.openai-api.specs.image :as specs.image]
            [me.pmatiello.openai-api.specs.model :as specs.model]
            [me.pmatiello.openai-api.specs.moderation :as specs.moderation]))

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

(defn models
  "Retrieves the list of available models.

  Example:
  (openai/models config)"
  [config]
  (http/get! "/v1/models" nil config nil))

(s/fdef models
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.model/description-list)

(defn model
  "Retrieves the details of a specific model by its id.

  Example:
  (openai/model \"gpt-3.5-turbo\" config)"
  [id config]
  (http/get! (str "/v1/models/" (name id)) nil config nil))

(s/fdef model
  :args (s/cat :id ::specs.model/id
               :config ::specs.config/config)
  :ret ::specs.model/description)

(defn ^:private params->http-params
  [params]
  (let [body      {:body params}
        http-opts (when (:stream params) {:http-opts {:as :stream}})]
    (merge body http-opts)))

(s/fdef params->http-params
  :args (s/cat :params map?)
  :ret ::http/params)

(defn completion
  "Generates a completion based on the given parameters.

  Example:
  (openai/completion {:model \"ada\" :prompt \"hello\"} config)"
  [params config]
  (http/post! "/v1/completions"
              (params->http-params params)
              config))

(s/fdef completion
  :args (s/cat :params ::specs.completion/params
               :config ::specs.config/config)
  :ret ::specs.completion/result)

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

(s/fdef chat
  :args (s/cat :params ::specs.chat/params
               :config ::specs.config/config)
  :ret ::specs.chat/result)

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

(s/fdef edit
  :args (s/cat :params ::specs.edit/params
               :config ::specs.config/config)
  :ret ::specs.edit/result)

(defn image-generation
  "Generates an image based on the given parameters.

  Example:
  (openai/image-generation {:prompt \"wall\"} config)"
  [params config]
  (http/post! "/v1/images/generations"
              {:body params} config))

(s/fdef image-generation
  :args (s/cat :params ::specs.image/generation-params
               :config ::specs.config/config)
  :ret ::specs.image/result)

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

(s/fdef image-edit
  :args (s/cat :params ::specs.image/edit-params
               :config ::specs.config/config)
  :ret ::specs.image/result)

(defn image-variation
  "Generates image variations based on the given parameters.

  Example:
  (openai/image-variation {:image (io/file \"image.png\")} config)"
  [params config]
  (http/post! "/v1/images/variations"
              {:multipart params} config))

(s/fdef image-variation
  :args (s/cat :params ::specs.image/variation-params
               :config ::specs.config/config)
  :ret ::specs.image/result)

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

(s/fdef embedding
  :args (s/cat :params ::specs.embedding/params
               :config ::specs.config/config)
  :ret ::specs.embedding/result)

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

(s/fdef audio-transcription
  :args (s/cat :params ::specs.audio/transcription-params
               :config ::specs.config/config)
  :ret ::specs.audio/result)

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

(s/fdef audio-translation
  :args (s/cat :params ::specs.audio/translation-params
               :config ::specs.config/config)
  :ret ::specs.audio/result)

(defn files
  "Retrieves the list of files associated with the provided config.

  Example:
  (openai/files config)"
  [config]
  (http/get! "/v1/files" nil config nil))

(s/fdef files
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.file/description-list)

(defn file
  "Retrieves the details of a specific file by its id.

  Example:
  (openai/file \"file-id\" config)"
  [id config]
  (http/get! (str "/v1/files/" id) nil config nil))

(s/fdef file
  :args (s/cat :id ::specs.file/id
               :config ::specs.config/config)
  :ret ::specs.file/result)

(defn file-content
  "Retrieves the content of a specific file by its id.

  Example:
  (openai/file-content \"file-id\" config)"
  [id config]
  (http/get! (str "/v1/files/" id "/content") nil config {:parse? false}))

(s/fdef file-content
  :args (s/cat :id ::specs.file/id
               :config ::specs.config/config)
  :ret string?)

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

(s/fdef file-upload!
  :args (s/cat :params ::specs.file/upload-params
               :config ::specs.config/config)
  :ret ::specs.file/upload-result)

(defn file-delete!
  "Deletes a specific file by its id.

  Example:
  (openai/file-delete! \"file-id\" config)"
  [id config]
  (http/delete! (str "/v1/files/" id)
                config))

(s/fdef file-delete!
  :args (s/cat :id ::specs.file/id
               :config ::specs.config/config)
  :ret ::specs.file/delete-result)

(defn ^:deprecated fine-tunes
  "Retrieves the list of fine-tunes associated with the provided config.

  Example:
  (openai/fine-tunes config)"
  [config]
  (http/get! "/v1/fine-tunes" nil config nil))

(s/fdef fine-tunes
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.fine-tune/description-list)

(defn ^:deprecated fine-tune
  "Retrieves the details of a specific fine-tune by its id.

  Example:
  (openai/fine-tune \"ft-id\" config)"
  [id config]
  (http/get! (str "/v1/fine-tunes/" (name id)) nil config nil))

(s/fdef fine-tune
  :args (s/cat :id ::specs.fine-tune/id
               :config ::specs.config/config)
  :ret ::specs.fine-tune/description)

(defn ^:deprecated fine-tune-events
  "Retrieves the list of events for a specific fine-tune by its id.

  Example:
  (openai/fine-tune-events \"ft-id\" config)"
  [id config]
  (http/get! (str "/v1/fine-tunes/" id "/events") nil config nil))

(s/fdef fine-tune-events
  :args (s/cat :id ::specs.fine-tune/id
               :config ::specs.config/config)
  :ret ::specs.fine-tune/event-list)

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

(s/fdef fine-tune-create!
  :args (s/cat :params ::specs.fine-tune/create-params
               :config ::specs.config/config)
  :ret ::specs.fine-tune/description)

(defn ^:deprecated fine-tune-cancel!
  "Cancels a specific fine-tune by its id.

  Example:
  (openai/fine-tune-cancel! \"ft-id\" config)"
  [id config]
  (http/post! (str "/v1/fine-tunes/" id "/cancel")
              {} config))

(s/fdef fine-tune-cancel!
  :args (s/cat :id ::specs.fine-tune/id
               :config ::specs.config/config)
  :ret ::specs.fine-tune/description)

(defn ^:deprecated fine-tune-delete!
  "Deletes a specific fine-tuned model by its id.

  Example:
  (openai/fine-tune-delete! \"model-id\" config)"
  [model-id config]
  (http/delete! (str "/v1/models/" model-id) config))

(s/fdef fine-tune-delete!
  :args (s/cat :model ::specs.fine-tune/model
               :config ::specs.config/config)
  :ret ::specs.fine-tune/delete-result)

(defn moderation
  "Performs content moderation with the provided parameters.

  Example:
  (openai/moderation {:input \"some text\"} config)"
  [params config]
  (http/post! "/v1/moderations"
              {:body params} config))

(s/fdef moderation
  :args (s/cat :params ::specs.moderation/params
               :config ::specs.config/config)
  :ret ::specs.moderation/classification)

(defn fine-tuning-jobs
  "List fine-tuning jobs.

  Example:
  (openai/fine-tuning-jobs {} config)"
  [params config]
  (http/get! "/v1/fine_tuning/jobs" params config nil))

(s/fdef fine-tuning-jobs
  :args (s/cat :params ::specs.fine-tuning-jobs/params
               :config ::specs.config/config)
  :ret ::specs.fine-tuning-jobs/description-list)

(defn fine-tuning-job
  "Retrieves the details of a specific fine-tuning by its id.

  Example:
  (openai/fine-tuning-job \"ft-id\" config)"
  [id config]
  (http/get! (str "/v1/fine_tuning/jobs/" (name id)) nil config nil))

(s/fdef fine-tuning-job
  :args (s/cat :id ::specs.fine-tuning-jobs/id
               :config ::specs.config/config)
  :ret ::specs.fine-tuning-jobs/description)

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

(s/fdef fine-tuning-jobs-create!
  :args (s/cat :params ::specs.fine-tuning-jobs/create-params
               :config ::specs.config/config)
  :ret ::specs.fine-tuning-jobs/description)

(defn fine-tuning-jobs-cancel!
  "Cancels a specific fine-tune by its id.

  Example:
  (openai/fine-tuning-jobs-cancel! \"ft-id\" config)"
  [id config]
  (http/post! (str "/v1/fine_tuning/jobs/" id "/cancel")
              {} config))

(s/fdef fine-tuning-jobs-cancel!
  :args (s/cat :id ::specs.fine-tuning-jobs/id
               :config ::specs.config/config)
  :ret ::specs.fine-tuning-jobs/description)
