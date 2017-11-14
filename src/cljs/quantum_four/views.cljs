(ns quantum-four.views
  (:require [re-frame.core :as rf]
            [quantum-four.subs :as subs]
            [quantum-four.events :as events]))

(defn add-row-element [vect]
  "adds :tr to every 7th td"
  (loop [i       0
         sub-vec []
         new-vec []]
    (if (< i (count vect))
      (if (= 0 (rem i 7))
        (recur (inc i) [:tr (vect i)] (conj new-vec sub-vec))
        (recur (inc i) (conj sub-vec (vect i)) new-vec))
      (rest new-vec))))

(defn make-space [space board]
  (vector :td
          {:on-click #(rf/dispatch [::events/change-turn])}
          (get-in board space)))

(defn main-panel []
  (let [current-board @(rf/subscribe [::subs/board])
        board-numbers (for [i (range 7)
                            j (range 6)]
                           [j i])
        board-vector (vec (map #(make-space % current-board) board-numbers))]
    [:div
     [:h1 "Quantum Four"]
     [:div
      [:table>tbody
       (add-row-element board-vector)]
      [:p "Turn: "
       (let [turn @(rf/subscribe [::subs/turn])]
         (if (= turn :r)
           "Red"
           "Black"))]]]))
