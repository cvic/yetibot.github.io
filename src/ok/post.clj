(ns ok.post
  (:require [ok.index])
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5)]))


(defn render [{global-meta :meta posts :entries post :entry}]
  (ok.index/layout global-meta posts
                   [:main.single-post
                    [:div#content
                     (ok.index/render-post post)]]))
