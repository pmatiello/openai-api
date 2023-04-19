(require '[pmatiello.openai-api.api :as api])
(require '[clojure.java.io :as io])
(require '[clojure.spec.alpha :as s])
(require '[clojure.java.browse :refer [browse-url]])
(require '[clojure.spec.test.alpha :as stest])

(stest/instrument)

(def ^:private api-key
  (or (System/getenv "OPENAI_API_KEY") (read-line)))

(def credentials
  (api/credentials api-key))

(api/models credentials)
(s/valid? :pmatiello.openai-api.specs.model/description-list *1)

(api/model "gpt-3.5-turbo" credentials)
(s/valid? :pmatiello.openai-api.specs.model/description *1)

(api/completion {:model "ada" :prompt "hello"} credentials)
(s/valid? :pmatiello.openai-api.specs.completion/result *1)

(api/chat {:model    "gpt-3.5-turbo"
           :messages [{:role "user" :content "(println \"hello"}]}
          credentials)
(s/valid? :pmatiello.openai-api.specs.chat/result *1)

(api/edit {:model       "code-davinci-edit-001"
           :instruction "Fix clojure code."
           :input       "(println \"hello)"}
          credentials)
(s/valid? :pmatiello.openai-api.specs.edit/result *1)

(api/image-generation {:prompt          "brick wall"
                       :response-format "url"}
                      credentials)
(s/valid? :pmatiello.openai-api.specs.image/result *1)

(api/image-edit {:image           (io/file "test/fixtures/image.png")
                 :mask            (io/file "test/fixtures/mask.png")
                 :prompt          "brick wall with a graffiti"
                 :response-format "url"}
                credentials)
(s/valid? :pmatiello.openai-api.specs.image/result *1)

(api/image-variation {:image (io/file "test/fixtures/image.png")} credentials)
(s/valid? :pmatiello.openai-api.specs.image/result *1)

(api/embedding {:model "text-embedding-ada-002"
                :input "brick wall"}
               credentials)
(s/valid? :pmatiello.openai-api.specs.embedding/result *1)

(api/audio-transcription {:model "whisper-1"
                          :file  (io/file "test/fixtures/audio.m4a")}
                         credentials)
(s/valid? :pmatiello.openai-api.specs.audio/result *1)

(api/audio-translation {:model "whisper-1"
                        :file  (io/file "test/fixtures/audio-pt.m4a")}
                       credentials)
(s/valid? :pmatiello.openai-api.specs.audio/result *1)

(api/file-upload! {:file    (io/file "test/fixtures/colors.txt")
                   :purpose "fine-tune"}
                  credentials)
(s/valid? :pmatiello.openai-api.specs.file/upload-result *1)

(api/files credentials)
(s/valid? :pmatiello.openai-api.specs.file/description-list *1)

(api/file (-> credentials api/files :data first :id) credentials)

(api/file-content (-> credentials api/files :data first :id) credentials)
(s/valid? string? *1)

(api/file-delete! (-> credentials api/files :data first :id) credentials)
(s/valid? :pmatiello.openai-api.specs.file/delete-result *1)

(api/fine-tune-create!
  {:training-file (->> credentials api/files :data
                       (filter #(-> % :filename #{"colors.txt"}))
                       first :id)
   :model         "ada"}
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/description *1)

(api/fine-tunes credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/description-list *1)

(api/fine-tune
  (-> credentials api/fine-tunes :data last :id)
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/description *1)

(api/fine-tune-delete!
  (->> credentials api/models :data
       (filter #(->> % :owned-by (re-matches #"user-.*"))) last :id)
  credentials)
(s/valid? :pmatiello.openai-api.specs.fine-tune/delete-result *1)

(comment
  "cleanup"

  "WARNING: deletes all files in the account"
  (doseq [each (->> credentials api/files :data (map :id))]
    (api/file-delete! each credentials))

  "WARNING: deletes all user's fine-tuned models in the account"
  (doseq [each (->> credentials api/models :data
                    (filter #(->> % :owned-by (re-matches #"user-.*")))
                    (map :id))]
    (api/fine-tune-delete! each credentials)))
