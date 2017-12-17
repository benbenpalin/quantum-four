(ns quantum-four.views
  (:require [re-frame.core :as rf]
            [quantum-four.subs :as subs]
            [quantum-four.events :as events]))

(defn add-row-element [vect]
  "Given a vector of [:td _] vectors, this function adds :tr to groups of 7"
  (loop [i       0
         sub-vec []
         new-vec []]
    (if (< i (count vect))
      (if (= 0 (rem i 7))
        (recur (inc i) ^{:key i}[:tr (vect i)] (conj new-vec sub-vec))
        (recur (inc i) (conj sub-vec (vect i)) new-vec))
      (rest (conj new-vec sub-vec)))))

(defn circle [value]
  (case value
    :e [:div]
    :r [:div.red]
    :b [:div.black]))

(defn make-space [space board active?]
  "Given a space and a board, this function creates the HTML for the table cell,
   it's action, and it's value"
  (let [space-value (get-in board space)]
    (vector :td
            (when active?
              {:on-click #(rf/dispatch [::events/select-column space])})
            (circle space-value))))

(defn table-board []
  "Creates the HTML for the game table that is seen by the players"
  (let [current-board @(rf/subscribe [::subs/board])
        winner?       @(rf/subscribe [::subs/active])
        board-numbers (for [i (range 6)
                            j (range 7)]
                           [i j])
        board-vector (vec (map #(make-space % current-board winner?) board-numbers))]
    [:table>tbody
     (add-row-element board-vector)]))

(defn game-chooser []
  [:div.chooser "What do you want to play?"
   [:div {:on-click #(rf/dispatch [::events/choose-game :quaint])} "Quaint"]
   [:div {:on-click #(rf/dispatch [::events/choose-game :quasi])}  "Quasi"]
   [:div {:on-click #(rf/dispatch [::events/choose-game :quantum])} "Quantum"]])

(defn main-panel []
  (let [chosen? @(rf/subscribe [::subs/game-chosen])
        active? @(rf/subscribe [::subs/active])]
    [:div
     [:h1 "Quantum Four"]
     (if-not chosen?
       [game-chooser chosen?]
       [:div
        [:div.board
         [table-board]]
        [:div.below
         [:p "Turn: "
          (let [turn @(rf/subscribe [::subs/turn])]
            (if (= turn :r)
              "Red"
              "Black"))]
         [:p "Alerts: " @(rf/subscribe [::subs/alert])]
         (if-not active?
           [:p
            {:on-click #(rf/dispatch [::events/play-again])}
            "Play again"])]])]))