(ns film-tempo.vision
  (:use vision.core
        [clarity.structure :only [$]])
  (:require [film-tempo.video :as video]
            [film-tempo.gui :as gui]
            [clarity.component :as c]))

;;"/Users/sideris/Movies/some.like.it.hot.avi"
;;"/Users/sideris/Downloads/StayingInLane_MPEG4.avi"

(let [filename "/Users/sideris/Downloads/StayingInLane_MPEG4.avi"
      *image* (atom nil)
      capture (capture-from-file filename)
      frame-count (get-capture-property capture :frame-count)
      fps (get-capture-property capture :fps)
      frame-delay (video/frame-delay fps)
      player (gui/make-player-frame *image*)
      time-label ($ player :time-label)
      duration-label ($ player :duration-label)
      slider ($ player :position-slider)]

  (print "frame count:" frame-count "\n"
         "fps:" fps "\n"
         "duration:" (video/format-time (video/frame-to-time frame-count fps)) "\n")

  (.setText duration-label (video/format-time (video/frame-to-time frame-count fps)))
  
  (c/do-component slider
                  [:minimum 0]
                  [:maximum frame-count]
                  [:value 0])
  
  (dotimes [frame-index frame-count]
    (let [frame (query-frame capture)]
      (swap! *image* (fn [_] (deref (:buffered-image frame))))
      ;;(view :raw frame)
      (.setText time-label
                (video/format-time (video/frame-to-time frame-index fps)))
      (.setValue slider frame-index)
      (Thread/sleep frame-delay)))
  (release capture)
  (print "Done\n"))
