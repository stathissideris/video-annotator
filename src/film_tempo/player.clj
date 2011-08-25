(ns film-tempo.player
  (:import [java.net
            MalformedURLException
            URL
            URI]
            [javax.media
            ControllerEvent
            ControllerListener
            Manager
            NoPlayerException
            Player
            RealizeCompleteEvent]))

(defn create-player [filename]
  (javax.media.Manager/createPlayer (URL. filename)))

#_(.exists (java.io.File. (URI. "file:///Users/sideris/Downloads/sample_mpeg4.mp4")))

#_(def player (create-player "file:///Users/sideris/Downloads/sample_mpeg4.mp4"))
#_(.start player)
