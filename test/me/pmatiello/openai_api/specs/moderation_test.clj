(ns me.pmatiello.openai-api.specs.moderation-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [me.pmatiello.openai-api.specs.moderation :as specs.moderation]))

(def ^:private params
  {:input "kittens"})

(def ^:private classification
  {:id      "modr-77COTW32sqZlj5OdsTQ1oigQjZUoS"
   :model   "text-moderation-004"
   :results [{:flagged         false
              :categories      {:sexual           false
                                :hate             false
                                :violence         false
                                :self-harm        false
                                :sexual/minors    false
                                :hate/threatening false
                                :violence/graphic false}
              :category-scores {:sexual           0.02671145834028721
                                :hate             8.750141569180414E-5
                                :violence         1.9700456732607563E-7
                                :self-harm        5.328976704355171E-10
                                :sexual/minors    9.594355105946306E-6
                                :hate/threatening 2.624464534584092E-10
                                :violence/graphic 4.069304893050685E-9}}]})

(deftest params-test
  (s/valid? ::specs.moderation/params params))

(deftest classification-test
  (s/valid? ::specs.moderation/classification classification))
