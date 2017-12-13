(ns ok.shared
  (:use [markdown.core]
        [hiccup.core :only (html)]
        [hiccup.page :only (html5)]))

(defn description2paragraphs [text]
  ;; `md-to-html-string` would not pick up the newlines to create
  ;; paragraphs on it's own.
  (map (fn [text]
         (md-to-html-string text))
       (clojure.string/split text #"\n")))

(defn render-footer []
  [:footer
   [:div
    {:itemscope true
     :itemprop "publisher"
     :itemtype "https://schema.org/Organization"}
    [:div.name{:itemprop "name"}
                  "Yetibot Corporation"]
    [:div{:itemprop "address"
                  :itemscope true
                  :itemtype "https://schema.org/PostalAddress"}
     [:a {:href "https://www.google.ro/maps/place/Antarctica/@-82.862752,134.9978113,17z/data=!3m1!4b1!4m5!3m4!1s0xb09dff882a7809e1:0xb08d0a385dc8c7c7!8m2!3d-82.862752!4d135" :title "View on Google Maps"}
      [:div{:itemprop "streetAddress"}
       "McMurdo Station"]
      [:div
       [:span{:itemprop "postalCode"}
        "9999999999"]
       " "
       [:span{:itemprop "addressLocality"}
        "Antarctica City"]]]]
    [:div{:itemprop "telephone"}
                  "+9999999999999999"]
    [:div{:itemprop "email"}
     [:a {:href "mailto:contact@yetibot"}
      "contact@yetibot"]]]])

(defn back-arrow []
  [:svg#back-arrow {:x "0px", :y "0px", :viewbox "0 0 30 30"}
    [:path {:d "M26.5,11.5H12.7l4.1-4.1c1.3-1.3,1.3-3.4,0-4.7c-1.3-1.3-3.4-1.3-4.7,0l-12,12c-0.2,0.2-0.2,0.5,0,0.7l12,12\n\tc0.6,0.6,1.5,1,2.4,1s1.7-0.3,2.4-1c1.3-1.3,1.3-3.4,0-4.7l-4.1-4.1h13.8c1.9,0,3.5-1.6,3.5-3.5S28.4,11.5,26.5,11.5z"}]])
