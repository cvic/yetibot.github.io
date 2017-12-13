(ns ok.helper)

(defn stylesheet-link-tag [href]
  [:link {:rel "stylesheet" :href href}])

(defn image-tag [src]
  [:img {:src src}])
