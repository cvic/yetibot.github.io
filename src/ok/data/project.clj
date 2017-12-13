(ns ok.data.project
  (:require [ok.layout :as layout]
            [ok.shared :as shared]
            [clojure.pprint :refer [pprint]]))

(defn- html-comment [text]
  (str "<!--" text "-->"))

(defn page
  [{:keys [entry meta]}]
  (let [proj-key (keyword (:short-filename entry))
        proj (-> meta :fsdb :manifest :projects proj-key)]
    (layout/main (first meta)
                 [:main.project-wrapper
                  [:a.back-tech {:href "/projects.html"}
                   (shared/back-arrow) "Projects"]
                  [:div.project

                   (html-comment proj)

                   [:h1 (:name proj)]

                   [:p
                    ;; TODO: Don't user :center, but styling!
                    [:center
                     (if-let [image (:image proj)]
                       [:img {:src image}])]]

                   (if-let [url (:url proj)]
                     [:a {:href url} url])

                   (if-let [incubated (:incubated proj)]
                     [:p "Incubated: "
                      incubated])

                   (if-let [status (:incubator-status proj)]
                     [:p "Status: "
                      status])

                   (if-let [oss (:opensourced proj)]
                     [:p "Opensourced: "
                      oss])


                   (if-let [text (:description proj)]
                     (shared/description2paragraphs text))

                   (if-let [technologies (:technologies proj)]
                     [:div
                      [:h2 "Technologies"]
                      (map (fn [tech]
                             ;; TODO: Not all of these links will
                             ;; work, because some techs are not yet
                             ;; in technologies.yml and other
                             ;; technologies are actually 200ok
                             ;; projects and have a different URL!
                             [:span.tech [:a {:href (str "/technology/"
                                                         tech
                                                         ".html")}
                                          tech]])
                           technologies)])]])))
