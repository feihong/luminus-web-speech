(ns demo.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [demo.ajax :refer [load-interceptors!]]
            [demo.events])
  (:import goog.History))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page @(rf/subscribe [:page])) "active")}
   [:a.nav-link {:href uri} title]])

(defn navbar []
  [:nav.navbar.navbar-dark.bg-primary.navbar-expand-md
   {:role "navigation"}
   [:button.navbar-toggler.hidden-sm-up
    {:type "button"
     :data-toggle "collapse"
     :data-target "#collapsing-navbar"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#/"} "demo"]
   [:div#collapsing-navbar.collapse.navbar-collapse
    [:ul.nav.navbar-nav.mr-auto
     [nav-link "#/" "Home" :home]
     [nav-link "#/about" "About" :about]]]])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src (str js/context "/img/warning_clojure.png")}]]]])

(defn home-page []
  [:div.container
   [:h1 "Web Speech Demo"]
   [:div "This button uses rf/dispatch and doesn't work on mobile Safari, unless you click the bottom button first"]
   [:button.btn.btn-danger {:on-click #(rf/dispatch [:say-something "This won't work on mobile"])}
                           "Speak using dispatch"]
   [:div "This button uses rf/dispatch with an effects handler"]
   [:button.btn.btn-warning {:on-click #(rf/dispatch [:say-something-properly "This won't work on mobile"])}
                         "Speak using dispatch"]
   [:div "This button uses rf/dispatch-sync and should work on mobile"]
   [:button.btn.btn-success {:on-click #(rf/dispatch-sync [:say-something-properly "This works on mobile"])}
                           "Speak using dispatch-sync"]
   [:div "This button always works because it calls speechSynthesis.speak() directly"]
   [:button.btn.btn-primary {:on-click #(.speak js/speechSynthesis (js/SpeechSynthesisUtterance.
                                                                    "This always works"))}
                            "Speak immediately"]])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page :about]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
