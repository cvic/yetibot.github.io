(ns ok.data.people
  (:require [ok.layout :as layout]))

(defn- person [person]
  [:li.team-member 
    [:h3 (:name person)]
    [:p.title (:title person)]
    [:p.description (:description person)]])

(defn page [arg]
  (let [db (get-in arg [:meta :fsdb :manifest])
        everybody (-> db :people vals)
        public (sort-by :position (filter :position everybody))
        num-more (- (count everybody) (count public))]

    (layout/main arg
                 [:main
                  [:h1 "Our Team & Network"]
                  [:ul.team (map person public)]
                  [:h3 "and " num-more " more."]])))
