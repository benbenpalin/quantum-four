(ns quantum-four.events
  (:require [re-frame.core :as re-frame]
            [quantum-four.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))
