(ns demo.events
  (:require [demo.db :as db]
            [re-frame.core :refer [dispatch reg-event-db reg-sub reg-fx reg-event-fx]]))

;;effects

(reg-fx
  :speak
  (fn [value]
    (.speak js/speechSynthesis (js/SpeechSynthesisUtterance. value))))

;;dispatchers

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(reg-event-db
  :say-something
  (fn [db [_ value]]
    (.speak js/speechSynthesis
            (js/SpeechSynthesisUtterance. value))
    db))

(reg-event-fx
  :say-something-properly
  (fn [cofx [_ value]]
    {:speak value}))

;;subscriptions

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
  :docs
  (fn [db _]
    (:docs db)))
