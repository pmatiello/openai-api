(ns pmatiello.openai-api.integration-tests
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]
            [pmatiello.openai-api.api :as api]))

(stest/instrument)

(def ^:private api-key
  (System/getenv "OPENAI_API_KEY"))

(def ^:private credentials
  (api/credentials api-key))

(deftest credentials-test
  (is (s/valid?
        :pmatiello.openai-api.specs.credentials/credentials
        (api/credentials api-key))))

(deftest models-test
  (is (s/valid?
        :pmatiello.openai-api.specs.model/result-list
        (api/models credentials))))

(deftest model-test
  (is (s/valid?
        :pmatiello.openai-api.specs.model/result
        (api/model :gpt-3.5-turbo credentials))))

(deftest completion-test
  (is (s/valid?
        :pmatiello.openai-api.specs.completion/result
        (api/completion {:model "ada" :prompt "(println "} credentials))))

(deftest chat-test
  (is (s/valid?
        :pmatiello.openai-api.specs.chat/result
        (api/chat {:model    "gpt-3.5-turbo"
                   :messages [{:role "user" :content "Hello!"}]}
                  credentials))))

(deftest edit-test
  (is (s/valid?
        :pmatiello.openai-api.specs.edit/result
        (api/edit {:model       "code-davinci-edit-001"
                   :instruction "Fix."
                   :input       "(println \"hello)"}
                  credentials))))

(deftest image-generation-test
  (is (s/valid?
        :pmatiello.openai-api.specs.image/result
        (api/image-generation {:prompt "kitten" :response-format "url"}
                              credentials))))

(deftest image-edit-test
  (is (s/valid?
        :pmatiello.openai-api.specs.image/result
        (api/image-edit {:image           (io/file "test/fixtures/image.png")
                         :mask            (io/file "test/fixtures/mask.png")
                         :prompt          "brick wall with a graffiti"
                         :response-format "url"}
                        credentials))))

(deftest image-variation-test
  (is (s/valid?
        :pmatiello.openai-api.specs.image/result
        (api/image-variation {:image (io/file "test/fixtures/image.png")}
                             credentials))))

(deftest embedding-test
  (is (s/valid?
        :pmatiello.openai-api.specs.embedding/result
        (api/embedding {:model "text-embedding-ada-002"
                        :input "kittens napping."}
                       credentials))))

(deftest audio-transcription-test
  (is (s/valid?
        :pmatiello.openai-api.specs.audio/result
        (api/audio-transcription {:model "whisper-1"
                                  :file  (io/file "test/fixtures/audio.m4a")}
                                 credentials))))

(deftest audio-translation-test
  (is (s/valid?
        :pmatiello.openai-api.specs.audio/result
        (api/audio-translation {:model "whisper-1"
                                :file  (io/file "test/fixtures/audio-pt.m4a")}
                               credentials))))

(deftest files-test
  (testing "file-upload!"
    (is (s/valid?
          :pmatiello.openai-api.specs.file/upload-result
          (api/file-upload! {:file    (io/file "test/fixtures/colors.txt")
                             :purpose "fine-tune"}
                            credentials))))

  (testing "files"
    (is (s/valid?
          :pmatiello.openai-api.specs.file/result-list
          (api/files credentials))))

  (while
    (->> credentials api/files :data (map :status) (every? #{"processed"}) not)
    (Thread/sleep 1000))

  (testing "file"
    (is (s/valid?
          :pmatiello.openai-api.specs.file/result
          (api/file (-> credentials api/files :data first :id) credentials))))

  (testing "file-content"
    (is (s/valid?
          string?
          (api/file-content (-> credentials api/files :data first :id)
                            credentials))))

  (testing "file-delete!"
    (is (s/valid?
          :pmatiello.openai-api.specs.file/delete-result
          (api/file-delete! (-> credentials api/files :data first :id)
                            credentials)))))
