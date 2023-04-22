(require '[me.pmatiello.openai-api.api :as openai])
(require '[clojure.java.io :as io])
(require '[clojure.spec.alpha :as s])
(require '[clojure.java.browse :refer [browse-url]])
(require '[clojure.spec.test.alpha :as stest])

(stest/instrument)

(def ^:private api-key
  (or (System/getenv "OPENAI_API_KEY") (read-line)))

(def credentials
  (openai/credentials api-key))

(openai/models credentials)
(s/valid? :pmatiello.openai-api.specs.model/description-list *1)

(openai/model "gpt-3.5-turbo" credentials)
(s/valid? :pmatiello.openai-api.specs.model/description *1)

(openai/completion {:model "ada" :prompt "hello"} credentials)
(s/valid? :pmatiello.openai-api.specs.completion/result *1)

(openai/chat {:model "gpt-3.5-turbo"
           :messages [{:role "user" :content "(println \"hello"}]}
             credentials)
(s/valid? :pmatiello.openai-api.specs.chat/result *1)

(openai/edit {:model    "code-davinci-edit-001"
           :instruction "Fix clojure code."
           :input       "(println \"hello)"}
             credentials)
(s/valid? :pmatiello.openai-api.specs.edit/result *1)

(openai/image-generation {:prompt       "brick wall"
                       :response-format "url"}
                         credentials)
(s/valid? :pmatiello.openai-api.specs.image/result *1)

(openai/image-edit {:image        (io/file "test/fixtures/image.png")
                 :mask            (io/file "test/fixtures/mask.png")
                 :prompt          "brick wall with a graffiti"
                 :response-format "url"}
                   credentials)
(s/valid? :pmatiello.openai-api.specs.image/result *1)

(openai/image-variation {:image (io/file "test/fixtures/image.png")} credentials)
(s/valid? :pmatiello.openai-api.specs.image/result *1)

(openai/embedding {:model "text-embedding-ada-002"
                :input    "brick wall"}
                  credentials)
(s/valid? :pmatiello.openai-api.specs.embedding/result *1)

(openai/audio-transcription {:model "whisper-1"
                          :file     (io/file "test/fixtures/audio.m4a")}
                            credentials)
(s/valid? :pmatiello.openai-api.specs.audio/result *1)

(openai/audio-translation {:model "whisper-1"
                        :file     (io/file "test/fixtures/audio-pt.m4a")}
                          credentials)
(s/valid? :pmatiello.openai-api.specs.audio/result *1)

(openai/file-upload! {:file (io/file "test/fixtures/colors.txt")
                   :purpose "fine-tune"}
                     credentials)
(s/valid? :pmatiello.openai-api.specs.file/upload-result *1)

(openai/files credentials)
(s/valid? :pmatiello.openai-api.specs.file/description-list *1)

(openai/file (-> credentials openai/files :data first :id) credentials)

(openai/file-content (-> credentials openai/files :data first :id) credentials)
(s/valid? string? *1)

(openai/file-delete! (-> credentials openai/files :data first :id) credentials)
(s/valid? :pmatiello.openai-api.specs.file/delete-result *1)

(openai/fine-tune-create!
  {:training-file (->> credentials openai/files :data
                       (filter #(-> % :filename #{"colors.txt"}))
                       first :id)
   :model         "ada"}
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/description *1)

(openai/fine-tunes credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/description-list *1)

(openai/fine-tune
  (-> credentials openai/fine-tunes :data last :id)
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/description *1)

(openai/fine-tune-events
  (-> credentials openai/fine-tunes :data last :id)
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/event-list *1)

(openai/fine-tune-cancel!
  (-> credentials openai/fine-tunes :data last :id)
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/description *1)

(openai/fine-tune-delete!
  (->> credentials openai/models :data
       (filter #(->> % :owned-by (re-matches #"user-.*"))) last :id)
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/delete-result *1)

(openai/moderation {:input "kittens"} credentials)
(s/valid? :pmatiello.openai-api.specs.moderation/classification *1)

(comment
  "cleanup"

  "WARNING: deletes all files in the account"
  (doseq [each (->> credentials openai/files :data (map :id))]
    (openai/file-delete! each credentials))

  "WARNING: deletes all user's fine-tuned models in the account"
  (doseq [each (->> credentials openai/models :data
                    (filter #(->> % :owned-by (re-matches #"user-.*")))
                    (map :id))]
    (openai/fine-tune-delete! each credentials)))
