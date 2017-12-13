(ns ok.page
  (:require [ok.index])
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5)]))

(defn- render-page
  "Renders a page as :article"
  [page & {:keys [max]}]
  [:article.page {:itemscope true
                  :itemtype "https://schema.org/WebPage"}
   [:h3 {:itemprop "headline"}
    [:a {:href (:permalink page)
         :itemprop "url"}
     (:name page)]]
   [:div {:itemprop "text"} (:content page)]])

(defn render [{global-meta :meta pages :entries page :entry}]
  (ok.index/layout global-meta pages
                   [:main
                    [:div#content.row
                     (render-page page)]]))
