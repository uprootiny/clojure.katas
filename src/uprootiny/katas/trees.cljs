(ns uprootiny.katas.trees
  "Katas exploring tree structures and recursive algorithms")

;; ============================================================================
;; TREE REPRESENTATION
;; ============================================================================

(defrecord TreeNode [value left right])

(defn leaf [value]
  "Create a leaf node"
  (->TreeNode value nil nil))

(defn node [value left right]
  "Create an internal node"
  (->TreeNode value left right))

;; ============================================================================
;; TREE TRAVERSALS
;; ============================================================================

(defn inorder
  "Inorder traversal: left -> root -> right"
  [tree]
  (when tree
    (lazy-seq
     (concat (inorder (:left tree))
             [(:value tree)]
             (inorder (:right tree))))))

(defn preorder
  "Preorder traversal: root -> left -> right"
  [tree]
  (when tree
    (lazy-seq
     (cons (:value tree)
           (concat (preorder (:left tree))
                   (preorder (:right tree)))))))

(defn postorder
  "Postorder traversal: left -> right -> root"
  [tree]
  (when tree
    (lazy-seq
     (concat (postorder (:left tree))
             (postorder (:right tree))
             [(:value tree)]))))

(defn level-order
  "Level-order (breadth-first) traversal"
  [tree]
  (when tree
    (loop [queue [tree] result []]
      (if (empty? queue)
        result
        (let [current (first queue)
              rest-queue (rest queue)
              children (filter identity [(:left current) (:right current)])]
          (recur (concat rest-queue children)
                 (conj result (:value current))))))))

;; ============================================================================
;; TREE PROPERTIES
;; ============================================================================

(defn tree-height
  "Calculate height of tree"
  [tree]
  (if (nil? tree)
    0
    (inc (max (tree-height (:left tree))
              (tree-height (:right tree))))))

(defn tree-size
  "Count total nodes in tree"
  [tree]
  (if (nil? tree)
    0
    (+ 1
       (tree-size (:left tree))
       (tree-size (:right tree)))))

(defn balanced?
  "Check if tree is height-balanced (AVL property)"
  [tree]
  (if (nil? tree)
    true
    (let [left-height (tree-height (:left tree))
          right-height (tree-height (:right tree))]
      (and (<= (abs (- left-height right-height)) 1)
           (balanced? (:left tree))
           (balanced? (:right tree))))))

;; ============================================================================
;; BINARY SEARCH TREE OPERATIONS
;; ============================================================================

(defn bst-insert
  "Insert value into binary search tree"
  [tree value]
  (cond
    (nil? tree) (leaf value)
    (< value (:value tree)) (node (:value tree)
                                   (bst-insert (:left tree) value)
                                   (:right tree))
    (> value (:value tree)) (node (:value tree)
                                   (:left tree)
                                   (bst-insert (:right tree) value))
    :else tree)) ; value already exists

(defn bst-search
  "Search for value in binary search tree"
  [tree value]
  (cond
    (nil? tree) false
    (= value (:value tree)) true
    (< value (:value tree)) (bst-search (:left tree) value)
    :else (bst-search (:right tree) value)))

(defn bst-from-sequence
  "Build BST from sequence of values"
  [values]
  (reduce bst-insert nil values))

;; ============================================================================
;; KATA CHALLENGES
;; ============================================================================

(defn kata-1-symmetric-tree
  "Check if binary tree is symmetric (mirror of itself)"
  [tree]
  (letfn [(mirror? [left right]
            (cond
              (and (nil? left) (nil? right)) true
              (or (nil? left) (nil? right)) false
              (not= (:value left) (:value right)) false
              :else (and (mirror? (:left left) (:right right))
                        (mirror? (:right left) (:left right)))))]
    (or (nil? tree)
        (mirror? (:left tree) (:right tree)))))

(defn kata-2-tree-paths
  "Find all root-to-leaf paths in tree"
  [tree]
  (letfn [(paths [node current-path]
            (if (nil? node)
              []
              (let [new-path (conj current-path (:value node))]
                (if (and (nil? (:left node)) (nil? (:right node)))
                  [new-path]
                  (concat (paths (:left node) new-path)
                          (paths (:right node) new-path))))))]
    (paths tree [])))

(defn kata-3-lowest-common-ancestor
  "Find lowest common ancestor of two values in BST"
  [tree val1 val2]
  (when tree
    (let [root-val (:value tree)]
      (cond
        (and (< val1 root-val) (< val2 root-val))
        (kata-3-lowest-common-ancestor (:left tree) val1 val2)
        
        (and (> val1 root-val) (> val2 root-val))
        (kata-3-lowest-common-ancestor (:right tree) val1 val2)
        
        :else root-val))))

(defn kata-4-tree-diameter
  "Find diameter (longest path between any two nodes)"
  [tree]
  (letfn [(diameter-helper [node]
            (if (nil? node)
              {:height 0 :diameter 0}
              (let [left-result (diameter-helper (:left node))
                    right-result (diameter-helper (:right node))
                    height (inc (max (:height left-result) (:height right-result)))
                    diameter (max (:diameter left-result)
                                  (:diameter right-result)
                                  (+ (:height left-result) (:height right-result)))]
                {:height height :diameter diameter})))]
    (:diameter (diameter-helper tree))))

(defn kata-5-serialize-deserialize
  "Serialize tree to string and deserialize back"
  [tree]
  (letfn [(serialize [node]
            (if (nil? node)
              "null"
              (str (:value node) ","
                   (serialize (:left node)) ","
                   (serialize (:right node)))))
          
          (deserialize [data]
            (let [tokens (atom (clojure.string/split data #","))]
              (letfn [(build []
                        (let [token (first @tokens)]
                          (swap! tokens rest)
                          (if (= token "null")
                            nil
                            (node (js/parseInt token)
                                  (build)
                                  (build)))))]
                (build))))]
    
    {:serialize serialize
     :deserialize deserialize
     :roundtrip #(-> % serialize deserialize)}))

;; ============================================================================
;; TREE TRANSFORMATIONS
;; ============================================================================

(defn mirror-tree
  "Create mirror image of tree"
  [tree]
  (when tree
    (node (:value tree)
          (mirror-tree (:right tree))
          (mirror-tree (:left tree)))))

(defn flatten-tree
  "Flatten tree to linked list using preorder traversal"
  [tree]
  (when tree
    (let [flattened-left (flatten-tree (:left tree))
          flattened-right (flatten-tree (:right tree))]
      (node (:value tree)
            nil
            (if flattened-left
              (-> flattened-left
                  (update :right #(or % flattened-right)))
              flattened-right)))))

(defn tree-map
  "Apply function to all values in tree"
  [f tree]
  (when tree
    (node (f (:value tree))
          (tree-map f (:left tree))
          (tree-map f (:right tree)))))

;; ============================================================================
;; ADVANCED ALGORITHMS
;; ============================================================================

(defn kata-6-binary-tree-to-bst
  "Convert binary tree to BST maintaining structure"
  [tree]
  (let [values (sort (inorder tree))]
    (letfn [(build-bst [vals]
              (when (seq vals)
                (let [mid (quot (count vals) 2)
                      left-vals (take mid vals)
                      right-vals (drop (inc mid) vals)]
                  (node (nth vals mid)
                        (build-bst left-vals)
                        (build-bst right-vals)))))]
      (build-bst values))))

(defn kata-7-validate-bst
  "Validate if tree is a valid binary search tree"
  [tree]
  (letfn [(validate [node min-val max-val]
            (or (nil? node)
                (and (< min-val (:value node) max-val)
                     (validate (:left node) min-val (:value node))
                     (validate (:right node) (:value node) max-val))))]
    (validate tree js/-Infinity js/Infinity)))

;; ============================================================================
;; UTILITY FUNCTIONS
;; ============================================================================

(defn print-tree
  "Pretty print tree structure"
  [tree]
  (letfn [(print-helper [node depth prefix]
            (when node
              (println (str (apply str (repeat depth "  "))
                           prefix
                           (:value node)))
              (when (or (:left node) (:right node))
                (print-helper (:left node) (inc depth) "L: ")
                (print-helper (:right node) (inc depth) "R: "))))]
    (print-helper tree 0 "Root: ")))