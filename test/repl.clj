(require '[me.pmatiello.openai-api.api :as openai])
(require '[clojure.java.io :as io])
(require '[clojure.spec.alpha :as s])
(require '[clojure.java.browse :refer [browse-url]])
(require '[clojure.spec.test.alpha :as stest])

(stest/instrument)

; Config
(def ^:private api-key
  (or (System/getenv "OPENAI_API_KEY") (read-line)))

(def config
  (openai/config :api-key api-key))
(s/valid? :me.pmatiello.openai-api.specs.config/config config)

; Models
(openai/models config)
(s/valid? :me.pmatiello.openai-api.specs.model/description-list *1)

(openai/model "gpt-3.5-turbo" config)
(s/valid? :me.pmatiello.openai-api.specs.model/description *1)

; Completions
(openai/completion {:model  "ada"
                    :prompt "hello"}
                   config)
(s/valid? :me.pmatiello.openai-api.specs.completion/result *1)

(openai/completion {:model  "ada"
                    :prompt "hello"
                    :stream true}
                   config)
(s/valid? :me.pmatiello.openai-api.specs.completion/result *1)

; Chat
(openai/chat {:model    "gpt-3.5-turbo"
              :messages [{:role "user" :content "(println \"hello"}]}
             config)
(s/valid? :me.pmatiello.openai-api.specs.chat/result *1)

(openai/chat {:model    "gpt-3.5-turbo"
              :stream   true
              :messages [{:role "user" :content "hello world program in clojure."}]}
             config)
(s/valid? :me.pmatiello.openai-api.specs.chat/result *1)

; Edits
(openai/edit {:model       "code-davinci-edit-001"
              :instruction "Fix clojure code."
              :input       "(println \"hello)"}
             config)
(s/valid? :me.pmatiello.openai-api.specs.edit/result *1)

; Images
(openai/image-generation {:prompt          "brick wall"
                          :response-format "url"}
                         config)
(s/valid? :me.pmatiello.openai-api.specs.image/result *1)

(openai/image-edit {:image           (io/file "test/fixtures/image.png")
                    :mask            (io/file "test/fixtures/mask.png")
                    :prompt          "brick wall with a graffiti"
                    :response-format "url"}
                   config)
(s/valid? :me.pmatiello.openai-api.specs.image/result *1)

(openai/image-variation {:image (io/file "test/fixtures/image.png")}
                        config)
(s/valid? :me.pmatiello.openai-api.specs.image/result *1)

; Embeddings
(openai/embedding {:model "text-embedding-ada-002"
                   :input "brick wall"}
                  config)
(s/valid? :me.pmatiello.openai-api.specs.embedding/result *1)

; Audio
(openai/audio-transcription {:model "whisper-1"
                             :file  (io/file "test/fixtures/audio.m4a")}
                            config)
(s/valid? :me.pmatiello.openai-api.specs.audio/result *1)

(openai/audio-translation {:model "whisper-1"
                           :file  (io/file "test/fixtures/audio-pt.m4a")}
                          config)
(s/valid? :me.pmatiello.openai-api.specs.audio/result *1)

; Files
(openai/file-upload! {:file    (io/file "test/fixtures/colors.txt")
                      :purpose "fine-tune"}
                     config)
(s/valid? :me.pmatiello.openai-api.specs.file/upload-result *1)

(openai/files config)
(s/valid? :me.pmatiello.openai-api.specs.file/description-list *1)

(openai/file (-> config openai/files :data first :id) config)
(s/valid? :me.pmatiello.openai-api.specs.file/result *1)

(openai/file-content (-> config openai/files :data first :id) config)
(s/valid? string? *1)

(openai/file-delete! (-> config openai/files :data first :id) config)
(s/valid? :me.pmatiello.openai-api.specs.file/delete-result *1)

; Fine-tuning jobs
(openai/fine-tuning-jobs {} config)
(openai/fine-tuning-jobs
  {:after (-> (openai/fine-tuning-jobs {} config) :data first :id)}
  config)

(openai/fine-tuning-jobs-create!
  {:training-file (->> config openai/files :data
                       (filter #(-> % :filename #{"colors.txt"}))
                       first :id)
   :model         "davinci-002"}
  config)

; Fine-tunes (deprecated)
(openai/fine-tune-create!
  {:training-file (->> config openai/files :data
                       (filter #(-> % :filename #{"colors.txt"}))
                       first :id)
   :model         "ada"}
  config)
(s/valid? :me.pmatiello.openai-api.specs.fine-tune/description *1)

(openai/fine-tunes config)
(s/valid? :me.pmatiello.openai-api.specs.fine-tune/description-list *1)

(openai/fine-tune
  (-> config openai/fine-tunes :data last :id)
  config)
(s/valid? :me.pmatiello.openai-api.specs.fine-tune/description *1)

(openai/fine-tune-events
  (-> config openai/fine-tunes :data last :id)
  config)
(s/valid? :me.pmatiello.openai-api.specs.fine-tune/event-list *1)

(openai/fine-tune-cancel!
  (-> config openai/fine-tunes :data last :id)
  config)
(s/valid? :me.pmatiello.openai-api.specs.fine-tune/description *1)

(openai/fine-tune-delete!
  (->> config openai/models :data
       (filter #(->> % :owned-by (re-matches #"user-.*"))) last :id)
  config)
(s/valid? :me.pmatiello.openai-api.specs.fine-tune/delete-result *1)

; Moderation
(openai/moderation {:input "kittens"} config)
(s/valid? :me.pmatiello.openai-api.specs.moderation/classification *1)

; Timeout
(openai/models (merge config {:http-opts {:connection-timeout 2500
                                          :socket-timeout     2500}}))
(s/valid? :me.pmatiello.openai-api.specs.model/description-list *1)

(comment
  "cleanup"

  "WARNING: deletes all files in the account"
  (doseq [each (->> config openai/files :data (map :id))]
    (openai/file-delete! each config))

  "WARNING: deletes all user's fine-tuned models in the account"
  (doseq [each (->> config openai/models :data
                    (filter #(->> % :owned-by (re-matches #"user-.*")))
                    (map :id))]
    (openai/fine-tune-delete! each config)))
