(ns ok.project
  (:require [ok.index]))

(defn- render-project
  "Renders a single project"
  [project]
  [:div.card.columns {:itemscope true
                              :itemtype "https://schema.org/softwareapplication"}
   [:div.card-divider
    [:h4 {:itemprop "name"}
     (:name project)]]
   [:div.text-center
    [:img {:src (str "/img/products/" (:image project))
           :alt (:name project)
           :itemprop "image"}]]
   [:div.card-section
    [:p
     {:itemprop "description"}
     (:content project)]]])

(defn render-collection
  "Renders a collection of projects"
  [{global-meta :meta projects :entries}]
  (ok.index/layout global-meta projects
                   [:main
                    [:div.callout.large.secondary.text-center
                     [:h2 "Products"]
                     [:h4 "Education Market"]]
                    [:div#projects.row.row.small-up-2.small-uncollapse
                     (map render-project (sort-by :position projects))]]))
