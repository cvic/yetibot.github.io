(ns ok.data.projects
  (:require [ok.layout :as layout]))

(defn- project
  [project]
  [:li [:a {:href (str "/project/" (name (:key project)) ".html")}
        [:p.name (:name project)]
        (if-let [image (:image project)]
          [:img.project-thumb {:src image}])]])


(defn- sort-projects
  [projects]
  (->> projects
       (map #(assoc (last %) :key (first %)))
       (sort-by #(or (:priority %) 9999))))

(defn page
  [arg]
  (let [db (get-in arg [:meta :fsdb :manifest])
        projects (sort-projects (-> db :projects))
        disclosed (filter :name (-> projects))
        num-undisclosed (- (count projects) (count disclosed))]
    (layout/main arg
                 [:main.projects
                  [:h1 "Our projects"]
                  [:ul.projects-list
                   (map project (filter #(contains? % :name) projects))]
                  [:h3 "and " num-undisclosed " undisclosed."]])))
