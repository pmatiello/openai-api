(ns pmatiello.openai-api.internal.http
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as str]))

(defn ^:private credentials->headers
  [{:keys [api-key org-id]}]
  (if org-id
    {"Authorization"       (str "Bearer " api-key)
     "OpenAI-Organization" org-id}
    {"Authorization" (str "Bearer " api-key)}))

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

(defn post! [endpoint body credentials]
  (let [headers   (credentials->headers credentials)
        body-json (json/write-str body)
        response  (client/post endpoint {:headers      headers
                                         :content-type :json
                                         :body         body-json})]
    (json/read-str (:body response) {:key-fn key-fn})))
