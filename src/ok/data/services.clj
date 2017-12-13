(ns ok.data.services
  (:require [ok.layout :as layout]
            [ok.shared :as shared]
            [camel-snake-kebab.core :refer :all]))

(defn- service [service]
  [:li
    [:img.services-thumb {:src (str "/img/services/" (->kebab-case (:name service)) ".svg")}]
   [:h2 (:name service)]
   [:p.description
    (shared/description2paragraphs (:description service))]])

(defn page [arg]
  (let [db (get-in arg [:meta :fsdb :manifest])
        services (-> db :services vals)]
    (layout/main arg
                 [:main.services
                  [:h1 "Our Offerings"]
                  [:ul.services-list
                   (map service services)]
                  [:a.button.secondary {:href "mailto:info@200ok.ch"} "Get in touch"]])))
