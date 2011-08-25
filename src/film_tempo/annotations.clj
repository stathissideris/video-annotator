(ns film-tempo.annotations)

(defn make-annotations [filename]
  {:video filename
   :entries []})

(defn add-cut [annotations position]
  (assoc annotations :entries
         (conj (:entries annotations)
               {:type :cut
                :start position})))

(defn add-fade [annotations start end]
  (assoc annotations :entries
         (conj (:entries annotations)
               {:type :fade
                :start start
                :end end})))

(defn add-dissolve [annotations start end]
  (assoc annotations :entries
         (conj (:entries annotations)
               {:type :dissolve
                :start start
                :end end})))

(defn add-note
  ([annotations subject text start end]
     (assoc annotations :entries
            (conj (:entries annotations)
                  {:type :note
                   :start start
                   :end end
                   :subject subject
                   :text text})))
  ([annotations subject text position]
     (add-note annotations subject text position nil)))

(defn save-annotations [annotations filename]
  (spit filename (with-out-str (pprint-json annotations))))

(defn load-annotations [filename]
  (read-json-from (slurp filename) true false []))
