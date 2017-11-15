(ns quantum-four.events
  (:require [re-frame.core :as rf]
            [quantum-four.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-db
  ::change-turn
  (fn [db _]
    (let [old-turn (:turn db)
          new-turn (if (= old-turn :r) :b :r)]
      (assoc db :turn new-turn))))

(defn find-empty-space [[orig-row column] board] ;;TODO make an alert if row is full instead of just forcing in the original value
  "Given a space and a board, this function returns lowest empty space in the column"
  (loop [row 6]
    (if (>= row 0)
      (if (not= :e (get-in board [row column]))
        (recur (dec row))
        [row column])
      [orig-row column])))

(rf/reg-event-fx
  ::select-column
  (fn [{:keys [db]} [_ space]]
    {:db (update db :board #(assoc-in % (find-empty-space space (:board db)) (:turn db)))
     :dispatch [::change-turn]}))
