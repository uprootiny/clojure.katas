# Clojure Katas

Interactive functional programming exercises exploring the mathematical elegance of Clojure.

## Philosophy

Clojure embodies computation as mathematical transformation—immutable data structures flowing through pure functions, with persistent data structures that maintain performance while preserving referential transparency. These katas explore that mathematical beauty through practical exercises.

## Features

- **🔄 Sequences** - Lazy evaluation, infinite data structures, and transformation pipelines
- **🌳 Trees** - Recursive algorithms, binary search trees, and traversal patterns  
- **📐 Mathematics** - Number theory, algebraic structures, and computational geometry
- **🎯 Interactive REPL** - Browser-based ClojureScript environment for immediate feedback
- **📱 Responsive Design** - Works seamlessly on desktop and mobile devices

## Quick Start

### Prerequisites

- Node.js (for shadow-cljs)
- Java (for Clojure compilation)

### Installation

```bash
# Clone the repository
git clone https://github.com/uprootiny/clojure.katas.git
cd clojure.katas

# Install shadow-cljs globally
npm install -g shadow-cljs

# Install dependencies
npm install
```

### Development

```bash
# Start the development server
shadow-cljs watch app

# Open browser to http://localhost:8280
```

### Production Build

```bash
# Build for production
shadow-cljs release app

# Serve the public directory
python -m http.server 8000 --directory public
```

## Project Structure

```
clojure.katas/
├── src/uprootiny/katas/
│   ├── core.cljs          # Main UI components and state management
│   ├── sequences.cljs     # Sequence transformation katas
│   ├── trees.cljs         # Tree structure and algorithm katas
│   └── mathematics.cljs   # Mathematical and number theory katas
├── public/
│   ├── index.html         # Main HTML with embedded CSS
│   └── js/                # Compiled ClojureScript output
├── deps.edn               # Clojure dependencies
└── shadow-cljs.edn        # Build configuration
```

## Kata Categories

### Sequences

Explore the power of lazy evaluation and functional transformations:

- **Fibonacci Sequence** - Generate infinite sequences with lazy evaluation
- **Word Frequency** - Text analysis using transformation pipelines
- **Pascal's Triangle** - Mathematical sequences and recurrence relations
- **Anagram Groups** - String manipulation and grouping algorithms
- **Collatz Sequences** - Number theory and sequence analysis

### Trees

Master recursive data structures and algorithms:

- **Tree Traversals** - Inorder, preorder, postorder, and level-order
- **Binary Search Trees** - Insert, search, and validation algorithms
- **Tree Properties** - Height, balance, symmetry, and diameter calculations
- **Path Finding** - Root-to-leaf paths and lowest common ancestors
- **Tree Transformations** - Mirroring, flattening, and structure modifications

### Mathematics

Dive into computational mathematics and algebraic structures:

- **Number Theory** - GCD, LCM, prime testing, and modular arithmetic
- **Combinatorics** - Permutations, combinations, and factorial calculations
- **Algebraic Structures** - Monoids, semigroups, functors, and applicatives
- **Matrix Operations** - Multiplication, transposition, and determinants
- **Perfect Numbers** - Mathematical curiosities and pattern recognition

## Interactive Features

### REPL Environment

Each kata includes an interactive REPL where you can:
- Experiment with the provided functions
- Test your own implementations
- Explore edge cases and performance characteristics
- See immediate results in your browser

### Visual Design

The interface features:
- **Dark Theme** - Easy on the eyes for extended coding sessions
- **Syntax Highlighting** - Clear code presentation
- **Responsive Layout** - Works on all screen sizes
- **Smooth Animations** - Polished user experience

## Mathematical Foundations

### Lazy Evaluation

Clojure's lazy sequences allow for elegant solutions to infinite problems:

```clojure
;; Infinite Fibonacci sequence
(defn fibonacci
  ([] (fibonacci 0 1))
  ([a b] (lazy-seq (cons a (fibonacci b (+ a b))))))

;; Take only what you need
(take 10 (fibonacci))
;; => (0 1 1 2 3 5 8 13 21 34)
```

### Immutable Data Structures

All data structures are immutable by default, enabling:
- **Referential Transparency** - Functions always return the same output for the same input
- **Thread Safety** - No race conditions or locks needed
- **Time Travel Debugging** - Previous states are preserved
- **Structural Sharing** - Efficient memory usage through persistent data structures

### Functional Composition

Complex operations emerge from simple function composition:

```clojure
;; Word frequency analysis pipeline
(->> text
     str/lower-case
     (re-seq #"\\w+")
     frequencies
     (sort-by val >)
     (into {}))
```

## Performance Considerations

### Lazy Sequences

- Memory efficient for large datasets
- Only compute what's needed
- Composable transformations
- Natural infinite data structure support

### Persistent Data Structures

- O(log n) updates through structural sharing
- Immutability without performance penalties
- Automatic memory management
- Cache-friendly access patterns

## Advanced Topics

### Monoids and Algebraic Structures

The mathematics module explores abstract algebra concepts:

```clojure
(defprotocol Monoid
  (identity-element [_])
  (combine [x y]))

;; Addition monoid
(def addition-monoid
  (reify Monoid
    (identity-element [_] 0)
    (combine [_ x y] (+ x y))))
```

### Tree Algorithms

Complex tree operations using recursive thinking:

```clojure
(defn tree-height [tree]
  (if (nil? tree)
    0
    (inc (max (tree-height (:left tree))
              (tree-height (:right tree))))))
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add new katas or improve existing ones
4. Include tests and documentation
5. Submit a pull request

## License

MIT License - see LICENSE file for details.

## Inspiration

This project draws inspiration from:
- **Mathematical Beauty** - Code as mathematical expression
- **Functional Programming Principles** - Immutability, composition, and purity
- **Interactive Learning** - Immediate feedback and exploration
- **Clean Design** - Minimal, focused user experience

---

*"Simplicity is the ultimate sophistication"* - Leonardo da Vinci

Explore the mathematical elegance of functional programming through hands-on practice.