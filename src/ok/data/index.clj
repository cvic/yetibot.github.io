(ns ok.data.index
  (:require [ok.layout :as layout]))

(defn page [arg]
  (let [db (get-in arg [:meta :fsdb :manifest])
        num-people      (-> db :people count)
        num-techs       (-> db :technologies count)
        num-clients     (-> db :clients count)
        num-services    (-> db :services count)
        num-projects    (-> db :projects count)
        ;;num-plugins     ("42")
        num-opensourced (->> db :projects vals (filter :opensourced) count)]
    (layout/main
     arg
     [:main.teaser-wrapper
      [:div.teaser
       [:span
        [:span "You can see Yetibot as a communal command line (*). It supports "]
        ;; TODO: 'team.html' is the old code that's sourced from
        ;; markdown files. 'people.html' is the new page that's
        ;; sourced through fsdb. For now, we're pragmatic and use the
        ;; team page, because we can deploy it faster. At some point,
        ;; 'team.html' will be deprecated in favour of 'people.html'.
        ;; [:a {:href "people.html"}
        [:a {:href "team.html"}
         num-people " databases"]]
       [:span
        [:span ", "]
        [:a {:href "technologies.html"}
          "over 9000 custom commands,"]]
       [:span
        [:span " working with "]
        [:a {:href "services.html"}
         num-services " APIs,"]]
       [:span
        [:span " and "]
        [:a {:href "opensource.html"}
         "an unlimited number of custom observers."]]
       [:div.supporting
        "* works with Slack and IRC... and it's Docker ready"]]])))
