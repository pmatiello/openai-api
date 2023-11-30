(ns me.pmatiello.openai-api.api-test
  (:require [clojure.test :refer :all]
            [me.pmatiello.openai-api.api :as api]
            [me.pmatiello.openai-api.internal.http :as http]
            [mockfn.clj-test :as mfn]))

; config

(mfn/deftest config-test
  (mfn/testing "returns config with API key, including a default base-url"
    (is (= {:api-key  "my-api-key"
            :base-url "https://api.openai.com"}
           (api/config :api-key "my-api-key"))))

  (mfn/testing "returns config with API key and organisation id"
    (is (= {:api-key  "my-api-key"
            :org-id   "my-org-id"
            :base-url "https://api.openai.com"}
           (api/config :api-key "my-api-key"
                       :org-id "my-org-id"))))

  (mfn/testing "returns config with overriden base-url"
    (is (= {:api-key  "my-api-key"
            :base-url "https://api.not-openai.com"}
           (api/config :api-key "my-api-key"
                       :base-url "https://api.not-openai.com"))))

  (mfn/testing "returns config with custom http options"
    (is (= {:api-key   "my-api-key"
            :base-url  "https://api.openai.com"
            :http-opts {:connection-timeout 2500
                        :socket-timeout     2500}}
           (api/config :api-key "my-api-key"
                       :http-opts {:connection-timeout 2500
                                   :socket-timeout     2500})))))

; audio

(deftest audio-speach-test
  (mfn/testing "retrieves speach generated from text"
    (is (= 'response (api/audio-speach 'params 'config)))
    (mfn/providing
      (http/post! "/v1/audio/speech"
                  {:body 'params :http-opts {:as :stream}}
                  'config {:parse? false})
      'response)))

(deftest audio-transcription-test
  (mfn/testing "retrieves an audio transcription"
    (is (= 'response (api/audio-transcription 'params 'config)))
    (mfn/providing
      (http/post! "/v1/audio/transcriptions" {:multipart 'params} 'config nil)
      'response)))

(deftest audio-translation-test
  (mfn/testing "retrieves an audio translation"
    (is (= 'response (api/audio-translation 'params 'config)))
    (mfn/providing
      (http/post! "/v1/audio/translations" {:multipart 'params} 'config nil)
      'response)))

; chat

(mfn/deftest chat-test
  (mfn/testing "retrieves completion"
    (is (= 'response (api/chat 'params 'config)))
    (mfn/providing
      (http/post! "/v1/chat/completions" {:body 'params} 'config nil) 'response))

  (mfn/testing "streams completion"
    (is (= 'response (api/chat {:messages 'messages :stream true} 'config)))
    (mfn/providing
      (http/post! "/v1/chat/completions"
                  {:body {:messages 'messages :stream true} :http-opts {:as :stream}}
                  'config nil)
      'response)))

; embeddings

(deftest embedding-test
  (mfn/testing "retrieves embedding"
    (is (= 'response (api/embedding 'params 'config)))
    (mfn/providing
      (http/post! "/v1/embeddings" {:body 'params} 'config nil) 'response)))

; fine-tuning

(deftest fine-tuning-jobs-test
  (mfn/testing "retrieves fine tuning jobs"
    (is (= 'response (api/fine-tuning-jobs 'params 'config)))
    (mfn/providing
      (http/get! "/v1/fine_tuning/jobs" 'params 'config nil)
      'response)))

(deftest fine-tuning-job-test
  (mfn/testing "retrieves a fine tuning job"
    (is (= 'response (api/fine-tuning-job 'id 'config)))
    (mfn/providing
      (http/get! "/v1/fine_tuning/jobs/id" nil 'config nil)
      'response)))

(deftest fine-tuning-job-create!-test
  (mfn/testing "creates a fine tuning job"
    (is (= 'response (api/fine-tuning-job-create! 'params 'config)))
    (mfn/providing
      (http/post! "/v1/fine_tuning/jobs" {:body 'params} 'config nil) 'response)))

(deftest fine-tuning-job-cancel!-test
  (mfn/testing "cancels a fine-tune job"
    (is (= 'response (api/fine-tuning-job-cancel! 'id 'config)))
    (mfn/providing
      (http/post! "/v1/fine_tuning/jobs/id/cancel" {} 'config nil) 'response)))

(deftest fine-tuning-job-events-test
  (mfn/testing "retrieves events for a fine tuning job"
    (is (= 'response (api/fine-tuning-job-events 'id 'params 'config)))
    (mfn/providing
      (http/get! "/v1/fine_tuning/jobs/id/events" 'params 'config nil)
      'response)))

; files

(deftest files-test
  (mfn/testing "retrieves list of files"
    (is (= 'response (api/files 'config)))
    (mfn/providing
      (http/get! "/v1/files" nil 'config nil)
      'response)))

(deftest file-test
  (mfn/testing "retrieve file"
    (is (= 'response (api/file 'id 'config)))
    (mfn/providing
      (http/get! "/v1/files/id" nil 'config nil)
      'response)))

(deftest file-content-test
  (mfn/testing "retrieve file"
    (is (= 'response (api/file-content 'id 'config)))
    (mfn/providing
      (http/get! "/v1/files/id/content" nil 'config {:parse? false})
      'response)))

(deftest file-upload!-test
  (mfn/testing "uploads a file"
    (is (= 'response (api/file-upload! 'params 'config)))
    (mfn/providing
      (http/post! "/v1/files" {:multipart 'params} 'config nil) 'response)))

(deftest file-delete!-test
  (mfn/testing "deletes the given file"
    (is (= 'response (api/file-delete! 'id 'config)))
    (mfn/providing
      (http/delete! "/v1/files/id" 'config) 'response)))

; images

(mfn/deftest image-generation-test
  (mfn/testing "retrieves a generated image"
    (is (= 'response (api/image-generation 'params 'config)))
    (mfn/providing
      (http/post! "/v1/images/generations" {:body 'params} 'config nil) 'response)))

(mfn/deftest image-edit-test
  (mfn/testing "retrieves an edited image"
    (is (= 'response (api/image-edit 'params 'config)))
    (mfn/providing
      (http/post! "/v1/images/edits" {:multipart 'params} 'config nil) 'response)))

(deftest image-variation-test
  (mfn/testing "retrieves an image variation"
    (is (= 'response (api/image-variation 'params 'config)))
    (mfn/providing
      (http/post! "/v1/images/variations" {:multipart 'params} 'config nil) 'response)))

; models

(mfn/deftest models-test
  (mfn/testing "retrieves list of models"
    (is (= 'response (api/models 'config)))
    (mfn/providing
      (http/get! "/v1/models" nil 'config nil)
      'response)))

(mfn/deftest model-test
  (mfn/testing "retrieves the given model"
    (is (= 'response (api/model 'model 'config)))
    (mfn/providing
      (http/get! "/v1/models/model" nil 'config nil)
      'response)))

(deftest model-delete!-test
  (mfn/testing "deletes a fine-tuned model"
    (is (= 'response (api/model-delete! 'model 'config)))
    (mfn/providing
      (http/delete! "/v1/models/model"
                    'config) 'response)))

; moderations

(deftest moderation-test
  (mfn/testing "retrieves moderation classification for input"
    (is (= 'response (api/moderation 'params 'config)))
    (mfn/providing
      (http/post! "/v1/moderations" {:body 'params} 'config nil) 'response)))

; deprecated

(mfn/deftest completion-test
  (mfn/testing "retrieves completion"
    (is (= 'response (api/completion 'params 'config)))
    (mfn/providing
      (http/post! "/v1/completions" {:body 'params} 'config nil) 'response))

  (mfn/testing "streams completion"
    (is (= 'response (api/completion {:prompt 'prompt :stream true} 'config)))
    (mfn/providing
      (http/post! "/v1/completions"
                  {:body {:prompt 'prompt :stream true} :http-opts {:as :stream}}
                  'config nil) 'response)))

(mfn/deftest edit-test
  (mfn/testing "retrieves edit"
    (is (= 'response (api/edit 'params 'config)))
    (mfn/providing
      (http/post! "/v1/edits" {:body 'params} 'config nil) 'response)))

(mfn/deftest fine-tunes-test
  (mfn/testing "retrieves list of fine-tunes"
    (is (= 'response (api/fine-tunes 'config)))
    (mfn/providing
      (http/get! "/v1/fine-tunes" nil 'config nil) 'response)))

(deftest fine-tune-test
  (mfn/testing "retrieves a fine-tune"
    (is (= 'response (api/fine-tune 'id 'config)))
    (mfn/providing
      (http/get! "/v1/fine-tunes/id" nil 'config nil) 'response)))

(deftest fine-tune-events-test
  (mfn/testing "retrieves events for a fine-tune"
    (is (= 'response (api/fine-tune-events 'id 'config)))
    (mfn/providing
      (http/get! "/v1/fine-tunes/id/events" nil 'config nil) 'response)))

(deftest fine-tune-create!-test
  (mfn/testing "creates a fine-tuned model"
    (is (= 'response (api/fine-tune-create! 'params 'config)))
    (mfn/providing
      (http/post! "/v1/fine-tunes" {:body 'params} 'config nil) 'response)))

(deftest fine-tune-cancel!-test
  (mfn/testing "cancels a fine-tune job"
    (is (= 'response (api/fine-tune-cancel! 'id 'config)))
    (mfn/providing
      (http/post! "/v1/fine-tunes/id/cancel" {} 'config nil) 'response)))

(deftest fine-tune-delete!-test
  (mfn/testing "deletes a fine-tuned model"
    (is (= 'response (api/fine-tune-delete! 'model 'config)))
    (mfn/providing
      (http/delete! "/v1/models/model"
                    'config) 'response)))
