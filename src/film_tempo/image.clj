(ns film-tempo.image
  (:require clarity.utils)
  (:import [java.io File]
           [java.awt.image BufferedImage AffineTransformOp]
           [java.awt.geom AffineTransform]
           [javax.imageio ImageIO]))

(def interpolation-types
     {:bilinear AffineTransformOp/TYPE_BILINEAR
      :bicubic AffineTransformOp/TYPE_BICUBIC
      :nearest AffineTransformOp/TYPE_NEAREST_NEIGHBOR})

(defn load [filename]
  (ImageIO/read (File. filename)))

(defn show [image]
  (clarity.utils/show-comp
   (proxy [javax.swing.JPanel] []
     (paintComponent [g] (if image (.drawImage g image 0 0 this)))
     (getPreferredSize[] (if image (java.awt.Dimension.
                                    (.getWidth image)
                                    (.getHeight image))
                             (java.awt.Dimension. 100 100))))))

(defn scale
  "Scale an the image by a scaling factor. The interpolation parameter
  can be :bilinear :bicubic or :nearest."
  ([image factor]
     (scale image factor factor))
  ([image x-factor y-factor & [interpolation]]
     (let [type (if interpolation (get interpolation-types
                                       interpolation AffineTransformOp/TYPE_BILINEAR)
                    AffineTransformOp/TYPE_BILINEAR)]
       (.filter
        (AffineTransformOp.
         (doto (AffineTransform.) (.scale x-factor y-factor)) type)
      image nil))))
