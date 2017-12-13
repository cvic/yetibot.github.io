(ns ok.data.opensourced
  (:require [ok.layout :as layout]))

(defn- opensource
  [project]
  [:li [:a {:href (str "/project/" (name (:key project)) ".html")}
        [:p.name (:name project)]
        (if-let [image (:image project)]
          [:img.project-thumb {:src image}])]
   [:p.description (:description project)]])

(defn- sort-projects
  [projects]
  (->> projects
       (map #(assoc (last %) :key (first %)))
       (sort-by #(or (:priority %) 9999))))

(defn page
  [arg]
  (let [db (get-in arg [:meta :fsdb :manifest])
        projects (sort-projects (-> db :projects))
        opensourced (filter :opensourced (-> projects))]
    (layout/main arg
                  [:main.projects
                  [:h1 "Projects We&rsquo;ve Open-Sourced"]
                  [:ul.projects-list
                   (map opensource opensourced)]])))
