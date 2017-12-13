(ns ok.person
  (:require [ok.index]))

(defn- render-person
  "Renders a single person"
  [person]
  [:div.team-member
   {:itemscope true
    :itemtype "https://schema.org/person"}
   [:div.team-picture
    [:img {:src (:image person)
           :alt (str (:name person) " Profile Picture")
           :itemprop "image"}]]
   [:div
    [:h3 (:name person)]
    [:p.title (:title person)]
    [:p.team-description
     {:itemprop "description"}
     (:content person)]
    [:div
     [:a {:href (str "mailto:" (:email person))}
      [:span {:itemprop "email"} (:email person)]]]]])

(defn render-collection
  "Renders a collection of persons"
  [{global-meta :meta persons :entries}]
  (ok.index/layout global-meta persons
                  [:main.team
                   [:div#content.row
                    (map render-person (sort-by :position persons))]]))
