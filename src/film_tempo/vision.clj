(ns film-tempo.vision
  (:use vision.core)
  (:require [film-tempo.video :as video]))

;;"/Users/sideris/Movies/some.like.it.hot.avi"
;;"/Users/sideris/Downloads/StayingInLane_MPEG4.avi"

(let [filename "/Users/sideris/Movies/some.like.it.hot.avi"
      capture (capture-from-file filename)
      frame-count (get-capture-property capture :frame-count)
      width (get-capture-property capture :frame-width)
      height (get-capture-property capture :frame-height)
      fps (get-capture-property capture :fps)]

  (print "frame count:" frame-count "\n"
         "w:" width "\n"
         "h:" height "\n"
         "fps:" fps "\n"
         "duration:" (video/format-time (video/frame-to-time frame-count fps)) "\n")

  #_(dotimes [_ frame-count]
    (let [frame (query-frame capture)]
      ;;(print (.getHeight (force (:buffered-image frame))))
      (view :raw frame)
      (Thread/sleep 10)))
  #_(release capture)
  (print "Done\n"))
