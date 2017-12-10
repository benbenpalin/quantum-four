(ns quantum-four.events
  (:require [re-frame.core :as rf]
            [quantum-four.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(defn same-color? [board space orig-color]
  (let [space-color (get-in board space)]
    (and (= space-color orig-color)
         (not= space-color :e))))

(defn new-space-super [old-space direction]
  (let [new-space (vec (map + old-space direction))]
    (when (seq new-space)
      (cond
        (>= (second new-space) 7) (assoc new-space 1 0)
        (<= (second new-space) -1) (assoc new-space 1 6)
        :else new-space))))

(defn four-in-a-row? [board space game]
  (let [color (get-in board space)
        get-new-space (case game
                        :standard #(map + %1 %2)
                        :super new-space-super)]
    (loop [directions  [[0 1]
                        [-1 1]
                        [-1 0]
                        [-1 -1]
                        [0 -1]
                        [1 -1]
                        [1 0]
                        [1 1]]
           starting-space space
           connections 1]
      (let [new-space (get-new-space starting-space (first directions))]
        (if (seq directions)
          (if (not= connections 4)
            (if (same-color? board new-space color)
              (recur directions new-space (inc connections))
              (recur (next directions) space 1))
            true)
          false)))))

(defn winning-move? [board game]
  (let [spaces (for [i (range 6)
                     j (range 7)]
                 [i j])]
    (as-> spaces s
          (map #(four-in-a-row? board % game) s)
          (set s)
          (s true))))

(rf/reg-event-fx
  ::check-board
  (fn [{:keys [db]}]
    (let [colors {:r "Red" :b "Black"}]
      (if (winning-move? (:board db) (:game db))
        {:db (assoc db :alert (str ((:turn db) colors) " Wins!") :active false)}
        {:dispatch [::change-turn]}))))

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
         :dispatch [::check-board]}
        {:db (assoc db :alert "This column is full, select a different one")}))))
