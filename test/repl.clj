(require '[me.pmatiello.openai-api.api :as openai])
(require '[clojure.java.browse :refer [browse-url]])
(require '[clojure.java.io :as io])
(require '[clojure.spec.alpha :as s])
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

(openai/model "gpt-3.5-turbo" config)

; Completions
(openai/completion {:model  "ada"
                    :prompt "hello"}
                   config)

(openai/completion {:model  "ada"
                    :prompt "hello"
                    :stream true}
                   config)

; Chat
(openai/chat {:model    "gpt-3.5-turbo"
              :messages [{:role "user" :content "(println \"hello"}]}
             config)

(openai/chat {:model    "gpt-3.5-turbo"
              :stream   true
              :messages [{:role "user" :content "hello world program in clojure."}]}
             config)

; Edits
(openai/edit {:model       "code-davinci-edit-001"
              :instruction "Fix clojure code."
              :input       "(println \"hello)"}
             config)

; Images
(openai/image-generation {:prompt          "brick wall"
                          :response-format "url"}
                         config)

(openai/image-edit {:image           (io/file "test/fixtures/image.png")
                    :mask            (io/file "test/fixtures/mask.png")
                    :prompt          "brick wall with a graffiti"
                    :response-format "url"}
                   config)

(openai/image-variation {:image (io/file "test/fixtures/image.png")}
                        config)

; Embeddings
(openai/embedding {:model "text-embedding-ada-002"
                   :input "brick wall"}
                  config)

; Audio
(openai/audio-transcription {:model "whisper-1"
                             :file  (io/file "test/fixtures/audio.m4a")}
                            config)

(openai/audio-translation {:model "whisper-1"
                           :file  (io/file "test/fixtures/audio-pt.m4a")}
                          config)

; Files
(openai/file-upload! {:file    (io/file "test/fixtures/colors.txt")
                      :purpose "fine-tune"}
                     config)

(openai/files config)

(openai/file (-> config openai/files :data first :id) config)

(openai/file-content (-> config openai/files :data first :id) config)

(openai/file-delete! (-> config openai/files :data first :id) config)

; Fine-tuning jobs
(openai/fine-tuning-jobs {} config)

(openai/fine-tuning-jobs
  {:after (-> (openai/fine-tuning-jobs {} config) :data first :id)}
  config)

(openai/fine-tuning-job
  (-> (openai/fine-tuning-jobs {} config) :data first :id)
  config)

(openai/fine-tuning-jobs-create!
  {:training-file (->> config openai/files :data
                       (filter #(-> % :filename #{"colors.txt"}))
                       first :id)
   :model         "davinci-002"}
  config)

(openai/fine-tuning-jobs-cancel!
  (-> (openai/fine-tuning-jobs {} config) :data first :id)
  config)

(openai/fine-tuning-jobs-events
  (-> (openai/fine-tuning-jobs {} config) :data (nth 4) :id) {}
  config)

; Fine-tunes (deprecated)
(openai/fine-tune-create!
  {:training-file (->> config openai/files :data
                       (filter #(-> % :filename #{"colors.txt"}))
                       first :id)
   :model         "ada"}
  config)

(openai/fine-tunes config)

(openai/fine-tune
  (-> config openai/fine-tunes :data last :id)
  config)

(openai/fine-tune-events
  (-> config openai/fine-tunes :data last :id)
  config)

(openai/fine-tune-cancel!
  (-> config openai/fine-tunes :data last :id)
  config)

(openai/fine-tune-delete!
  (->> config openai/models :data
       (filter #(->> % :owned-by (re-matches #"user-.*"))) last :id)
  config)

; Moderation
(openai/moderation {:input "kittens"} config)

; Timeout
(openai/models (merge config {:http-opts {:connection-timeout 2500
                                          :socket-timeout     2500}}))

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
