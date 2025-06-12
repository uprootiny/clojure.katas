(ns uprootiny.katas.mathematics
  "Katas exploring mathematical structures and algebraic patterns")

;; ============================================================================
;; MONOIDS AND SEMIGROUPS
;; ============================================================================

(defprotocol Semigroup
  "Associative binary operation"
  (combine [x y]))

(defprotocol Monoid
  "Semigroup with identity element"
  (identity-element [_]))

;; String concatenation monoid
(extend-type string
  Semigroup
  (combine [x y] (str x y))
  Monoid
  (identity-element [_] ""))

;; Addition monoid for numbers
(def addition-monoid
  (reify
    Semigroup
    (combine [_ x y] (+ x y))
    Monoid
    (identity-element [_] 0)))

;; Multiplication monoid for numbers  
(def multiplication-monoid
  (reify
    Semigroup
    (combine [_ x y] (* x y))
    Monoid
    (identity-element [_] 1)))

;; List concatenation monoid
(def list-monoid
  (reify
    Semigroup
    (combine [_ x y] (concat x y))
    Monoid
    (identity-element [_] '())))

(defn fold-monoid
  "Fold sequence using monoid operations"
  [monoid coll]
  (reduce (partial combine monoid)
          (identity-element monoid)
          coll))

;; ============================================================================
;; FUNCTORS AND APPLICATIVES
;; ============================================================================

(defprotocol Functor
  "Mappable container"
  (fmap [container f]))

(defprotocol Applicative
  "Functor with application"
  (pure [_ value])
  (apply-f [container f]))

;; Maybe functor
(defrecord Some [value])
(defrecord None [])

(def some ->Some)
(def none (->None))

(extend-type Some
  Functor
  (fmap [container f] (some (f (:value container))))
  Applicative
  (pure [_ value] (some value))
  (apply-f [container f]
    (if (instance? Some f)
      (some ((:value f) (:value container)))
      none)))

(extend-type None
  Functor
  (fmap [container f] none)
  Applicative
  (pure [_ value] (some value))
  (apply-f [container f] none))

;; ============================================================================
;; NUMBER THEORY KATAS
;; ============================================================================

(defn gcd
  "Greatest common divisor using Euclidean algorithm"
  [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))

(defn lcm
  "Least common multiple"
  [a b]
  (/ (* a b) (gcd a b)))

(defn extended-gcd
  "Extended Euclidean algorithm: returns [gcd, x, y] where ax + by = gcd"
  [a b]
  (if (zero? b)
    [a 1 0]
    (let [[g x1 y1] (extended-gcd b (mod a b))
          x (- y1 (* (quot a b) x1))
          y x1]
      [g x y])))

(defn mod-inverse
  "Modular multiplicative inverse"
  [a m]
  (let [[g x _] (extended-gcd a m)]
    (when (= g 1)
      (mod x m))))

(defn fast-expt
  "Fast exponentiation using repeated squaring"
  [base exp]
  (cond
    (zero? exp) 1
    (even? exp) (fast-expt (* base base) (/ exp 2))
    :else (* base (fast-expt base (dec exp)))))

(defn mod-expt
  "Modular exponentiation"
  [base exp modulus]
  (cond
    (zero? exp) 1
    (even? exp) (mod (fast-expt (mod (* base base) modulus) (/ exp 2)) modulus)
    :else (mod (* base (mod-expt base (dec exp) modulus)) modulus)))

;; ============================================================================
;; PRIME NUMBER ALGORITHMS
;; ============================================================================

(defn miller-rabin-test
  "Probabilistic primality test"
  [n k]
  (if (< n 2)
    false
    (if (= n 2)
      true
      (if (even? n)
        false
        (let [d (loop [d (dec n)]
                  (if (odd? d) d (recur (/ d 2))))
              r (loop [r 0 temp (dec n)]
                  (if (odd? temp) r (recur (inc r) (/ temp 2))))]
          (loop [i 0]
            (if (>= i k)
              true
              (let [a (+ 2 (rand-int (- n 3)))
                    x (mod-expt a d n)]
                (if (or (= x 1) (= x (dec n)))
                  (recur (inc i))
                  (loop [j 0 x x]
                    (if (>= j (dec r))
                      false
                      (let [x (mod (* x x) n)]
                        (if (= x (dec n))
                          (recur (inc i))
                          (if (= x 1)
                            false
                            (recur (inc j) x))))))))))))))

(defn prime?
  "Check if number is prime using Miller-Rabin test"
  [n]
  (miller-rabin-test n 5))

(defn next-prime
  "Find next prime after n"
  [n]
  (loop [candidate (if (even? n) (inc n) (+ n 2))]
    (if (prime? candidate)
      candidate
      (recur (+ candidate 2)))))

;; ============================================================================
;; COMBINATORICS
;; ============================================================================

(defn factorial
  "Factorial with memoization"
  [n]
  (reduce * (range 1 (inc n))))

(def factorial-memo
  (memoize factorial))

(defn combinations
  "Number of ways to choose k items from n items"
  [n k]
  (if (or (< n 0) (< k 0) (> k n))
    0
    (/ (factorial-memo n)
       (* (factorial-memo k) (factorial-memo (- n k))))))

(defn permutations
  "Number of ways to arrange k items from n items"
  [n k]
  (if (or (< n 0) (< k 0) (> k n))
    0
    (/ (factorial-memo n) (factorial-memo (- n k)))))

(defn generate-permutations
  "Generate all permutations of a sequence"
  [coll]
  (if (empty? coll)
    '(())
    (for [head coll
          tail (generate-permutations (remove #{head} coll))]
      (cons head tail))))

(defn generate-combinations
  "Generate all combinations of k elements from sequence"
  [k coll]
  (cond
    (zero? k) '(())
    (empty? coll) '()
    :else (concat
           (map #(cons (first coll) %)
                (generate-combinations (dec k) (rest coll)))
           (generate-combinations k (rest coll)))))

;; ============================================================================
;; MATRIX OPERATIONS
;; ============================================================================

(defn matrix-multiply
  "Multiply two matrices"
  [A B]
  (let [rows-A (count A)
        cols-A (count (first A))
        cols-B (count (first B))]
    (for [i (range rows-A)]
      (for [j (range cols-B)]
        (reduce + (map * (nth A i) (map #(nth % j) B)))))))

(defn matrix-transpose
  "Transpose matrix"
  [matrix]
  (apply map vector matrix))

(defn matrix-determinant
  "Calculate determinant of square matrix"
  [matrix]
  (let [n (count matrix)]
    (cond
      (= n 1) (ffirst matrix)
      (= n 2) (- (* (get-in matrix [0 0]) (get-in matrix [1 1]))
                 (* (get-in matrix [0 1]) (get-in matrix [1 0])))
      :else (reduce +
                    (map-indexed
                     (fn [j element]
                       (* element
                          (if (even? j) 1 -1)
                          (matrix-determinant
                           (mapv #(vec (concat (take j %) (drop (inc j) %)))
                                 (rest matrix)))))
                     (first matrix))))))

;; ============================================================================
;; KATA CHALLENGES
;; ============================================================================

(defn kata-1-perfect-numbers
  "Find all perfect numbers up to n (numbers equal to sum of proper divisors)"
  [n]
  (filter (fn [num]
            (= num (reduce + (filter #(zero? (mod num %)) 
                                   (range 1 num)))))
          (range 1 (inc n))))

(defn kata-2-goldbach-conjecture
  "Express even number as sum of two primes (Goldbach's conjecture)"
  [n]
  (when (and (even? n) (> n 2))
    (first (for [p (take-while #(< % n) (filter prime? (range 2 n)))
                 :when (prime? (- n p))]
             [p (- n p)]))))

(defn kata-3-collatz-conjecture
  "Analyze Collatz sequences: find longest sequence up to n"
  [n]
  (let [collatz-length (fn [num]
                         (loop [x num length 0]
                           (if (= x 1)
                             length
                             (recur (if (even? x) (/ x 2) (inc (* 3 x)))
                                    (inc length)))))]
    (apply max-key collatz-length (range 1 (inc n)))))

(defn kata-4-egyptian-fractions
  "Express fraction as sum of distinct unit fractions"
  [numerator denominator]
  (loop [num numerator den denominator result []]
    (if (zero? num)
      result
      (let [unit-denom (Math/ceil (/ den num))]
        (recur (- (* num unit-denom) den)
               (* den unit-denom)
               (conj result (str "1/" unit-denom)))))))

(defn kata-5-magic-square
  "Generate magic square of order n (odd n only)"
  [n]
  (when (and (odd? n) (pos? n))
    (let [magic (vec (repeat n (vec (repeat n 0))))
          start-row (quot n 2)
          start-col 0]
      (loop [num 1 row start-row col start-col square magic]
        (if (> num (* n n))
          square
          (let [next-row (mod (dec row) n)
                next-col (mod (inc col) n)
                [new-row new-col] (if (not= 0 (get-in square [next-row next-col]))
                                   [(mod (inc row) n) col]
                                   [next-row next-col])]
            (recur (inc num)
                   new-row
                   new-col
                   (assoc-in square [row col] num))))))))

;; ============================================================================
;; UTILITY FUNCTIONS
;; ============================================================================

(defn validate-monoid-laws
  "Test if implementation satisfies monoid laws"
  [monoid values]
  (let [id (identity-element monoid)]
    {:left-identity (every? #(= % (combine monoid id %)) values)
     :right-identity (every? #(= % (combine monoid % id)) values)
     :associativity (every? (fn [[a b c]]
                             (= (combine monoid (combine monoid a b) c)
                                (combine monoid a (combine monoid b c))))
                           (for [a values b values c values] [a b c]))}))

(defn benchmark-algorithm
  "Simple benchmarking utility"
  [f & args]
  (let [start-time (js/Date.now)
        result (apply f args)
        end-time (js/Date.now)]
    {:result result
     :time-ms (- end-time start-time)}))