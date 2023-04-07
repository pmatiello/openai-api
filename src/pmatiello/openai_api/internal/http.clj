(ns pmatiello.openai-api.internal.http
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [pmatiello.openai-api.specs.credentials :as specs.credentials]))

(s/def ::api-response map?)
(s/def ::endpoint string?)
(s/def ::http-body map?)
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

(defn ^:private key-fn
  [orig-key]
  (-> orig-key
      (str/replace #"_" "-")
      keyword))

(defn get!
  [endpoint credentials]
  (let [headers  (credentials->headers credentials)
        response (client/get endpoint {:headers headers})]
    (json/read-str (:body response) {:key-fn key-fn})))

(s/fdef get!
  :args (s/cat :endpoint ::endpoint
               :credentials ::specs.credentials/credentials)
  :ret ::api-response)

(defn post! [endpoint body credentials]
  (let [headers   (credentials->headers credentials)
        body-json (json/write-str body)
        response  (client/post endpoint {:headers      headers
                                         :content-type :json
                                         :body         body-json})]
    (json/read-str (:body response) {:key-fn key-fn})))

(s/fdef post!
  :args (s/cat :endpoint ::endpoint
               :body ::http-body
               :credentials ::specs.credentials/credentials)
  :ret ::api-response)
