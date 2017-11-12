(ns quantum-four.views
  (:require [re-frame.core :as rf]
            [quantum-four.subs :as subs]))

(defn add-row-element [vect]
  (loop [i       0
         sub-vec []
         new-vec []]
    (if (< i (count vect))
      (if (= 0 (rem i 7))
        (recur (inc i) [:tr (vect i)] (conj new-vec sub-vec))
        (recur (inc i) (conj sub-vec (vect i)) new-vec))
      (rest new-vec))))

(defn main-panel []
  (let [current-board (rf/subscribe [::subs/board])
        board-numbers (for [i (range 7)
                            j (range 6)]
                           [j i])
        space-value #(name (get-in @current-board [(% 0) (% 1)]))
        board-vector (vec (map #(vector :td (space-value %)) board-numbers))]
    [:div
     [:h1 "Quantum Four"]
     [:div
      [:table
       (add-row-element board-vector)]]]))