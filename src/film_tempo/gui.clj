(ns film-tempo.gui
  (:require [clojure.contrib.miglayout :as mig]
            [clarity.component :as c]
            clarity.utils))

(defn image-panel [image]
  (let [p (proxy [javax.swing.JPanel] []
            (paintComponent [g] (if @image (.drawImage g @image 0 0 this)))
            (getPreferredSize[] (if @image (java.awt.Dimension.
                                            (.getWidth @image)
                                            (.getHeight @image))
                                    (java.awt.Dimension. 100 100))))]
    (add-watch image "image-watch" (fn [k r o n] (.repaint p)))
    p))

(defn make-player-panel [image]
  (mig/miglayout (c/make :panel)
                 :layout [:wrap 5]
                 :column "[grow,fill][grow,fill][grow,fill][grow,fill][grow,fill]"
                 ;;video area
                 (c/make :label "Film tempo v0.1" [:id :header]) :span
                 (image-panel image) :span
                 ;;controls
                 (mig/miglayout (c/make :panel)
                                :column "3[]3[grow,fill]3[]3"
                                (c/make :label "00:00" [:id :current-time-label])
                                (c/make :slider [:id :position-slider])
                                (c/make :label "00:00" [:id :total-time-label]))
                 :span
                 (c/make :button "[]" [:id :stop-button])
                 (c/make :button ">" [:id :play-button])
                 (c/make :button "cut" [:id :cut-button])
                 (c/make :button "x1" [:id :normal-speed-button])
                 (c/make :button "x0.5" [:id :half-speed-button])
                 (c/make :panel [:id :timeline-area]) :span))

(defn make-player-frame [image]
  (clarity.utils/show-comp (make-player-panel image)))

#_(clarity.utils/show-comp (make-player-panel))
