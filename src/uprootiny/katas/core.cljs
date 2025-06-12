(ns uprootiny.katas.core
  (:require [reagent.core :as r]
            [uprootiny.katas.sequences :as seq]
            [uprootiny.katas.trees :as trees]
            [uprootiny.katas.mathematics :as math]
            [uprootiny.katas.llm-patterns :as llm]
            [uprootiny.katas.ai-architecture :as arch]))

;; ============================================================================
;; STATE MANAGEMENT
;; ============================================================================

(defonce app-state (r/atom {:current-kata nil
                            :results {}
                            :show-solution false}))

;; ============================================================================
;; KATA DEFINITIONS
;; ============================================================================

(def kata-categories
  {:sequences
   [{:id :fibonacci
     :title "Fibonacci Sequence"
     :description "Generate infinite fibonacci sequence using lazy evaluation"
     :fn seq/fibonacci
     :example "(take 10 (fibonacci)) => (0 1 1 2 3 5 8 13 21 34)"}
    {:id :word-frequency
     :title "Word Frequency"
     :description "Count word frequencies in text"
     :fn seq/kata-1-word-frequency
     :example "(kata-1-word-frequency \"hello world hello\") => {\"hello\" 2, \"world\" 1}"}
    {:id :pascal-triangle
     :title "Pascal's Triangle"
     :description "Generate first n rows of Pascal's triangle"
     :fn seq/kata-2-pascal-triangle
     :example "(kata-2-pascal-triangle 4) => [[1] [1 1] [1 2 1] [1 3 3 1]]"}]
   
   :trees
   [{:id :tree-height
     :title "Tree Height"
     :description "Calculate height of binary tree"
     :fn trees/tree-height
     :example "(tree-height sample-tree) => 3"}
    {:id :symmetric-tree
     :title "Symmetric Tree"
     :description "Check if binary tree is symmetric"
     :fn trees/kata-1-symmetric-tree
     :example "(symmetric-tree mirror-tree) => true"}
    {:id :tree-paths
     :title "Root to Leaf Paths"
     :description "Find all paths from root to leaves"
     :fn trees/kata-2-tree-paths
     :example "(tree-paths sample-tree) => [[1 2 4] [1 2 5] [1 3]]"}]
   
   :mathematics
   [{:id :gcd
     :title "Greatest Common Divisor"
     :description "Find GCD using Euclidean algorithm"
     :fn math/gcd
     :example "(gcd 48 18) => 6"}
    {:id :prime-test
     :title "Primality Test"
     :description "Test if number is prime using Miller-Rabin"
     :fn math/prime?
     :example "(prime? 97) => true"}
    {:id :perfect-numbers
     :title "Perfect Numbers"
     :description "Find perfect numbers up to n"
     :fn math/kata-1-perfect-numbers
     :example "(kata-1-perfect-numbers 30) => [6 28]"}]
   
   :llm-patterns
   [{:id :chain-of-thought
     :title "Chain of Thought Reasoning"
     :description "Apply systematic reasoning decomposition"
     :fn llm/chain-of-thought
     :example "(chain-of-thought \"Calculate 23 * 47\" [\"Break down: 20*47 + 3*47\" \"940 + 141\" \"1081\"])"}
    {:id :constitutional-ai
     :title "Constitutional AI Critique"
     :description "Self-correction using constitutional principles"
     :fn llm/constitutional-critique
     :example "(constitutional-critique response [\"Be helpful\" \"Be harmless\" \"Be honest\"])"}
    {:id :rag-pipeline
     :title "Retrieval-Augmented Generation"
     :description "Enhance responses with retrieved knowledge"
     :fn llm/rag-pipeline
     :example "(rag-pipeline query knowledge-base retrieve-fn synthesize-fn)"}
    {:id :meta-cognition
     :title "Metacognitive Reflection"
     :description "Assess reasoning quality and uncertainty"
     :fn llm/metacognitive-reflection
     :example "(metacognitive-reflection reasoning-trace)"}]
   
   :ai-architecture
   [{:id :attention-mechanism
     :title "Attention Mechanisms"
     :description "Query-key-value attention computation"
     :fn arch/attention-mechanism
     :example "(attention-mechanism queries keys values :multi-head)"}
    {:id :mixture-of-experts
     :title "Mixture of Experts"
     :description "Route inputs to specialized expert networks"
     :fn arch/mixture-of-experts
     :example "(mixture-of-experts expert-list router-fn true)"}
    {:id :global-workspace
     :title "Global Workspace Theory"
     :description "Simulate consciousness through global broadcasting"
     :fn arch/global-workspace
     :example "(global-workspace modules attention-mechanism)"}
    {:id :neural-plasticity
     :title "Neural Plasticity"
     :description "Adaptive learning through synaptic modification"
     :fn arch/neural-plasticity
     :example "(neural-plasticity network learning-rules plasticity-types)"}]})

;; ============================================================================
;; SAMPLE DATA
;; ============================================================================

(def sample-tree
  (trees/node 1
              (trees/node 2
                          (trees/leaf 4)
                          (trees/leaf 5))
              (trees/leaf 3)))

;; ============================================================================
;; UI COMPONENTS
;; ============================================================================

(defn kata-card [category-key kata]
  [:div.kata-card
   {:class (when (= (:current-kata @app-state) [category-key (:id kata)]) "active")
    :on-click #(swap! app-state assoc :current-kata [category-key (:id kata)])}
   [:h3 (:title kata)]
   [:p (:description kata)]
   [:code.example (:example kata)]])

(defn kata-category [category-key katas]
  [:div.category
   [:h2 (name category-key)]
   [:div.kata-grid
    (for [kata katas]
      ^{:key (:id kata)}
      [kata-card category-key kata])]])

(defn code-runner []
  (let [input-text (r/atom "")
        result (r/atom nil)]
    (fn []
      [:div.code-runner
       [:h3 "Interactive REPL"]
       [:textarea.code-input
        {:value @input-text
         :on-change #(reset! input-text (-> % .-target .-value))
         :placeholder "Enter Clojure code here..."}]
       [:button.run-button
        {:on-click #(try
                     (reset! result (str (js/eval @input-text)))
                     (catch js/Error e
                       (reset! result (str "Error: " (.-message e)))))} 
        "Run"]
       (when @result
         [:div.result
          [:h4 "Result:"]
          [:pre @result]])])))

(defn kata-detail []
  (when-let [[category-key kata-id] (:current-kata @app-state)]
    (let [kata (->> (get kata-categories category-key)
                    (filter #(= (:id %) kata-id))
                    first)]
      [:div.kata-detail
       [:h2 (:title kata)]
       [:p (:description kata)]
       [:div.example
        [:h4 "Example:"]
        [:code (:example kata)]]
       [code-runner]])))

(defn main-component []
  [:div.app
   [:header
    [:h1 "Clojure Katas"]
    [:p "Functional programming exercises exploring mathematical elegance"]]
   
   [:div.content
    [:div.sidebar
     (for [[category-key katas] kata-categories]
       ^{:key category-key}
       [kata-category category-key katas])]
    
    [:div.main-panel
     (if (:current-kata @app-state)
       [kata-detail]
       [:div.welcome
        [:h2 "Welcome to Clojure Katas"]
        [:p "Select a kata from the sidebar to begin exploring functional programming patterns."]
        [:ul
         [:li "🔄 Sequences - Lazy evaluation and transformations"]
         [:li "🌳 Trees - Recursive structures and algorithms"]
         [:li "📐 Mathematics - Number theory and algebraic structures"]
         [:li "🤖 LLM Patterns - Advanced AI interaction patterns"]
         [:li "🧠 AI Architecture - Neural networks and consciousness simulation"]]])]]
   
   [:footer
    [:p "Built with ClojureScript + Reagent"]]])

(defn init []
  (r/render [main-component]
            (.getElementById js/document "app")))