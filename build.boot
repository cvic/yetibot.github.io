(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources" "assets"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [perun "0.4.2-SNAPSHOT"]
                 [hiccup "1.0.5"]
                 [garden "1.3.2"]
                 [pandeiro/boot-http "0.7.3"]
                 [markdown-clj "1.0.1"]
                 [jeluard/boot-notify "0.1.2" :scope "test"]
                 [cpmcdaniel/boot-copy "1.0"]
                 [clj-http "2.3.0"]
                 [org.clojure/data.json "0.2.6"]
                 [deraen/boot-sass "0.3.1"]
                 [fsdb "0.1.1-SNAPSHOT"]
                 [camel-snake-kebab "0.4.0"]])

(require '[boot.core :as boot]
         '[io.perun :as perun]
         '[io.perun.meta :as meta]
         '[io.perun.core :as perun-core]
         '[ok.index :as index-view]
         '[ok.post :as post-view]
         '[ok.data.technology]
         '[ok.data.project]
         '[pandeiro.boot-http :refer [serve]]
         '[garden.core :refer [css]]
         '[cpmcdaniel.boot-copy :refer :all]
         '[clojure.data.json]
         '[clj-http.client]
         '[fsdb.core :as fsdb]
         '[deraen.boot-sass :refer [sass]])
;;'[jeluard.boot-notify :refer [notify]]

(deftask slack
  "Post `message` to slack."
  [u url URL str "The slack incoming webhook url"
   m message MESSAGE str "The slack message"]
  (with-post-wrap fileset
    (let [data {:username "GitLab CI"
                :text message
                :icon_emoji ":gitlabci:"}
          json (clojure.data.json/write-str data)]
      (clj-http.client/post url {:body json}))))

(task-options!
 slack {:url "https://hooks.slack.com/services/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"}
 copy {:output-dir "target/public"
       ;; TODO: Make this regexp more readable. It has three parts:
       ;;       google search console + favicon stuff + other assets.
       :matching   #{#"(google.*\.html|safari-pinned-tab\.svg|favicon\.ico|browserconfig\.xml|manifest\.json)|\.(css|js|png|jpg|svg|gif|pdf)$"}})

(defn slug-fn
  "Slugs are derived from filenames of html files. They can have a
  YYYY-MM-DD- prefix or not."
  [_ {:keys [filename]}]
  (last (re-find #"(\d+-\d+-\d+-|)(.*)\.html" filename)))

(deftask set-meta-data
  "Add :key attribute with :val value to each file metadata and also
   to the global meta"
  [k key VAL kw "meta-data key"
   v val VAL str "meta-data value"]
  (with-pre-wrap fileset
    (let [files           (meta/get-meta fileset)
          global-meta     (meta/get-global-meta fileset)
          updated-files   (map #(assoc % key val) files)
          new-global-meta (assoc global-meta key val)
          updated-fs      (meta/set-meta fileset updated-files)]
      (meta/set-global-meta updated-fs new-global-meta))))

(deftask categories
  "Add :categories of all posts to the meta-data"
  []
  (with-pre-wrap fileset
    (let [files           (meta/get-meta fileset)
          global-meta     (meta/get-global-meta fileset)
          categories      (filter #(:category %) files)
          updated-files   (map #(assoc % :categories categories) files)
          new-global-meta (assoc global-meta :categories categories)
          updated-fs      (meta/set-meta fileset updated-files)]
      (meta/set-global-meta updated-fs new-global-meta))))

(defn category-grouper
  [entries]
  (reduce (fn [result entry]
            (let [category (:category entry)
                  path (str category ".html")]
              (-> result
                  (update-in [path :entries] conj entry)
                  (assoc-in [path :entry :category] category))))
          {}
          entries))

(deftask fsdb
  "Loads fsdb and adds it as meta to the fileset."
  [f fsdb FSDB str "fsdb path"]
  (with-pre-wrap fileset
    (->> (fsdb/read-tree "manifest")
         ;; have to use :io.perun.global here to have it merged into
         ;; static's meta
         (assoc-in (meta fileset) [:io.perun.global :fsdb])
         (with-meta fileset))))

(deftask print-global-meta
  []
  (with-pre-wrap fileset
    (perun-core/report-info "print-global-meta" (meta fileset))
    fileset))

(defn my-grouper [group-key global-meta entries]
  (let [items (-> global-meta :fsdb :manifest group-key)]
    ;; TODO `apply merge map`, feels like it should use `into`
    (apply merge (map (fn [[key item]]
                        {(str (name key) ".html")
                         {:entries []
                          :entry nil
                          :meta global-meta
                          :meta-entry item}}) items))))

(deftask global-assortment
  "Renders an assortment using global metadata"
  [o out-dir    OUTDIR     str   "the output directory"
   r renderer   RENDERER   sym   "page renderer (fully qualified symbol resolving to a function)"
   k group-key  GROUPKEY   kw    "key on which to group"]
  (fn [next-task]
    (fn [fileset]
      (let [global-meta (io.perun.meta/get-global-meta fileset)
            task-fn (perun/assortment-task {:task-name "global-assortment"
                                            :renderer renderer
                                            :out-dir out-dir
                                            :filterer identity
                                            ;;:extensions [".html"]
                                            :sortby :date-published
                                            :comparator (fn [i1 i2] (compare i2 i1))
                                            :tracer :your.ns/global-assortment
                                            :grouper (partial my-grouper group-key global-meta)})]
        ((task-fn next-task) fileset)))))

(deftask build
  "Build the 200ok page."
  []
  (let [is-of-type? (fn [{:keys [permalink]} doc-type] (.startsWith permalink (str "/" doc-type)))]
    (comp
     (perun/markdown)
     (perun/draft)
     ;;(print-meta)
     (perun/slug :slug-fn slug-fn)
     (perun/ttr)
     (categories)
     (perun/word-count)
     (perun/build-date)
     (perun/gravatar
      :source-key :author-email
      :target-key :author-gravatar)

     (perun/collection
      :renderer 'ok.index/render
      :page "blog.html"
      ;; Order pages in reverse chronological order
      :sortby #(:date-created %)
      :comparator #(.compareTo %2 %1)
      :filterer #(is-of-type? % "posts"))

     (perun/collection
      :renderer 'ok.project/render-collection
      :page "projects.html"
      :filterer #(is-of-type? % "projects"))

     (fsdb)
     ;;(print-global-meta)

     (perun/static :renderer 'ok.data.index/page        :page "index.html")
     (perun/static :renderer 'ok.data.people/page       :page "people.html")
     (perun/static :renderer 'ok.data.technologies/page :page "technologies.html")
     (perun/static :renderer 'ok.data.clients/page      :page "clients.html")
     (perun/static :renderer 'ok.data.services/page     :page "services.html")
     (perun/static :renderer 'ok.data.projects/page     :page "projects.html")
     (perun/static :renderer 'ok.data.opensourced/page  :page "opensource.html")

     (global-assortment :renderer 'ok.data.technology/page
                        :out-dir "public/technology"
                        :group-key :technologies)

     (global-assortment :renderer 'ok.data.project/page
                        :out-dir "public/project"
                        :group-key :projects)

     ;; TODO omit for prod
     (perun/static :renderer 'ok.data.debug/page        :page "debug.html")

     (perun/collection
      :renderer 'ok.person/render-collection
      :page "team.html"
      :filterer #(is-of-type? % "team"))

     (perun/collection
      :renderer 'ok.index/render
      :page "open-source.html"
      :filterer #(is-of-type? % "open-source"))

     ;; Groups all posts that have a :category (yes, only a single one
     ;; atm) into one file. For example a post with a :category of
     ;; "emacs" will be rendered into a file
     ;; "public/category/emacs.html" together with every other post
     ;; with the same tag.
     (perun/assortment :renderer 'ok.index/render
                       :grouper category-grouper
                       :out-dir "public/category"
                       :filterer #(is-of-type? % "posts"))

     ;; renders each md file in posts into its own page
     (perun/render :renderer 'ok.post/render
                   :filterer #(is-of-type? % "posts"))

     (perun/render :renderer 'ok.page/render
                   :filterer #(is-of-type? % "audio-book"))

     (perun/sitemap)
     ;;(print-global-meta)
     (perun/atom-feed :filterer #(is-of-type? % "posts")
                      ;; setting base-url explicitly is a quickfix for
                      ;; a perun bug of some sort
                      :base-url "http://200ok.ch/")

     (perun/static :renderer 'ok.static/letsencrypt
                   :page ".well-known/acme-challenge/zN70yAoyS5KLi097MJd11yzKm0SKLGVDCU2lDAARsx0")

     ;;(notify)
     ;;(print-meta)
     (sass)
     (target)
     (copy))))

(deftask dev
  []
  (comp
   (watch)
   (perun/global-metadata)
   (set-meta-data :key :target
                  :val "dev")
   (build)
   (serve :dir "target/public")))

(deftask prod
  []
  (comp
   ;;(slack :message "<http://200ok.ch|200ok.ch> has been updated.")
   (perun/global-metadata)
   (set-meta-data :key :target
                  :val "prod")
   (build)))
