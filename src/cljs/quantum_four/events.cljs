(ns quantum-four.events
  (:require [re-frame.core :as rf]
            [quantum-four.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

;(rf/reg-event-fx
;  ::player-move)

(rf/reg-event-db
  ::change-turn
  (fn [db _]
    (let [old-turn (:turn db)
          new-turn (if (= old-turn :r) :b :r)]
      (assoc db :turn new-turn))))

(defn update-space [space board]
  ())

(rf/reg-event-fx
  ::select-column
  (fn [{:keys [db]} [_ space]]
    {:db (update db :board #(assoc-in % space (:turn db)))
     :dispatch [::change-turn]}))
