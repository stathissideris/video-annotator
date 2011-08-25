(ns film-tempo.video)

(def HOUR-IN-MILLIS (* 60 60 1000))
(def MINUTE-IN-MILLIS (* 60 1000))

(defn frame-delay
  "Calculates the inter-frame delay in millis based on the fps."
  [fps]
  (/ 1000 fps))

(defn frame-to-millis
  "Converts a frame index to elapsed time in millis from the
  beginning of the video."
  [index fps]
  (* index (frame-delay fps)))

(defn frame-to-time
  "Converts a frame index to a map representing the elapsed time from
  the beginning of the video. The keys are :hours :minutes :seconds
  :millis."
  [index fps]
  (let [total (frame-to-millis index fps)
        hours (int (/ total HOUR-IN-MILLIS))
        total (rem total HOUR-IN-MILLIS)
        minutes (int (/ total MINUTE-IN-MILLIS))
        total (rem total MINUTE-IN-MILLIS)
        seconds (int (/ total 1000))
        millis (rem total 1000)]
    {:hours hours :minutes minutes :seconds seconds :millis millis}))

(defn format-time
  "Formats the passed time map (see frame-to-time) time to HH:MM:SS."
  [time]
  (format "%02d:%02d:%02d" (:hours time) (:minutes time) (:seconds time)))

