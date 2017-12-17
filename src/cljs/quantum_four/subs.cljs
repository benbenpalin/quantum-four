(ns quantum-four.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::board
 (fn [db]
   (:board db)))

(rf/reg-sub
  ::turn
  (fn [db]
    (:turn db)))

(rf/reg-sub
  ::alert
  (fn [db]
    (:alert db)))

(rf/reg-sub
  ::active
  (fn [db]
    (:active db)))

(rf/reg-sub
  ::game-chosen
  (fn [db]
    (:game-chosen db)))