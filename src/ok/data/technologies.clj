(ns ok.data.technologies
  (:require [ok.layout :as layout]))

(defn- technology
  [tech]
  [:li [:a {:href (str "/technology/" (name (:key tech)) ".html")}
        [:img.tech-thumb {:src (str "/img/technologies/"
                                    (:logo tech "placeholder.svg"))}]
        (:name tech)]])

(defn- sort-techs
  [techs]
  (->> techs
       (map #(assoc (last %) :key (first %)))
       (sort-by #(or (:priority %) 9999))))

(defn page
  [arg]
  (let [db (get-in arg [:meta :fsdb :manifest])
        technologies (sort-techs (-> db :technologies))]
    (layout/main arg
                 [:main.technologies
                  [:h1 "Technologies"]
                  [:ul.technologies-list
                   (map technology technologies)]])))
