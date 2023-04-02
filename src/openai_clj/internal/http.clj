(ns openai-clj.internal.http
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
