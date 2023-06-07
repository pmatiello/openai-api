(ns ^:no-doc me.pmatiello.openai-api.internal.http
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [me.pmatiello.openai-api.internal.json :as json]
            [me.pmatiello.openai-api.specs.config :as specs.config])
  (:import (java.io File InputStream)))

(s/def ::api-response
  (s/or :map map?
        :string string?
        :coll-map (s/coll-of map?)))
(s/def ::as #{:stream})
(s/def ::body map?)
(s/def ::http-headers map?)
(s/def ::http-response map?)
(s/def ::multipart map?)
(s/def ::parse? boolean?)
(s/def ::path string?)
(s/def ::req-map map?)
(s/def ::url string?)
(s/def ::http-opts
  (s/keys :opt-un [::as]))
(s/def ::options
  (s/keys* :opt-un [::parse?]))
(s/def ::params
  (s/keys :opt-un [::body ::multipart ::http-opts]))

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

(defn ^:private config->http-opts
  [config]
  (-> config
      :http-opts
      (select-keys [:connection-timeout :socket-timeout])))

(s/fdef config->http-opts
  :args (s/cat :config ::specs.config/config)
  :ret ::specs.config/http-opts)

(defn ^:private with-config-http-opts
  [req-map config]
  (merge req-map (config->http-opts config)))

(s/fdef with-config-http-opts
  :args (s/cat :req map? :config ::specs.config/config)
  :ret ::req-map)

(defn ^:private params->http-opts
  [params]
  (-> params
      :http-opts
      (select-keys [:as])))

(s/fdef params->http-opts
  :args (s/cat :params ::params)
  :ret ::http-opts)

(defn ^:privat with-params-http-opts
  [req-map params]
  (merge req-map (params->http-opts params)))

(s/fdef with-params-http-opts
  :args (s/cat :req map? :params ::params)
  :ret ::req-map)

(defn ^:private as-api-response [http-response & {:as options}]
  (let [body    (:body http-response)
        options (merge {:parse? true} options)]
    (cond
      (-> options :parse? false?)
      body

      (instance? String body)
      (json/read body)

      (instance? InputStream body)
      (->> body io/reader line-seq
           (map #(re-find #"data: (\{.+\})" %))
           (map last) (filter some?)
           (map json/read)))))

(s/fdef as-api-response
  :args (s/cat :http-response ::http-response :options ::options)
  :ret ::api-response)

(defn get!
  [path config & {:as options}]
  (let [url      (config+path->url config path)
        req-map  (-> {}
                     (with-headers config)
                     (with-config-http-opts config))
        response (client/get url req-map)
        options  (merge {:parse? true} options)]
    (as-api-response response options)))

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
                     (with-config-http-opts config)
                     (with-params-http-opts params)
                     (with-body params)
                     (with-multipart params))
        response (client/post url req-map)]
    (as-api-response response)))

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
                     (with-config-http-opts config))
        response (client/delete url req-map)]
    (as-api-response response)))

(s/fdef delete!
  :args (s/cat :path ::path
               :config ::specs.config/config)
  :ret ::api-response)
