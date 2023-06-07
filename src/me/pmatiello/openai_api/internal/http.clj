(ns ^:no-doc me.pmatiello.openai-api.internal.http
  (:require [clj-http.client :as client]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [me.pmatiello.openai-api.internal.json :as json]
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
(s/def ::req-map map?)
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

(defn ^:private with-headers
  [req-map config]
  (merge req-map {:headers (config->headers config)}))

(s/fdef with-headers
  :args (s/cat :req map? :config ::specs.config/config)
  :ret ::req-map)

(defn ^:private config->http-opts [config]
  (-> config
      :http-opts
      (select-keys [:connection-timeout :socket-timeout])))

(s/fdef config->http-opts
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.config/http-opts)

(defn ^:private with-http-opts
  [req-map config]
  (merge req-map (config->http-opts config)))

(s/fdef with-http-opts
  :args (s/cat :req map? :config ::specs.config/config)
  :ret ::req-map)

(defn get!
  [path config & {:as options}]
  (let [url      (config+path->url config path)
        req-map  (-> {}
                     (with-headers config)
                     (with-http-opts config))
        response (client/get url req-map)
        body     (:body response)
        options  (merge {:parse? true} options)]
    (if (:parse? options)
      (json/read body)
      body)))

(s/fdef get!
  :args (s/cat :path ::path
               :config ::specs.config/config
               :options ::options)
  :ret ::api-response)

(defn ^:private with-body
  [req-map {:keys [body]}]
  (if body
    (merge req-map
           {:body         (json/write body)
            :content-type :json})
    req-map))

(defn ^:private ->part
  [[k v]]
  {:name    (-> k name (str/replace #"-" "_"))
   :content (if (instance? File v) v (str v))})

(defn ^:private with-multipart
  [req-map {:keys [multipart]}]
  (if multipart
    (merge req-map {:multipart (mapv ->part multipart)})
    req-map))

(defn post! [path params config]
  (let [url      (config+path->url config path)
        req-map  (-> {}
                     (with-headers config)
                     (with-http-opts config)
                     (with-body params)
                     (with-multipart params))
        response (client/post url req-map)]
    (json/read (:body response))))

(s/fdef post!
  :args (s/cat :path ::path
               :params ::params
               :config ::specs.config/config)
  :ret ::api-response)

(defn delete!
  [path config]
  (let [url      (config+path->url config path)
        req-map  (-> {}
                     (with-headers config)
                     (with-http-opts config))
        response (client/delete url req-map)]
    (json/read (:body response))))

(s/fdef delete!
  :args (s/cat :path ::path
               :config ::specs.config/config)
  :ret ::api-response)
