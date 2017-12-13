(ns ok.data.debug
  (:require [hiccup.page :refer [html5]]))

(defn- prettify
  [data]
  (with-out-str (clojure.pprint/write data)))

(defn page
  [{:keys [meta entry]}]
  (html5 [:body
          [:h1 "debug"]
          [:h2 "fsdb"]
          [:pre (prettify (get-in meta [:fsdb :manifest]))]
          [:h2 "meta"]
          [:pre (prettify meta)]]))
