(ns film-tempo.gui
  (:require [clojure.contrib.miglayout :as mig]
            [clarity.component :as c]
            clarity.utils)
  (:import java.awt.Color))

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
                 :layout [:wrap 7] ;:debug
                 :column "[grow,fill][grow,fill][grow,fill][grow,fill][grow,fill]"
                 (c/make :label "Video Annotator v0.1"
                                        [:id :header]
                                        [:font :size "x1.5"])
                 :span

                 (c/make :button "new" [:id :new-button] [:enabled false])
                 (c/make :button "load" [:id :load-button])
                 (c/make :button "save" [:id :save-button])
                 :wrap
                 
                 ;;video area
                 (mig/miglayout (c/make :panel)
                                :layout [:insets 0]
                                :column "[center]10px[fill,grow]"
                                (c/do-component (image-panel image)
                                                [:background Color/black])
                                (c/scroll-pane
                                 (c/make :list [:id :annotation-list])) :growy)
                 :span :growy
                 ;;controls
                 (mig/miglayout (c/make :panel)
                                :layout [:insets 0]
                                :column "3[]3[grow,fill]3[]3"
                                (c/make :label "00:00" [:id :time-label])
                                (c/make :slider [:id :position-slider])
                                (c/make :label "00:00" [:id :duration-label]))
                 :span
                 (c/make :button "||" [:id :pause-button])
                 (c/make :button ">" [:id :play-button])
                 (c/make :button "<<" [:id :skip-back-button])
                 (c/make :button ">>" [:id :skip-forward-button])
                 (c/make :button "x1" [:id :normal-speed-button])
                 (c/make :button "x0.5" [:id :half-speed-button])
                 (c/make :button "cut" [:id :cut-button])

                 (c/make :panel [:id :timeline-area]) :span
                 (c/make :label [:id :status-line]) :span))

(defn make-player-frame [image]
  (clarity.utils/show-comp (make-player-panel image)))

#_(clarity.utils/show-comp (make-player-panel))
