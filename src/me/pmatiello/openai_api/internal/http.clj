(ns ^:no-doc me.pmatiello.openai-api.internal.http
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [me.pmatiello.openai-api.specs.config :as specs.config])
  (:import (java.io File)))

(s/def ::api-response
  (s/or :map map?
        :string string?))
(s/def ::body map?)
(s/def ::http-headers map?)
(s/def ::multipart map?)
(s/def ::parse? boolean?)
(s/def ::path string?)
(s/def ::url string?)
(s/def ::options
  (s/keys* :opt-un [::parse?]))
(s/def ::params
  (s/keys ::opt-un [::body ::multipart]))

(defn config+path->url [config path]
  (str (:base-url config) path))

(s/fdef config+path->url
  :args (s/cat :config ::specs.config/config
               :path ::path)
  :ret ::url)

(defn ^:private config->headers
  [{:keys [api-key org-id]}]
  (if org-id
    {"Authorization"       (str "Bearer " api-key)
     "OpenAI-Organization" org-id}
    {"Authorization" (str "Bearer " api-key)}))

(s/fdef config->headers
  :args (s/cat :config ::specs.config/config)
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
  [path config & {:as options}]
  (let [url      (config+path->url config path)
        headers  (config->headers config)
        response (client/get url {:headers headers})
        body     (:body response)
        options  (merge {:parse? true} options)]
    (if (:parse? options)
      (json/read-str body {:key-fn json->clj-keys})
      body)))

(s/fdef get!
  :args (s/cat :path ::path
               :config ::specs.config/config
               :options ::options)
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

(defn post! [path params config]
  (let [url      (config+path->url config path)
        headers  (config->headers config)
        req-map  (-> {}
                     (with-headers headers)
                     (with-body params)
                     (with-multipart params))
        response (client/post url req-map)]
    (json/read-str (:body response) {:key-fn json->clj-keys})))

(s/fdef post!
  :args (s/cat :path ::path
               :params ::params
               :config ::specs.config/config)
  :ret ::api-response)

(defn delete!
  [path config]
  (let [url      (config+path->url config path)
        headers  (config->headers config)
        response (client/delete url {:headers headers})]
    (json/read-str (:body response) {:key-fn json->clj-keys})))

(s/fdef delete!
  :args (s/cat :path ::path
               :config ::specs.config/config)
  :ret ::api-response)
