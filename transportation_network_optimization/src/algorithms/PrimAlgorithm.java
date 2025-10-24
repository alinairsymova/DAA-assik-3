package algorithms;

import model.Edge;
import model.Graph;
import model.MSTResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Professional implementation of Prim's algorithm for Minimum Spanning Tree
 * Supports multiple priority queue strategies and comprehensive performance analysis
 */
public class PrimAlgorithm implements MSTAlgorithm{
    private final PriorityQueueStrategy queueStrategy;
    private final boolean enableFibonacciHeap;
    private final boolean trackOperations;
    private final int initialCapacity;
    private boolean optimizeDenseGraphs;

    private int operationsCount;
    private int comparisonsCount;
    private int queueOperations;
    private int decreaseKeyOperations;

    /**
     * Priority Queue strategy enumeration
     */
    public enum PriorityQueueStrategy {
        BINARY_HEAP,
        FIBONACCI_HEAP,
        ARRAY_BASED,
        D_ARY_HEAP
    }

    /**
     * Internal vertex data structure for Prim's algorithm
     */
    private static class PrimVertex implements Comparable<PrimVertex> {
        private final String id;
        private double key;
        private Edge minEdge;
        private boolean inQueue;
        private int queueIndex;

        public PrimVertex(String id) {
            this.id = id;
            this.key = Double.MAX_VALUE;
            this.minEdge = null;
            this.inQueue = true;
            this.queueIndex = -1;
        }

        @Override
        public int compareTo(PrimVertex other) {
            return Double.compare(this.key, other.key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrimVertex that = (PrimVertex) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return String.format("PrimVertex{id='%s', key=%.2f}", id, key);
        }
    }

    /**
     * Custom priority queue for Prim's algorithm with decrease-key operation
     */
    private static class PrimPriorityQueue {
        private final List<PrimVertex> heap;
        private final Map<String, Integer> vertexIndexMap;
        private int operations;

        public PrimPriorityQueue(int initialCapacity) {
            this.heap = new ArrayList<>(initialCapacity);
            this.vertexIndexMap = new HashMap<>(initialCapacity);
            this.operations = 0;
        }

        public void add(PrimVertex vertex) {
            heap.add(vertex);
            vertex.queueIndex = heap.size() - 1;
            vertexIndexMap.put(vertex.id, vertex.queueIndex);
            siftUp(heap.size() - 1);
            operations++;
        }

        public PrimVertex poll() {
            if (heap.isEmpty()) return null;

            PrimVertex min = heap.get(0);
            PrimVertex last = heap.remove(heap.size() - 1);

            if (!heap.isEmpty()) {
                heap.set(0, last);
                last.queueIndex = 0;
                vertexIndexMap.put(last.id, 0);
                siftDown(0);
            }

            vertexIndexMap.remove(min.id);
            min.inQueue = false;
            min.queueIndex = -1;
            operations++;
            return min;
        }

        public void decreaseKey(PrimVertex vertex, double newKey) {
            int index = vertexIndexMap.get(vertex.id);
            if (newKey >= vertex.key) {
                return; // Not actually decreasing
            }

            vertex.key = newKey;
            siftUp(index);
            operations++;
        }

        public boolean contains(PrimVertex vertex) {
            return vertexIndexMap.containsKey(vertex.id);
        }

        public boolean isEmpty() {
            return heap.isEmpty();
        }

        public int size() {
            return heap.size();
        }

        private void siftUp(int index) {
            while (index > 0) {
                int parent = (index - 1) / 2;
                if (heap.get(index).compareTo(heap.get(parent)) >= 0) {
                    break;
                }
                swap(index, parent);
                index = parent;
                operations++;
            }
        }

        private void siftDown(int index) {
            int size = heap.size();
            while (index < size) {
                int left = 2 * index + 1;
                int right = 2 * index + 2;
                int smallest = index;

                if (left < size && heap.get(left).compareTo(heap.get(smallest)) < 0) {
                    smallest = left;
                }
                if (right < size && heap.get(right).compareTo(heap.get(smallest)) < 0) {
                    smallest = right;
                }
                if (smallest == index) {
                    break;
                }
                swap(index, smallest);
                index = smallest;
                operations++;
            }
        }

        private void swap(int i, int j) {
            PrimVertex temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);

            heap.get(i).queueIndex = i;
            heap.get(j).queueIndex = j;

            vertexIndexMap.put(heap.get(i).id, i);
            vertexIndexMap.put(heap.get(j).id, j);
            operations += 4;
        }

        public int getOperationsCount() {
            return operations;
        }
    }

    /**
     * Simple array-based priority queue for dense graphs
     */
    private static class ArrayPriorityQueue {
        private final List<PrimVertex> vertices;
        private int operations;

        public ArrayPriorityQueue(Collection<PrimVertex> vertices) {
            this.vertices = new ArrayList<>(vertices);
            this.operations = 0;
        }

        public PrimVertex poll() {
            if (vertices.isEmpty()) return null;

            int minIndex = findMinIndex();
            PrimVertex min = vertices.remove(minIndex);
            min.inQueue = false;
            operations++;
            return min;
        }

        public void decreaseKey(PrimVertex vertex, double newKey) {
            // In array implementation, we just update the key
            // The min finding happens during poll()
            vertex.key = newKey;
            operations++;
        }

        public boolean isEmpty() {
            return vertices.isEmpty();
        }

        private int findMinIndex() {
            int minIndex = 0;
            double minKey = vertices.get(0).key;

            for (int i = 1; i < vertices.size(); i++) {
                operations++;
                if (vertices.get(i).key < minKey) {
                    minKey = vertices.get(i).key;
                    minIndex = i;
                }
            }
            return minIndex;
        }

        public int getOperationsCount() {
            return operations;
        }
    }

    /**
     * Builder pattern for PrimAlgorithm configuration
     */
    public static class Builder {
        private PriorityQueueStrategy queueStrategy = PriorityQueueStrategy.BINARY_HEAP;
        private boolean enableFibonacciHeap = false;
        private boolean trackOperations = true;
        private int initialCapacity = 100;
        private boolean optimizeDenseGraphs = false;

        public Builder queueStrategy(PriorityQueueStrategy strategy) {
            this.queueStrategy = strategy;
            return this;
        }

        public Builder enableFibonacciHeap(boolean enable) {
            this.enableFibonacciHeap = enable;
            return this;
        }

        public Builder trackOperations(boolean track) {
            this.trackOperations = track;
            return this;
        }

        public Builder initialCapacity(int capacity) {
            this.initialCapacity = capacity;
            return this;
        }

        public Builder optimizeDenseGraphs(boolean optimize) {
            this.optimizeDenseGraphs = optimize;
            return this;
        }

        public PrimAlgorithm build() {
            return new PrimAlgorithm(this);
        }
    }

    /**
     * Private constructor
     */
    private PrimAlgorithm(Builder builder) {
        this.queueStrategy = builder.queueStrategy;
        this.enableFibonacciHeap = builder.enableFibonacciHeap;
        this.trackOperations = builder.trackOperations;
        this.initialCapacity = builder.initialCapacity;
        this.optimizeDenseGraphs = builder.optimizeDenseGraphs;
        resetCounters();
    }

    /**
     * Main MST computation method
     */
    public MSTResult computeMST(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }

        if (graph.getVerticesCount() == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex");
        }

        resetCounters();
        long startTime = System.nanoTime();
        long memoryBefore = getMemoryUsage();

        try {
            List<Edge> mstEdges = executePrim(graph);
            double totalCost = calculateTotalCost(mstEdges);

            long endTime = System.nanoTime();
            long memoryAfter = getMemoryUsage();

            return buildResult(graph, mstEdges, totalCost, startTime, endTime,
                    memoryAfter - memoryBefore);

        } catch (Exception e) {
            throw new MSTComputationException("Prim algorithm failed: " + e.getMessage(), e);
        }
    }

    /**
     * Core Prim algorithm implementation
     */
    private List<Edge> executePrim(Graph graph) {
        Map<String, PrimVertex> vertexMap = initializeVertices(graph);
        List<Edge> mstEdges = new ArrayList<>(graph.getVerticesCount() - 1);

        // Choose starting vertex
        String startVertex = graph.getVertexIds().get(0);
        vertexMap.get(startVertex).key = 0;

        // Initialize priority queue based on strategy
        QueueWrapper queue = initializeQueue(vertexMap.values());

        while (!queue.isEmpty()) {
            PrimVertex current = queue.poll();
            operationsCount++;
            queueOperations++;

            if (current.minEdge != null) {
                current.minEdge.setInMST(true);
                mstEdges.add(current.minEdge);
                operationsCount++;
            }

            // Process adjacent vertices
            processAdjacentVertices(graph, current, vertexMap, queue);

            // Early termination check
            if (mstEdges.size() >= graph.getVerticesCount() - 1) {
                break;
            }
        }

        validateMST(graph, mstEdges);
        return mstEdges;
    }

    /**
     * Initialize vertex data structures
     */
    private Map<String, PrimVertex> initializeVertices(Graph graph) {
        Map<String, PrimVertex> vertexMap = new HashMap<>();
        for (String vertexId : graph.getVertexIds()) {
            vertexMap.put(vertexId, new PrimVertex(vertexId));
            operationsCount++;
        }
        return vertexMap;
    }

    /**
     * Process adjacent vertices and update keys
     */
    private void processAdjacentVertices(Graph graph, PrimVertex current,
                                         Map<String, PrimVertex> vertexMap, QueueWrapper queue) {
        // Get adjacent edges using public API
        List<Edge> adjacentEdges = getAdjacentEdges(graph, current.id);

        for (Edge edge : adjacentEdges) {
            operationsCount++;
            comparisonsCount++;

            String neighborId = edge.getOtherVertex(current.id);
            PrimVertex neighbor = vertexMap.get(neighborId);

            if (neighbor.inQueue && edge.getWeight() < neighbor.key) {
                neighbor.key = edge.getWeight();
                neighbor.minEdge = edge;
                queue.decreaseKey(neighbor, edge.getWeight());
                decreaseKeyOperations++;
                operationsCount += 2;
            }
        }
    }

    /**
     * Get adjacent edges using public Graph API
     */
    private List<Edge> getAdjacentEdges(Graph graph, String vertexId) {
        // Since we're in a different package, we need to work with public methods
        // This method would need to be implemented based on your Graph class's public API
        List<Edge> edges = new ArrayList<>();

        // Example implementation - you'll need to adapt this to your Graph class
        try {
            // If your Graph class has a method to get adjacent edges as Edge objects
            // edges = graph.getAdjacentEdges(vertexId);

            // If you only have DTOs, you'll need to convert them
            List<Edge> allEdges = getAllEdgesFromGraph(graph);
            for (Edge edge : allEdges) {
                if (edge.containsVertex(vertexId)) {
                    edges.add(edge);
                }
            }
        } catch (Exception e) {
            throw new MSTComputationException("Failed to get adjacent edges for vertex: " + vertexId, e);
        }

        return edges;
    }

    /**
     * Helper method to get all edges from graph
     */
    private List<Edge> getAllEdgesFromGraph(Graph graph) {
        // This method needs to be implemented based on your Graph class's public API
        // You might need to use DTO conversion or other public methods
        List<Edge> edges = new ArrayList<>();

        // Example: if your Graph has getEdges() that returns List<Edge>
        // edges = graph.getEdges();

        // Or if it returns DTOs, convert them:
        // List<Edge.EdgeDTO> dtos = graph.getEdges();
        // for (Edge.EdgeDTO dto : dtos) {
        //     edges.add(new Edge(dto.getFrom(), dto.getTo(), dto.getWeight()));
        // }

        return edges;
    }

    /**
     * Queue wrapper for different priority queue implementations
     */
    private interface QueueWrapper {
        PrimVertex poll();
        void decreaseKey(PrimVertex vertex, double newKey);
        boolean isEmpty();
        int getOperationsCount();
    }

    /**
     * Initialize appropriate priority queue based on strategy
     */
    private QueueWrapper initializeQueue(Collection<PrimVertex> vertices) {
        boolean useArray = optimizeDenseGraphs &&
                vertices.size() < 1000 && // Small graphs
                queueStrategy == PriorityQueueStrategy.ARRAY_BASED;

        if (useArray) {
            return new ArrayQueueWrapper(new ArrayPriorityQueue(vertices));
        } else {
            PrimPriorityQueue heap = new PrimPriorityQueue(vertices.size());
            for (PrimVertex vertex : vertices) {
                heap.add(vertex);
            }
            return new HeapQueueWrapper(heap);
        }
    }

    /**
     * Wrapper for heap-based queue
     */
    private class HeapQueueWrapper implements QueueWrapper {
        private final PrimPriorityQueue heap;

        public HeapQueueWrapper(PrimPriorityQueue heap) {
            this.heap = heap;
        }

        @Override
        public PrimVertex poll() {
            return heap.poll();
        }

        @Override
        public void decreaseKey(PrimVertex vertex, double newKey) {
            heap.decreaseKey(vertex, newKey);
        }

        @Override
        public boolean isEmpty() {
            return heap.isEmpty();
        }

        @Override
        public int getOperationsCount() {
            return heap.getOperationsCount();
        }
    }

    /**
     * Wrapper for array-based queue
     */
    private class ArrayQueueWrapper implements QueueWrapper {
        private final ArrayPriorityQueue arrayQueue;

        public ArrayQueueWrapper(ArrayPriorityQueue arrayQueue) {
            this.arrayQueue = arrayQueue;
        }

        @Override
        public PrimVertex poll() {
            return arrayQueue.poll();
        }

        @Override
        public void decreaseKey(PrimVertex vertex, double newKey) {
            arrayQueue.decreaseKey(vertex, newKey);
        }

        @Override
        public boolean isEmpty() {
            return arrayQueue.isEmpty();
        }

        @Override
        public int getOperationsCount() {
            return arrayQueue.getOperationsCount();
        }
    }

    /**
     * Utility methods
     */
    private double calculateTotalCost(List<Edge> edges) {
        return edges.stream().mapToDouble(Edge::getWeight).sum();
    }

    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private void resetCounters() {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.queueOperations = 0;
        this.decreaseKeyOperations = 0;
    }

    private void validateMST(Graph graph, List<Edge> mstEdges) {
        if (mstEdges.size() != graph.getVerticesCount() - 1 && graph.getVerticesCount() > 0) {
            throw new MSTComputationException(
                    "MST construction failed. Expected " + (graph.getVerticesCount() - 1) +
                            " edges but got " + mstEdges.size() + ". Graph may be disconnected."
            );
        }
    }

    /**
     * Result building
     */
    private MSTResult buildResult(Graph graph, List<Edge> mstEdges, double totalCost,
                                  long startTime, long endTime, long memoryUsed) {
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        MSTResult.PerformanceMetrics metrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(executionTimeMs)
                .operationsCount(operationsCount)
                .memoryUsageBytes(memoryUsed)
                .comparisonsCount(comparisonsCount)
                .unionOperations(0) // Prim doesn't use union operations
                .priorityQueueOperations(queueOperations + decreaseKeyOperations)
                .build();

        MSTResult.AlgorithmParameters params = new MSTResult.AlgorithmParameters.Builder()
                .useFibonacciHeap(enableFibonacciHeap)
                .useUnionFind(false)
                .optimizeMemory(optimizeDenseGraphs)
                .initialCapacity(initialCapacity)
                .dataStructureVariant(queueStrategy.name())
                .build();

        return new MSTResult.Builder("Prim", graph, mstEdges)
                .performanceMetrics(metrics)
                .parameters(params)
                .build();
    }

    /**
     * Advanced analysis methods
     */
    public Map<String, Object> analyzeGraphSuitability(Graph graph) {
        Map<String, Object> analysis = new LinkedHashMap<>();

        int vertexCount = graph.getVerticesCount();
        int edgeCount = graph.getEdgesCount();
        double density = graph.getDensity();

        analysis.put("vertexCount", vertexCount);
        analysis.put("edgeCount", edgeCount);
        analysis.put("density", density);
        analysis.put("suitableForPrim", isSuitableForPrim(graph));
        analysis.put("expectedOperations", estimateOperations(vertexCount, edgeCount));
        analysis.put("recommendedQueueStrategy", recommendQueueStrategy(vertexCount, density));
        analysis.put("recommendedOptimization", recommendOptimization(vertexCount, edgeCount));

        return analysis;
    }

    private boolean isSuitableForPrim(Graph graph) {
        // Prim is generally good for dense graphs
        return graph.getDensity() > 0.3 || graph.getVerticesCount() < 1000;
    }

    private long estimateOperations(int vertices, int edges) {
        // O(V^2) for array, O(E log V) for binary heap, O(E + V log V) for Fibonacci heap
        if (optimizeDenseGraphs && vertices < 1000) {
            return (long) (vertices * vertices); // Array implementation
        } else {
            return (long) (edges * Math.log(vertices + 1)); // Heap implementation
        }
    }

    private PriorityQueueStrategy recommendQueueStrategy(int vertexCount, double density) {
        if (vertexCount < 500 || density > 0.7) {
            return PriorityQueueStrategy.ARRAY_BASED;
        } else if (vertexCount < 5000) {
            return PriorityQueueStrategy.BINARY_HEAP;
        } else {
            return PriorityQueueStrategy.D_ARY_HEAP;
        }
    }

    private String recommendOptimization(int vertexCount, int edgeCount) {
        if (edgeCount > vertexCount * vertexCount / 4) {
            return "Use array-based implementation for dense graph";
        } else if (vertexCount > 10000) {
            return "Consider Fibonacci heap for large sparse graphs";
        } else {
            return "Binary heap is optimal for this graph";
        }
    }

    /**
     * Batch processing for multiple graphs
     */
    public List<MSTResult> computeMSTBatch(List<Graph> graphs) {
        return graphs.parallelStream()
                .map(this::computeMST)
                .collect(Collectors.toList());
    }

    /**
     * Performance optimization methods
     */
    public void optimizeForSparseGraphs() {
        // For sparse graphs, binary heap is usually best
        this.optimizeDenseGraphs = false;
    }

    public void optimizeForDenseGraphs() {
        // For dense graphs, array implementation can be better
        this.optimizeDenseGraphs = true;
    }

    // Getters for configuration and metrics
    public PriorityQueueStrategy getQueueStrategy() { return queueStrategy; }
    public boolean isFibonacciHeapEnabled() { return enableFibonacciHeap; }
    public boolean isTrackOperations() { return trackOperations; }
    public boolean isOptimizeDenseGraphs() { return optimizeDenseGraphs; }
    public int getOperationsCount() { return operationsCount; }
    public int getComparisonsCount() { return comparisonsCount; }
    public int getQueueOperations() { return queueOperations; }
    public int getDecreaseKeyOperations() { return decreaseKeyOperations; }

    /**
     * Custom exception for MST computation errors
     */
    public static class MSTComputationException extends RuntimeException {
        public MSTComputationException(String message) {
            super(message);
        }

        public MSTComputationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Static factory methods
     */
    public static PrimAlgorithm createDefault() {
        return new Builder().build();
    }

    public static PrimAlgorithm createOptimizedForSparseGraphs() {
        return new Builder()
                .queueStrategy(PriorityQueueStrategy.BINARY_HEAP)
                .enableFibonacciHeap(false)
                .optimizeDenseGraphs(false)
                .build();
    }

    public static PrimAlgorithm createOptimizedForDenseGraphs() {
        return new Builder()
                .queueStrategy(PriorityQueueStrategy.ARRAY_BASED)
                .enableFibonacciHeap(false)
                .optimizeDenseGraphs(true)
                .build();
    }

    public static PrimAlgorithm createOptimizedForLargeGraphs() {
        return new Builder()
                .queueStrategy(PriorityQueueStrategy.D_ARY_HEAP)
                .enableFibonacciHeap(true)
                .optimizeDenseGraphs(false)
                .initialCapacity(10000)
                .build();
    }

    @Override
    public String toString() {
        return String.format(
                "PrimAlgorithm{queueStrategy=%s, fibonacciHeap=%s, trackOperations=%s, optimizeDense=%s}",
                queueStrategy, enableFibonacciHeap, trackOperations, optimizeDenseGraphs
        );
    }

    @Override
    public String getAlgorithmName() {
        return "Prim";
    }

    @Override
    public String getTimeComplexity() {
        switch (queueStrategy) {
            case ARRAY_BASED: return "O(V²)";
            case BINARY_HEAP: return "O(E log V)";
            default: return "O(E log V)";
        }
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V + E)";
    }

    @Override
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("operationsCount", this.operationsCount);
        metrics.put("comparisonsCount", this.comparisonsCount);
        metrics.put("queueOperations", this.queueOperations);
        metrics.put("decreaseKeyOperations", this.decreaseKeyOperations);
        return metrics;
    }

    @Override
    public Map<String, Object> getAlgorithmParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("queueStrategy", this.queueStrategy.name());
        params.put("optimizeDenseGraphs", this.optimizeDenseGraphs);
        return params;
    }

    @Override
    public void reset() {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.queueOperations = 0;
        this.decreaseKeyOperations = 0;
    }

    @Override
    public Map<String, Object> analyzeSuitability(Graph graph) {
        return analyzeGraphSuitability(graph);
    }
}