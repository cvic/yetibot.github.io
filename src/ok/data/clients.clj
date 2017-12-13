(ns ok.data.clients
  (:require [ok.layout :as layout]))

(defn- client [client]
  [:li
   [:a {:href (:url client)
        :target "_blank"}
    [:img
     {:src (str "/img/clients/" (:logo client))}]]])

(defn page [arg]
  (let [db (get-in arg [:meta :fsdb :manifest])
        clients (sort-by :position (-> db :clients vals))]
    (layout/main arg
                 [:main.clients
                  [:h1 "Our distinguished clients"]
                  [:ul.clients-list
                   (map client clients)]])))
