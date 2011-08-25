(ns film-tempo
  (:require [clojure.contrib.miglayout :as mig]
            [clarity.component :as c]
            clarity.utils))

(defn make-player-panel []
  (let [columns 5]
    (mig/miglayout (c/make :panel)
                   :layout [:wrap columns]
                   :column "[grow,fill][grow,fill][grow,fill][grow,fill][grow,fill]"
                   ;;video area
                   (c/make :label "Film tempo v0.1" [:id :header]) [:span columns] 
                   (c/make :panel [:id :video-area]) [:span columns]
                   ;;controls
                   (mig/miglayout (c/make :panel)
                                  :column "3[]3[grow,fill]3[]3"
                                  (c/make :label "00:00" [:id :current-time-label])
                                  (c/make :slider [:id :position-slider])
                                  (c/make :label "00:00" [:id :total-time-label]))
                   [:span columns]
                   (c/make :button "[]" [:id :stop-button])
                   (c/make :button ">" [:id :play-button])
                   (c/make :button "cut" [:id :cut-button])
                   (c/make :button "x1" [:id :normal-speed-button])
                   (c/make :button "x0.5" [:id :half-speed-button])
                   (c/make :panel [:id :timeline-area]) [:span columns])))

(clarity.utils/show-comp (make-player-panel))
