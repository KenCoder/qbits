(ns qbits.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [qbits.gear :as b]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(def item-counts (reagent/atom {:cutting-board 1}))

;; -------------------------
;; Views
(defn gear-list []
  [:div
   [:h1 "Patrol Box"]
   (let [counts @item-counts]
     (for [
           [category items] b/patrol-box]
       ^{:key category}
       [:div
        [:h2 category]
        [:table.table.table-bordered
         (for [{:keys [:gear_id :description :count]} items
               :let [current-count (gear_id counts nil)]]
           ^{:key gear_id}
           [:tr {:class (cond (= current-count nil) ""
                              (= current-count count) "success"
                              (< current-count count) "danger"
                              :else "warning")}
            [:td
             [:button.btn.btn-xs {:type "button" :on-click #(swap! item-counts (fn [x] (assoc x gear_id count)))}
              [:span.glyphicon.glyphicon-ok {:aria-hidden "true"}]]
             [:button.btn.btn-xs {:type "button" :on-click #(swap! item-counts (fn [x] (assoc x gear_id (inc (gear_id x 0)))))}
              [:span.glyphicon.glyphicon-plus {:aria-hidden "true"}]]
             [:button.btn.btn-xs {:type "button" :on-click #(swap! item-counts (fn [x] (assoc x gear_id (max 0 (dec (gear_id x 0))))))}
              [:span.glyphicon.glyphicon-minus {:aria-hidden "true"}]]
             ]
            [:td
             [:span (str count " " description)]]
            [:td
             [:span (if (nil? current-count) "" (str current-count " found"))]]
            ])]]))
   ])

(defn home-page []
  [:div [:h2 "Welcome to qbits"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About qbits"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
                    (session/put! :current-page #'gear-list))

(secretary/defroute "/about" []
                    (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
