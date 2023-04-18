(ns pmatiello.openai-api.specs.audio-test
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pmatiello.openai-api.specs.audio :as specs.audio]))

(def ^:private transcription-params
  {:model "whisper-1"
   :file  (io/file "test/fixtures/audio.m4a")})

(def ^:private translation-params
  {:model "whisper-1"
   :file  (io/file "test/fixtures/audio-pt.m4a")})

(def ^:private result
  {:text "The kittens are napping."})

(deftest transcription-params-test
  (is (s/valid? ::specs.audio/transcription-params transcription-params)))

(deftest transcription-params-test
  (is (s/valid? ::specs.audio/translation-params translation-params)))

(deftest result-test
  (is (s/valid? ::specs.audio/result result)))
