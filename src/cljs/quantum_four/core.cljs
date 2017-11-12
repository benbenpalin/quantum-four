(ns quantum-four.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [quantum-four.events :as events]
            [quantum-four.views :as views]
            [quantum-four.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
