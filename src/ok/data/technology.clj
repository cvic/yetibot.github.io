(ns ok.data.technology
  (:require [ok.layout :as layout]
            [ok.shared :as shared]
            [clojure.pprint :refer [pprint]]))

(defn- html-comment [text]
  (str "<!--" text "-->"))

(defn page
  [{:keys [entry meta]}]
  (let [tech-key (keyword (:short-filename entry))
        tech (-> meta :fsdb :manifest :technologies tech-key)]
    (layout/main (first meta)
                 [:main.technology-wrapper
                  [:a.back-tech {:href "/technologies.html"}
                        (shared/back-arrow) "Technologies"]
                  [:div.technology

                  (html-comment tech)
                  (if-let [logo (:logo tech)]
                    [:img {:src (str "/img/technologies/" logo)}])

                  [:h1 (:name tech)]

                  (if-let [url (:url tech)]
                    [:a {:href url} url])

                  (if-let [text (:description tech)]
                    (shared/description2paragraphs text))]])))



;; Ideas
;;
;; * similar technologies (same category)
;; * we usually combine it with (via projects)
;; * sample projects making use of that technology
;; * services we provide employeing this technology
;; * next technology (tour)
;; * random other technology
