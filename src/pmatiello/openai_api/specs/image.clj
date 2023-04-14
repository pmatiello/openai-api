(ns pmatiello.openai-api.specs.image
  (:require [clojure.spec.alpha :as s])
  (:import (java.io File)))

(s/def ::prompt string?)

(s/def ::n integer?)
(s/def ::response-format #{"url" "b64_json"})
(s/def ::size #{"256x256" "512x512" "1024x1024"})
(s/def ::user string?)

(s/def ::generation-params
  (s/keys :req-un [::prompt]
          :opt-un [::n ::response-format ::size ::user]))

(s/def ::file #(instance? File %))
(s/def ::image ::file)
(s/def ::mask ::file)

(s/def ::edit-params
  (s/keys :req-un [::image ::prompt]
          :opt-un [::mask ::n ::response-format ::size ::user]))

(s/def ::variation-params
  (s/keys :req-un [::image]
          :opt-un [::n ::response-format ::size ::user]))

(s/def ::created integer?)

(s/def ::url string?)
(s/def ::b64-json string?)
(s/def ::data*
  (s/or :url (s/keys :req-un [::url])
        :b64-json (s/keys :req-un [::b64-json])))
(s/def ::data
  (s/coll-of ::data*))

(s/def ::result
  (s/keys :req-un [::created ::data]))
