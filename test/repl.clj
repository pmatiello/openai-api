(require '[pmatiello.openai-api.api :as api])
(require '[clojure.java.io :as io])
(require '[clojure.spec.alpha :as s])
(require '[clojure.java.browse :refer [browse-url]])
(require '[clojure.spec.test.alpha :as stest])

(stest/instrument)

(def ^:private api-key
  (or (System/getenv "OPENAI_API_KEY") (read-line)))

(def credentials
  (api/credentials api-key))

(api/models credentials)
(s/valid? :pmatiello.openai-api.specs.model/description-list *1)

(api/model "gpt-3.5-turbo" credentials)
(s/valid? :pmatiello.openai-api.specs.model/description *1)

(api/completion {:model "ada" :prompt "hello"} credentials)
(s/valid? :pmatiello.openai-api.specs.completion/result *1)
