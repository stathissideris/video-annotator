(ns film-tempo.timeline
  (:use clojure.contrib.trace)
  (:require [film-tempo.video :as video]
            [clarity.style :as style]))

#_(def i (java.awt.image.BufferedImage.
          1300 50
          java.awt.image.BufferedImage/TYPE_INT_RGB))
#_(clarity.utils/watch-image
					  (fn [] (let [g (.getGraphics i)]
								  (draw-timeline g [(.getWidth i)(.getHeight i)])
								  i)))

(def *background-colors* [(style/color 0 0 120)
                          (style/color 50 50 160)])
(def *cuts-color* (style/color 30 200 30))

(defn- size
  ([dimension] [(.getWidth dimension) (.getHeight dimension)])
  ([w h] (java.awt.Dimension. w h)))

(defn- half [x] (/ x 2))

(defn- focusing-on [start end fps]
  (let [start-millis (video/frame-to-millis start fps)
        end-millis (video/frame-to-millis end fps)
        diff (- end-millis start-millis)]
    (cond (<= diff 2000) :frames
          (and (> diff 2000) (<= diff video/MINUTE-IN-MILLIS)) :seconds
          (and (> diff video/MINUTE-IN-MILLIS) (<= diff video/HOUR-IN-MILLIS)) :minutes
          :else :tens-of-minutes)))

(defn- time-unit-in-pixels [] 50)

(defn draw-background [g [w h] start end duration fps]
  (let [bg (cycle *background-colors*)
        focusing-on (focusing-on start end fps)
        unit-width (time-unit-in-pixels)]
    ;;draw light backgrounds
    (.setColor g (second *background-colors*))
    (dotimes [x (inc (int (half (/ w unit-width))))]
      (.fillRect g (* x unit-width 2) 0 unit-width h))))

(defn frame-pixel-position [cut width start end]
  (let [frame-duration (- end start)
        frame-offset (- cut start)]
    (/ (* frame-offset width) frame-duration)))

(defn draw-cuts [g [w h] start end data]
  (.setColor g *cuts-color*)
  (doseq [cut data]
    (let [pos (frame-pixel-position cut w start end)]
      (.drawLine g pos 0 pos (dec h)))))

(defn draw-timeline [g [w h]]
  (let [title "cuts"
        start 0
        end 361
        duration 30000
        fps 30
        data [10 20 30 50 200 300]
        units true
        font-h 12
        main-h (if units (- h font-h 2) h)]
    (.clearRect g 0 0 w h)

    ;;background
    (.setColor g (first *background-colors*))
    (.fillRect g 0 0 w h)
    (if units
      (do (.setColor g (second *background-colors*))
          (.drawLine g 0 main-h w main-h)))
    (draw-background g [w main-h] start end duration fps)

    (if title
      (do (.setColor g (style/color :white))
          (.drawString g title 10 15)))

    ;;(.drawString g (name (focusing-on start end fps)) 10 30)
    
    ;;draw cuts
    (draw-cuts g [w main-h] start end data)))
