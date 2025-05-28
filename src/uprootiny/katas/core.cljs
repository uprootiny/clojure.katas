(ns uprootiny.katas.core
  (:require [reagent.core :as r]))

(defn main-component []
  [:div "Welcome to uprootiny.katas!"])

(defn init []
  (r/render [main-component]
            (.getElementById js/document "app")))