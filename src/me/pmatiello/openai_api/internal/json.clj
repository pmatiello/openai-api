(ns ^:no-doc me.pmatiello.openai-api.internal.json
  (:refer-clojure :exclude [read])
  (:require [clojure.data.json :as json]
            [clojure.string :as str]))

(defn ^:private json->clj-keys
  [orig-key]
  (-> orig-key
      (str/replace #"_" "-")
      keyword))

(defn read [payload]
  (json/read-str payload {:key-fn json->clj-keys}))

(defn ^:private clj->json-keys
  [orig-key]
  (-> orig-key
      name
      (str/replace #"-" "_")))

(defn write [payload]
  (json/write-str payload {:key-fn clj->json-keys}))
