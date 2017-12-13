(ns ok.index
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5)])
  (:require [clojure.string :as s]
            [ok.shared :as shared]
            [ok.john :as john]
            [clojure.data.json :as json]))

(defn stylesheet-link-tag [href]
  [:link {:rel "stylesheet" :href href}])

(defn image-tag [src]
  [:img {:src src}])

;; Returns the publish date or a fallback if no :date-created is
;; provided. This, however, is always a lie, because the
;; last-modified-time is whenever the page has been built.
(defn- date-of-post
  "Returns the last-updated-at date of a post"
  [post]
  (if (:date-created post)
    (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
             (:date-created post))
    (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
             (java.util.Date.
              (.lastModified
               (clojure.java.io/file
                (str "resources/" (:path post))))))))

(defn- ok-metadata
  []
  [:div {:itemscope true
         :itemprop "publisher"
         :itemtype "https://schema.org/Organization"}
   [:span
    [:meta {:itemprop "name"
            :content "Yertibot Corp"}]]
   [:span {:itemscope true
           :itemprop "logo"
           :itemtype "https://schema.org/ImageObject"
           }
    [:meta {:itemprop "url"
            :content "https://yetibot.github.io/img/logo.png"}]]
   [:span
    [:meta {:itemprop "legalName"
            :content "Yetibot Corp"}]]])

(defn- authors
  "Get author(s). If there are none, return 'YetibotCorp'."
  [post]
  (s/split (or (:authors post) "YetibotCorp") #"\s*,\s*"))

(defn- author-section
  [post]
  (for [author (authors post)]
    [:section.author {:itemscope true
                      :itemprop "author"
                      :itemtype "https://schema.org/Person"}
     [:span {:itemprop "name"}
      author]]))


(defn- category-section
  [post]
  (if (:category post)
    [:li.category

     [:a {:href (str "/category/" (:category post) ".html")}
      (:category post)]]))

(defn- date-of-post-section
  [post]
  [:time {:itemprop "datePublished"}
   (date-of-post post)])

(defn- word-count-section
  [post]
  [:span {:itemprop "wordCount"}
   (:word-count post)])

(defn- time-to-read-section
  [post]
  [:span {:itemprop "timeRequired"}
   (:ttr post)])

(defn- subheader-post
  "Returns metadata of a post inside a :div.subheader"
  [post]
  [:div.subheader
    [:p.post-meta
   (date-of-post-section post)
   " - "
   (word-count-section post)
   " words"
   " - "
   (time-to-read-section post)
   " min read"
   (ok-metadata)]
   [:div.byline
    [:img.author-icon {:src "/img/author.svg"}]
   (author-section post)]
  ;  (category-section post)
   ])

(defn- separated-tags
  "Get tag(s). If there are none, return 'YetibotCorp'."
  [post]
  (s/split (or (:tags post) "YetibotCorp") #"\s*,\s*"))

;; TODO: Refactor this to yield a unique keyword list
;; TODO: Find out how to yield the 'category' as focus keyword
(defn- tags
  "Renders tags and category into the footer of a post"
  [post]
  [:div.tags
     [:img.tag-icon {:src "/img/tag.svg"}]
   [:ul {:itemprop "keywords"}
    (category-section post)
    (for [tag (separated-tags post)]
    [:li.tag tag])]])

(defn full-post [post]
  [:div
   [:div.article-body {:itemprop "articleBody"} (:content post)]
   (tags post)])

(defn- preview-post
  "Returns the first 100 words of a post wrapped in a :section.
   Optionally with a link to 'Read more'"
  [post]
  [:section
   [:div.article-section {:itemprop "articleSection"}
    (as-> (:content post) %
      (s/split % #"<pre>")
      (first %)
      (s/split % #" ")
      (take 100 %)
      (s/join " " %))
    "..."
    (tags post)
    [:p
     [:a.read-more {:href (:permalink post)
          :itemprop "url"}
      "Read more..."]]]])

(defn- image-meta-data
  "Add image meta data for the Yetibot logo."
  []
  [:span {:itemscope true
          :itemprop "image"
          :itemtype "https://schema.org/ImageObject"}

   [:meta {:itemprop "height"
           :content "190"}]
   [:meta {:itemprop "width"
           :content "349"}]
   [:meta {:itemprop "url"
           :content "https://yetibot.github.io/img/logo.png"}]])

(defn render-post
  "Renders a post as :article"
  [post & {:keys [max]}]
  [:article.blog-post {:itemscope true
                       :itemtype "https://schema.org/BlogPosting"}
   [:h3.headline {:itemprop "headline"}
    [:a.nunito {:href (:permalink post)
         :itemprop "url"}
     (:title post)]]
   (subheader-post post)
   ;; Right now every post will get the Yetibot logo as image meta data.
   ;; This could be optimized: If a post actually uses a picture, this
   ;; could yield the meta data. For the moment the solution is good,
   ;; though, since schema.org requires a picture per BlogPosting.
   (image-meta-data)
   (let [content (:content post)
         words (:word-count post)]
     (if (or (nil? max) (> max words))
       (full-post post)
       (preview-post post)))])

(defn- categories
  "Add categories to sidebar"
  [global-meta]
  [:div.sidebar
   [:div
    [:h5.categories "Categories"]
    [:ul
     (let [freqs (frequencies (map :category (:categories global-meta)))
           sorted (-> (sort-by second freqs)
                      reverse)]
       (map (fn [[category occurrences]]
              [:li [:a {:href (str "/category/" category ".html") }
                    (clojure.string/capitalize category)
                    [:span.badge occurrences]]])
            sorted))]]])

(defn scripts []
  [:div.scripts {:style {:display "none"}}
   [:script {:src "/js/highlight.pack.js"}]
   [:script {:src "/js/tour.js"}]
   [:script "hljs.initHighlightingOnLoad();"]])

(defn layout [global-meta posts content]
  (html5
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
    (stylesheet-link-tag "https://fonts.googleapis.com/css?family=Abril+Fatface")
    (stylesheet-link-tag "/css/normalize.css")
    ; (stylesheet-link-tag "/css/foundation.css")
    (stylesheet-link-tag "/css/styles/solarized-light.css")
    (stylesheet-link-tag "/css/app.css")]

   [:body {:id "blog"
           :itemscope true
           :itemtype "http://schema.org/Blog"}
    [:div.top-bar
     [:div.top-bar-left
      [:top-bar-title {:itemprop "image"}
       [:a#logo {:href "/"}
        (image-tag "/img/yetibot_mark_blue.svg")]]]
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
    (if (= (:target global-meta)
           "prod")
      [:script {:src "/js/ga.js"}])]))


(defn render [{global-meta :meta posts :entries}]
  (layout global-meta posts
          [:main
           [:div.content {:id "content"}
            ; [:div.blog-column
             (for [post posts]
               (render-post post :max 100))]
            (categories global-meta)]))
