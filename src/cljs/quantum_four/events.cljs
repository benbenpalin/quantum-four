(ns quantum-four.events
  (:require [re-frame.core :as rf]
            [quantum-four.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(defn same-color? [board space orig-color]
  (let [space-color (get-in board space)]
    (and (= orig-color space-color)
         (not= space-color :e))))

(defn four-in-a-row? [board space]
  (let [color (get-in board space)]
    (loop [directions  [[0 1]
                        [-1 1]
                        [-1 0]
                        [-1 -1]
                        [0 -1]
                        [1 -1]
                        [1 0]
                        [1 1]]
           direction   (first directions)
           new-space   (map + space direction)
           connections 1]
      (if (seq directions)
        (if (not= connections 4)
          (if (same-color? board new-space color)
            (recur directions direction (map + new-space direction) (inc connections))
            (recur (next directions) (first (next directions)) (map + space (first (next directions))) 1))
          true)
        false))))

(defn winning-move? [board]
  (let [spaces (for [i (range 6)
                     j (range 7)]
                 [i j])]
    (as-> spaces s
          (map #(four-in-a-row? board %) s)
          (set s)
          (s true))))


(rf/reg-event-db
  ::change-turn
  (fn [db _]
    (let [old-turn (:turn db)
          new-turn (if (= old-turn :r) :b :r)]
      (assoc db :turn new-turn :alert ""))))

(defn find-empty-space [[orig-row column] board]
  "Given a space and a board, this function returns lowest empty space in the column"
  (loop [row 6]
    (if (>= row 0)
      (if (not= :e (get-in board [row column]))
        (recur (dec row))
        [row column])
      "full row")))

(rf/reg-event-fx
  ::select-column
  (fn [{:keys [db]} [_ space]]
    (let [empty-space (find-empty-space space (:board db))]
      (if (not= empty-space "full row")
        {:db (update db :board #(assoc-in % empty-space (:turn db)))
         :dispatch [::change-turn]}
        {:db (assoc db :alert "This column is full, select a different one")}))))
