(ns film-tempo.vision
  (:use vision.core)
  (:require [film-tempo.video :as video]
            [film-tempo.gui :as gui]
            [clarity.component :as c]))

;;"/Users/sideris/Movies/some.like.it.hot.avi"
;;"/Users/sideris/Downloads/StayingInLane_MPEG4.avi"

(let [filename "/Users/sideris/Movies/some.like.it.hot.avi"
      *image* (atom nil)
      capture (capture-from-file filename)
      frame-count (get-capture-property capture :frame-count)
      fps (get-capture-property capture :fps)
      frame-delay (video/frame-delay fps)
      player (gui/make-player-frame *image*)]

  (print "frame count:" frame-count "\n"
         "fps:" fps "\n"
         "duration:" (video/format-time (video/frame-to-time frame-count fps)) "\n")

  (dotimes [_ frame-count]
    (let [frame (query-frame capture)]
      (swap! *image* (fn [_] (deref (:buffered-image frame))))
      ;;(view :raw frame)
      (Thread/sleep frame-delay)))
  (release capture)
  (print "Done\n"))
