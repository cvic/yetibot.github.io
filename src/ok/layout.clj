(ns ok.layout
  (:require [hiccup.page :as hiccup]
            [ok.helper :as helper]
            [ok.shared :as shared]
            [ok.john :as john]
            [clojure.data.json :as json]))

(defn- scripts
  []
  [:div.scripts {:style {:display "none"}}
   [:script {:src "/js/tour.js"}]
   [:script {:src "/js/highlight.pack.js"}]
   [:script "hljs.initHighlightingOnLoad();"]])

(defn main
  "The 'main' layout, which is used for every page."
  [global-meta content]
  (hiccup/html5
   {:lang "en" }
   (john/mccarthy)
   [:head
    [:title (:site-title global-meta)]
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible"
            :content "IE=edge,chrome=1"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
    [:link {:rel "apple-touch-icon", :sizes "180x180", :href "/apple-touch-icon.png"}]
    [:link {:rel "icon", :type "image/png", :href "/favicon-32x32.png", :sizes "32x32"}]
    [:link {:rel "icon", :type "image/png", :href "/favicon-16x16.png", :sizes "16x16"}]
    [:link {:rel "manifest", :href "/manifest.json"}]
    [:link {:rel "mask-icon", :href "/safari-pinned-tab.svg", :color "#5bbad5"}]
    [:meta {:name "theme-color", :content "#ffffff"}]
    (helper/stylesheet-link-tag "https://fonts.googleapis.com/css?family=Abril+Fatface")
    (helper/stylesheet-link-tag "/css/normalize.css")
    (helper/stylesheet-link-tag "/css/app.css")]

   [:body {:itemscope true
           :itemtype "http://schema.org/Blog"}
    [:div.top-bar
     [:div.top-bar-left
      [:top-bar-title {:itemprop "image"}
       [:a#logo {:href "/"}
        (helper/image-tag "/img/yetibot_mark_grey.svg")]]]
     [:div.top-bar-right
      [:ul.menu
       [:li [:a {:href "/blog.html"} "Blog"]]
       [:li [:a {:href "/projects.html"} "Docs"]]
;;       [:li [:a {:href "/team.html"} "Team"]]
       [:li [:a {:href "/atom.xml"} [:img {:id "atom-feed"
                                           :src "/img/rss.svg"}]]]
       ;; [:li [:a
       ;;       {:href "/introduction_to_modern_frontend_and_backend_development/index.html"}
       ;;       "Audio Book"]]
       ;; [:li [:a {:href "/projects.html"} "Projects"]]
       ;; [:li [:a {:href "/open-source.html"} "Open Source"]]
       ]]]
    ;;[:div.callout.large.primary
    ;; [:div.row.column.text-center
    ;;  [:h4.nunito (:description global-meta)]
    ;;  [:span.nunito (:subdescription global-meta)]]]
    content
    (shared/render-footer)
    (scripts)
    [:script (str "tour=" (json/write-str (get-in global-meta [:meta :fsdb :manifest :tour])))]
    (when (= (:target global-meta) "prod")
      [:script {:src "/js/ga.js"}]
      [:script {:src "/js/tawkto.js"}])]))
