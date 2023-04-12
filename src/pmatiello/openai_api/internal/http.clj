(ns pmatiello.openai-api.internal.http
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [pmatiello.openai-api.specs.credentials :as specs.credentials])
  (:import (java.io File)))

(s/def ::api-response map?)
(s/def ::endpoint string?)
(s/def ::body map?)
(s/def ::multipart map?)
(s/def ::params
  (s/keys ::opt-un [::body ::multipart]))
(s/def ::http-headers map?)

(defn ^:private credentials->headers
  [{:keys [api-key org-id]}]
  (if org-id
    {"Authorization"       (str "Bearer " api-key)
     "OpenAI-Organization" org-id}
    {"Authorization" (str "Bearer " api-key)}))

(s/fdef credentials->headers
  :args (s/cat :credentials ::specs.credentials/credentials)
  :ret ::http-headers)

(defn ^:private json->clj-keys
  [orig-key]
  (-> orig-key
      (str/replace #"_" "-")
      keyword))

(defn ^:private clj->json-keys
  [orig-key]
  (-> orig-key
      name
      (str/replace #"-" "_")))

(defn get!
  [endpoint credentials]
  (let [headers  (credentials->headers credentials)
        response (client/get endpoint {:headers headers})]
    (json/read-str (:body response) {:key-fn json->clj-keys})))

(s/fdef get!
  :args (s/cat :endpoint ::endpoint
               :credentials ::specs.credentials/credentials)
  :ret ::api-response)

(defn ^:private with-headers
  [req-map headers]
  (merge req-map {:headers headers}))

(defn ^:private with-body
  [req-map {:keys [body]}]
  (if body
    (merge req-map
           {:body         (json/write-str body {:key-fn clj->json-keys})
            :content-type :json})
    req-map))

(defn ^:private ->part
  [[k v]]
  {:name    (clj->json-keys k)
   :content (if (instance? File v) v (str v))})

(defn ^:private with-multipart
  [req-map {:keys [multipart]}]
  (if multipart
    (merge req-map {:multipart (mapv ->part multipart)})
    req-map))

(defn post! [endpoint params credentials]
  (let [headers  (credentials->headers credentials)
        req-map  (-> {}
                     (with-headers headers)
                     (with-body params)
                     (with-multipart params))
        response (client/post endpoint req-map)]
    (json/read-str (:body response) {:key-fn json->clj-keys})))

(s/fdef post!
  :args (s/cat :endpoint ::endpoint
               :params ::params
               :credentials ::specs.credentials/credentials)
  :ret ::api-response)
