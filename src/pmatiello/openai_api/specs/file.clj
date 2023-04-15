(ns pmatiello.openai-api.specs.file
  (:require [clojure.spec.alpha :as s])
  (:import (java.io File)))

(s/def ::bytes integer?)
(s/def ::created-at integer?)
(s/def ::filename string?)
(s/def ::id string?)
(s/def ::object string?)
(s/def ::purpose string?)

(s/def ::data*
  (s/keys :req-un [::bytes ::created-at ::filename ::id ::object ::purpose]))

(s/def ::data
  (s/coll-of ::data*))

(s/def ::result-list
  (s/keys :req-un [::data ::object]))

(s/def ::status string?)
(s/def ::status-details any?)

(s/def ::result
  (s/keys :req-un [::bytes ::created-at ::filename ::id ::object ::purpose
                   ::status ::status-details]))

(s/def ::file #(instance? File %))
(s/def ::purpose string?)

(s/def ::upload-params
  (s/keys :req-un [::file ::purpose]))

(s/def ::upload-result
  (s/keys :req-un [::bytes ::created-at ::filename ::id ::object ::purpose
                   ::status ::status-details]))

(s/def ::deleted boolean?)

(s/def ::delete-result
  (s/keys :req-un [::deleted ::id ::object]))
