(ns uprootiny.katas.sequences
  "Katas exploring sequence transformations and lazy evaluation"
  (:require [clojure.string :as str]))

;; ============================================================================
;; SEQUENCE FUNDAMENTALS
;; ============================================================================

(defn fibonacci
  "Generate infinite fibonacci sequence using lazy evaluation"
  ([] (fibonacci 0 1))
  ([a b] (lazy-seq (cons a (fibonacci b (+ a b))))))

(defn primes
  "Generate infinite sequence of prime numbers using the Sieve of Eratosthenes"
  []
  (let [sieve (fn sieve [s]
                (lazy-seq
                 (let [p (first s)]
                   (cons p (sieve (filter #(not= 0 (mod % p)) (rest s)))))))]
    (sieve (iterate inc 2))))

(defn collatz
  "Generate the Collatz sequence for a given number"
  [n]
  (lazy-seq
   (cons n
         (when (not= n 1)
           (if (even? n)
             (collatz (/ n 2))
             (collatz (+ (* 3 n) 1)))))))

;; ============================================================================
;; TRANSFORMATION PATTERNS
;; ============================================================================

(defn sliding-window
  "Create sliding windows of size n over sequence s"
  [n s]
  (->> s
       (partition n 1)
       (map vec)))

(defn group-by-runs
  "Group consecutive identical elements"
  [coll]
  (->> coll
       (partition-by identity)
       (map #(vector (first %) (count %)))))

(defn transpose
  "Transpose a matrix (sequence of sequences)"
  [matrix]
  (apply map vector matrix))

(defn unfold
  "Generate sequence by repeatedly applying f to seed until pred is true"
  [f pred seed]
  (lazy-seq
   (when-not (pred seed)
     (cons seed (unfold f pred (f seed))))))

;; ============================================================================
;; KATA CHALLENGES
;; ============================================================================

(defn kata-1-word-frequency
  "Count word frequencies in text, returning sorted map"
  [text]
  (->> text
       str/lower-case
       (re-seq #"\w+")
       frequencies
       (sort-by val >)
       (into {})))

(defn kata-2-pascal-triangle
  "Generate first n rows of Pascal's triangle"
  [n]
  (take n
        (iterate (fn [row]
                   (vec (map +
                             (concat [0] row)
                             (concat row [0]))))
                 [1])))

(defn kata-3-anagram-groups
  "Group words by anagrams"
  [words]
  (->> words
       (group-by #(sort (str/lower-case %)))
       vals
       (filter #(> (count %) 1))))

(defn kata-4-longest-increasing-subsequence
  "Find length of longest increasing subsequence"
  [coll]
  (if (empty? coll)
    0
    (let [dp (vec (repeat (count coll) 1))]
      (reduce (fn [dp i]
                (reduce (fn [dp j]
                          (if (< (nth coll j) (nth coll i))
                            (assoc dp i (max (dp i) (inc (dp j))))
                            dp))
                        dp
                        (range i)))
              dp
              (range 1 (count coll)))
      (apply max dp))))

(defn kata-5-lazy-merge
  "Merge multiple sorted lazy sequences into single sorted sequence"
  [& sequences]
  (lazy-seq
   (let [heads (keep #(when (seq %) (first %)) sequences)
         tails (map rest sequences)]
     (when (seq heads)
       (let [min-val (apply min heads)
             new-seqs (map (fn [seq head]
                             (if (= head min-val)
                               (rest seq)
                               seq))
                           sequences heads)]
         (cons min-val (apply kata-5-lazy-merge new-seqs)))))))

;; ============================================================================
;; MATHEMATICAL SEQUENCES
;; ============================================================================

(defn catalan-numbers
  "Generate Catalan numbers using recurrence relation"
  []
  (lazy-seq
   (cons 1
         (map (fn [n]
                (/ (->> (range n)
                        (map #(* (nth (catalan-numbers) %)
                                (nth (catalan-numbers) (- n 1 %))))
                        (apply +))
                   (inc n)))
              (iterate inc 1)))))

(defn pentagonal-numbers
  "Generate pentagonal numbers: n(3n-1)/2"
  []
  (map #(/ (* % (- (* 3 %) 1)) 2) (iterate inc 1)))

(defn look-and-say
  "Generate the 'Look and Say' sequence"
  [start]
  (lazy-seq
   (cons start
         (look-and-say
          (->> start
               str
               (partition-by identity)
               (mapcat #(list (count %) (first %)))
               (apply str))))))

;; ============================================================================
;; UTILITY FUNCTIONS FOR VISUALIZATION
;; ============================================================================

(defn take-until
  "Take elements until predicate is true (inclusive)"
  [pred coll]
  (lazy-seq
   (when-let [s (seq coll)]
     (if (pred (first s))
       [(first s)]
       (cons (first s) (take-until pred (rest s)))))))

(defn sequence-stats
  "Compute statistics for finite sequence"
  [coll]
  (when (seq coll)
    {:count (count coll)
     :sum (apply + coll)
     :mean (/ (apply + coll) (count coll))
     :min (apply min coll)
     :max (apply max coll)}))