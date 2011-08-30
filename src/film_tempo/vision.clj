(ns film-tempo.vision
  (:use vision.core
        [clarity.structure :only [$]]
        clarity.list)
  (:require [film-tempo.video :as video]
            [film-tempo.gui :as gui]
            [film-tempo.annotations :as ann]
            [clarity.component :as c]
            [clarity.event :as event]
            clarity.menu
            [clojure.contrib.swing-utils :as swing])
  (:import [java.awt.event KeyEvent]
           [javax.swing JComponent KeyStroke AbstractAction JFileChooser]))

;;"/Users/sideris/Movies/some.like.it.hot.avi"
;;"/Users/sideris/Downloads/StayingInLane_MPEG4.avi"
;;"/Users/sideris/Movies/breaking bad/Breaking.Bad.s03ep12.Half_Measures.HDTV.avi"

(defn frame-pos [capture]
  (get-capture-property capture :pos-frames))

(def frame-pos-lock :dummy)

(defn set-frame-pos [capture pos]
  (locking frame-pos-lock
    (set-capture-property capture :pos-frames (max (int pos) 0))))

(defn advance-frame-pos [capture frames]
  (set-frame-pos capture (+ (frame-pos capture) frames)))

(defn retreat-frame-pos [capture frames]
  (set-frame-pos capture (- (frame-pos capture) frames)))

(defn time-millis []
  (java.lang.System/currentTimeMillis))

(defn annotations-to-list [annotations fps]
  (map #(str (video/format-time (video/frame-to-time % fps))
             " (f" (int %) ")") (map :start (:entries annotations))))

(let [filename "/Users/sideris/Movies/some.like.it.hot.avi"
      *image* (atom nil)
      capture (capture-from-file filename)
      frame-count (get-capture-property capture :frame-count)
      fps (get-capture-property capture :fps)
      normal-frame-delay (video/frame-delay fps)
      *frame-delay* (atom normal-frame-delay)
      *speed-label* (atom "x1")
      *play-status* (atom :play)
      jump-frames (video/time-to-frame 10000 fps)

      annotations (atom (ann/make-annotations filename fps frame-count))
      
      player (gui/make-player-frame *image*)
      time-label ($ player :time-label)
      duration-label ($ player :duration-label)
      slider ($ player :position-slider)
      slider-listener (event/listener
                       :change
                       (:on-state-changed
                        (set-frame-pos capture (.getValue slider))))
      status-line ($ player :status-line)
      set-image (fn [frame] (reset! *image* (deref (:buffered-image frame))))
      update-time-display
      #(swing/do-swing
        (let [f (frame-pos capture)]
          (.setText time-label (video/format-time (video/frame-to-time f fps)))
          (doto slider
            (.removeChangeListener slider-listener)
            (.setValue f)
            (.addChangeListener slider-listener))
          (.setText status-line (str filename
                                     " - Speed: " @*speed-label*
                                     " (f" (int f) ")"))))
      frame-before-pause (atom 0)

      play-button ($ player :play-pause-button)
      play #(do (reset! *play-status* :play)
                (.setText play-button "||"))
      pause #(do (reset! frame-before-pause (frame-pos capture))
                 (reset! *play-status* :pause)
                 (.setText play-button ">"))
      
      pack (delay (.pack player))]

  (.addChangeListener slider slider-listener)
  
  #_(print "frame count:" frame-count "\n"
         "fps:" fps "\n"
         "duration:" (video/format-time (video/frame-to-time frame-count fps)) "\n")

  (.setText duration-label (video/format-time (video/frame-to-time frame-count fps)))

  ;;keyboard short cuts:
  ;; / for cut
  ;; P for pause/play
  ;; 1,2,3,4 for speeds
  
  (let [set-speed
        (fn [factor]
          (reset! *frame-delay* (* normal-frame-delay (/ 1 factor)))
          (reset! *speed-label* (str factor)))]
    
    (c/do-component ($ player :normal-speed-button)
                    [:on-click (set-speed 1)])    
    (c/do-component ($ player :half-speed-button)
                    [:on-click (set-speed 1/2)])
    (c/do-component ($ player :quarter-speed-button)
                    [:on-click (set-speed 1/4)])
    (c/do-component ($ player :eighth-speed-button)
                    [:on-click (set-speed 1/8)])
    (c/do-component ($ player :normal-speed-button)
                    (event/add-window-shortcut KeyEvent/VK_1 0
                                               (set-speed 1))
                    (event/add-window-shortcut KeyEvent/VK_2 0
                                               (set-speed 1/2))
                    (event/add-window-shortcut KeyEvent/VK_3 0
                                               (set-speed 1/4))
                    (event/add-window-shortcut KeyEvent/VK_4 0
                                               (set-speed 1/8))))

  (doto ($ player :panel)
    (event/add-window-shortcut
     KeyEvent/VK_P 0
     (if (= :pause @*play-status*) (play) (pause)))
    (event/add-window-shortcut
     KeyEvent/VK_LEFT 0
     (if (= :pause @*play-status*)
       (do
         (println "Frame before pause:" @frame-before-pause)
         (set-frame-pos capture (swap! frame-before-pause dec))
         (println "Frame set to:" (frame-pos capture))
         (let [frame (query-frame capture)]
           (if frame
             (set-image frame)
             (update-time-display))))))
    (event/add-window-shortcut
     KeyEvent/VK_RIGHT 0
     (if (= :pause @*play-status*)
       (do
         (set-frame-pos capture (swap! frame-before-pause inc))
         (let [frame (query-frame capture)]
           (if frame
             (set-image frame)
             (update-time-display)))))))
  
  (c/do-component
   ($ player :skip-back-button)
   [:on-click
    (retreat-frame-pos capture jump-frames)])
    ;;(set-frame-pos capture 0)])

  (c/do-component
   ($ player :skip-forward-button)
   [:on-click
    (advance-frame-pos capture jump-frames)])

  (c/do-component
   ($ player :play-pause-button)
   (:on-click
    (if (= :pause @*play-status*)
      (do (play))
      (do (pause)))))
  
  (c/do-component
   slider
   [:minimum 0]
   [:maximum frame-count])
  
  (let [add-cut #(do (swap! annotations ann/add-cut (frame-pos capture))
                     (swing/do-swing ;;not entirely correct, should be the index added
                      (.ensureIndexIsVisible ($ player :annotation-list)
                                             (dec (count (:entries @annotations)))))
                     (println "Cut added"))]
    (c/do-component ($ player :cut-button) (:on-click (add-cut)))
    (event/add-window-shortcut ($ player :cut-button) KeyEvent/VK_C 0 (add-cut))
    (event/add-window-shortcut ($ player :cut-button) KeyEvent/VK_SLASH 0 (add-cut)))
  
  (c/do-component
   player
   (:on-window-closing
    (pause)
    (println "Video decoding stopped")))

  (c/do-component
   ($ player :annotation-list)
   (:model (mutable-list-model
            annotations
            #(annotations-to-list % fps)))
   (:on-click
    (if (= 2 (.getClickCount event))
      (let [index (.locationToIndex this (.getPoint event))
            frame (:start (nth (:entries @annotations) index))]
        (set-frame-pos capture frame)))))

  (c/do-component
   ($ player :delete-annotation-button)
   (:on-click
    (let [l ($ player :annotation-list)
          i (.getSelectedIndex l)]
      (if (not= -1 i)
        (swap! annotations ann/remove-annotation i)))))
  
  (c/do-component
   ($ player :save-button)
   (:on-click
    (pause)
    (ann/save-annotations @annotations (c/choose-file nil :save))
    (play))
   (event/add-window-shortcut KeyEvent/VK_S 0 (.doClick this))) ;;<-doesn't work

  (c/do-component
   ($ player :load-button)
   (:on-click
    (pause)
    (reset! annotations (ann/load-annotations (c/choose-file)))
    (play)))
  
  #_(.setJMenuBar player
                  (clarity.menu/menu-bar
                   "File"
                   ["Annotate new video"
                    "Load annotations"
                    "Save annotations"
                    "Save annotations as"]))
  
  (while (not= :stopped @*play-status*)
    (let [time-before-decode (time-millis)
          frame (if (not= :pause @*play-status*)
                  (query-frame capture))]
      (if (= :play @*play-status*)
        (set-image frame))
      (update-time-display)
      (force pack)
      (let [time-taken-to-decode (- (time-millis) time-before-decode)
            sleep-time (- @*frame-delay* time-taken-to-decode)]
        (if (> sleep-time 0)
          (Thread/sleep sleep-time)))))
  (release capture)
  (print "Done\n"))
