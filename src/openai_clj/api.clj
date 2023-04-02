(ns openai-clj.api
  (:require [openai-clj.internal.http :as http]))

(defn credentials
  ([api-key]
   {:api-key api-key})
  ([api-key org-id]
   {:api-key api-key
    :org-id  org-id}))

(defn models
  [credentials]
  (http/get! "https://api.openai.com/v1/models"
             credentials))

(defn model
  [model credentials]
  (http/get! (str "https://api.openai.com/v1/models/" (name model))
             credentials))
