(ns film-tempo.core
  (:require [clarity.utils :as utils])
  (:use rosado.processing)
  (:import (processing.core PApplet)))

;; here's a function which will be called by Processing's (PApplet)
;; draw method every frame. Place your code here. If you eval it
;; interactively, you can redefine it while the applet is running and
;; see effects immediately

(defn fancy-draw
  "An example of a function which does *something*."
  [dst]
  (background-float (rand-int 256) (rand-int 256) (rand-int 256))
  (fill-float (rand-int 125) (rand-int 125) (rand-int 125))
  (ellipse 100 100 (rand-int 90) (rand-int 90))
  (stroke-float 10)
  (line 10 10 (rand-int 150) (rand-int 150))
  (no-stroke)
  (filter-kind INVERT)
  (framerate 10))

;; below, we create an PApplet proxy and override setup() and draw()
;; methods. Then we put the applet into a window and display it.

(def my-applet
     (proxy [PApplet] []
       (setup []
              (binding [*applet* this]
                (size 200 200)
                (smooth)
                (no-stroke)
                (fill 226)
                (framerate 10)))
       (draw []
             (binding [*applet* this]
               (fancy-draw this)))))

(.init my-applet)

(utils/show-comp my-applet)
