(ns film-tempo.mp4
  (:use byte-spec)
  (:import [java.io RandomAccessFile]))

(defspec mp4-atom
  :n-bytes :int32
  :type    [:byte 4]
  :bytes   [:byte 4])

;;http://msdn.microsoft.com/en-us/library/ms779636.aspx

(declare riff-list riff-chunk)

(defn read-lists-and-chunks [data]
  (let [magic (read-fstring 4)]
    (seek-by -4)
      (if (= "LIST" magic)
        (spec-read riff-list)
        (assoc (spec-read riff-chunk) :magic :chunk))))

(defspec riff-chunk
  :id     [:fstring 4]
  :n-data :int32-l
  :data   [:byte])

(defspec riff-list
  :magic  [:fstring 4]
  :n-data :int32-l
  :type   [:fstring 4]
  :data   read-lists-and-chunks)

(defspec riff-header
  :magic     [:fstring 4]
  :file-size :int32-l
  :file-type [:fstring 4]
  :lists-and-chunks read-lists-and-chunks)

(defn spec-read-file [spec filename]
  (with-open [file-input-stream (RandomAccessFile. filename "r")]
    (binding [*spec-in* file-input-stream]
      (spec-read spec))))

(print (spec-read-file riff-header "/Users/sideris/Movies/some.like.it.hot.avi"))
