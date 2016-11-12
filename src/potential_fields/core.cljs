(ns potential-fields.core
  (:require [monet.canvas :as canvas]
            [cljs.pprint :as pprint]))

(enable-console-print!)

(defn hypot [x y]
  (js/Math.hypot x y))

(defn round [n]
  (js/Math.round n))

(println "Started.")

(defonce canvas-dom (.getElementById js/document "canvas"))

;; (defonce monet-canvas (canvas/init canvas-dom "2d"))

(defrecord potential-map [w h fields])

(defrecord potential-field [x y potential gradation])

(def tile-size 16)

(defn rgba [r g b a]
  (pprint/cl-format nil "rgba(~d, ~d, ~d, ~d)" r g b a))

(defn potential-field-value [field x y]
  (let [fx (:x field)
        fy (:y field)
        distance (hypot (- fx x) (- fy y))
        potential (:potential field)]
    (if (pos? potential)
      (max 0 (round (- potential (* (/ distance tile-size) (:gradation field)))))
      (min 0 (round (+ potential (* (/ distance tile-size) (:gradation field))))))))

(defn potential-value [world x y]
  (reduce + (map #(potential-field-value % x y) (:fields world))))

(defn potential-color [world x y]
  (let [potential (potential-value world x y)]
    (if (pos? potential)
      (rgba 0 potential 0 1)
      (rgba (- potential) 0 0 1))))

(defn draw-map [ctx world]
  (doseq [x (range 0 (:w world) tile-size)
          y (range 0 (:h world) tile-size)]
    (-> ctx
        (canvas/fill-style (potential-color world x y))
        (canvas/fill-rect {:x x :y y :w  tile-size :h tile-size}))))

(defonce world-map (->potential-map 1280 720
                                [(->potential-field 200 200 -256 64)
                                 (->potential-field 600 100 256 8)
                                 (->potential-field 400 300 -256 128)]))

(defrecord agent [x y speed])

(defonce player (atom (->agent 300 300 10)))

(defn draw-agent [ctx agent]
  (-> ctx
      (canvas/fill-style "#fff")
      (canvas/fill-rect {:x (:x @agent) :y (:y @agent) :w 32 :h 32})))

(defn find-direction [agent]
  (let [speed (:speed @agent)]
    (first
     (sort-by
      (fn [[dx dy]]
        (- (potential-value world-map
                            (max 0 (+ (:x @agent) dx))
                            (max 0 (+ (:y @agent) dy)))))
      (map (fn [[dx dy]] [(* speed dx) (* speed dy)])
           [[-1 -1] [0 -1] [+1 -1]
            [-1 +0]        [+1 +0]
            [-1 +1] [0 +1] [+1 +1]])))))

(defn update-agent [agent dt]
  (let [[dx dy] (find-direction agent)]
    (swap! agent update :x #(+ % dx))
    (swap! agent update :y #(+ % dy)))
  nil)

(defn agent-move-to [agent x y]
  (swap! agent assoc-in [:x] x)
  (swap! agent assoc-in [:y] y))


(defn tick [ctx]
  ;; (update-agent player 0)
  (draw-map ctx world-map)
  (draw-agent ctx player)
  (js/requestAnimationFrame #(tick ctx)))


(defn start []
  (let [ctx (canvas/get-context canvas-dom "2d")]
    (tick ctx)))

;; (defn start []
;;   (canvas/restart monet-canvas)
;;   (canvas/add-entity monet-canvas :world-map
;;                      (canvas/entity world-map nil draw-map))
;;   (canvas/add-entity monet-canvas :player
;;                      (canvas/entity player #'update-agent draw-agent))
;;   (reagent/render [app] (js/document.getElementById "main")))

(start)
