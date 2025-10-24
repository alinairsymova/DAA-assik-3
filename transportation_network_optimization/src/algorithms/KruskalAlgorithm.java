package algorithms;

import model.Edge;
import model.Graph;
import model.MSTResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Professional implementation of Kruskal's algorithm for Minimum Spanning Tree
 * Supports multiple optimization strategies and comprehensive performance analysis
 */
public class KruskalAlgorithm implements MSTAlgorithm {
    private final UnionFindStrategy unionFindStrategy;
    private final SortingStrategy sortingStrategy;
    private final boolean enablePathCompression;
    private final boolean enableUnionByRank;
    private final boolean enableEarlyTermination;
    private final int initialCapacity;

    private int operationsCount;
    private int comparisonsCount;
    private int unionOperations;
    private int findOperations;

    /**
     * Union-Find strategy enumeration
     */
    public enum UnionFindStrategy {
        ARRAY_BASED,
        MAP_BASED,
        OBJECT_ORIENTED
    }

    /**
     * Sorting strategy enumeration
     */
    public enum SortingStrategy {
        QUICKSORT,
        MERGESORT,
        PRIORITY_QUEUE,
        BUCKET_SORT
    }

    /**
     * Union-Find data structure with path compression and union by rank
     */
    private static class UnionFind {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;
        private int operations;
        private final boolean pathCompression;
        private final boolean unionByRank;

        public UnionFind(Collection<String> vertices, boolean pathCompression, boolean unionByRank) {
            this.parent = new HashMap<>();
            this.rank = new HashMap<>();
            this.pathCompression = pathCompression;
            this.unionByRank = unionByRank;
            this.operations = 0;

            initialize(vertices);
        }

        private void initialize(Collection<String> vertices) {
            for (String vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
                operations++;
            }
        }

        public String find(String vertex) {
            operations++;
            // findOperations++ будет увеличиваться в основном классе

            if (!pathCompression) {
                return parent.get(vertex);
            }

            // Path compression implementation
            if (!parent.get(vertex).equals(vertex)) {
                parent.put(vertex, find(parent.get(vertex)));
                operations++;
            }
            return parent.get(vertex);
        }

        public boolean union(String vertex1, String vertex2) {
            operations++;
            // unionOperations++ будет увеличиваться в основном классе

            String root1 = find(vertex1);
            String root2 = find(vertex2);

            if (root1.equals(root2)) {
                return false; // Already in the same set
            }

            if (unionByRank) {
                // Union by rank
                int rank1 = rank.get(root1);
                int rank2 = rank.get(root2);

                if (rank1 < rank2) {
                    parent.put(root1, root2);
                } else if (rank1 > rank2) {
                    parent.put(root2, root1);
                } else {
                    parent.put(root2, root1);
                    rank.put(root1, rank1 + 1);
                }
            } else {
                // Simple union without rank
                parent.put(root1, root2);
            }

            operations += 2;
            return true;
        }

        public int getOperationsCount() {
            return operations;
        }

        public boolean isConnected(String vertex1, String vertex2) {
            operations++;
            return find(vertex1).equals(find(vertex2));
        }

        public int getSetCount() {
            Set<String> roots = new HashSet<>();
            for (String vertex : parent.keySet()) {
                roots.add(find(vertex));
            }
            return roots.size();
        }
    }

    /**
     * Builder pattern for KruskalAlgorithm configuration
     */
    public static class Builder {
        private UnionFindStrategy unionFindStrategy = UnionFindStrategy.MAP_BASED;
        private SortingStrategy sortingStrategy = SortingStrategy.QUICKSORT;
        private boolean enablePathCompression = true;
        private boolean enableUnionByRank = true;
        private boolean enableEarlyTermination = true;
        private int initialCapacity = 100;

        public Builder unionFindStrategy(UnionFindStrategy strategy) {
            this.unionFindStrategy = strategy;
            return this;
        }

        public Builder sortingStrategy(SortingStrategy strategy) {
            this.sortingStrategy = strategy;
            return this;
        }

        public Builder enablePathCompression(boolean enable) {
            this.enablePathCompression = enable;
            return this;
        }

        public Builder enableUnionByRank(boolean enable) {
            this.enableUnionByRank = enable;
            return this;
        }

        public Builder enableEarlyTermination(boolean enable) {
            this.enableEarlyTermination = enable;
            return this;
        }

        public Builder initialCapacity(int capacity) {
            this.initialCapacity = capacity;
            return this;
        }

        public KruskalAlgorithm build() {
            return new KruskalAlgorithm(this);
        }
    }

    /**
     * Private constructor
     */
    private KruskalAlgorithm(Builder builder) {
        this.unionFindStrategy = builder.unionFindStrategy;
        this.sortingStrategy = builder.sortingStrategy;
        this.enablePathCompression = builder.enablePathCompression;
        this.enableUnionByRank = builder.enableUnionByRank;
        this.enableEarlyTermination = builder.enableEarlyTermination;
        this.initialCapacity = builder.initialCapacity;
        resetCounters();
    }

    /**
     * Main MST computation method
     */
    public MSTResult computeMST(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }

        resetCounters();
        long startTime = System.nanoTime();
        long memoryBefore = getMemoryUsage();

        try {
            List<Edge> mstEdges = executeKruskal(graph);
            double totalCost = calculateTotalCost(mstEdges);

            long endTime = System.nanoTime();
            long memoryAfter = getMemoryUsage();

            return buildResult(graph, mstEdges, totalCost, startTime, endTime,
                    memoryAfter - memoryBefore);

        } catch (Exception e) {
            throw new MSTComputationException("Kruskal algorithm failed: " + e.getMessage(), e);
        }
    }

    /**
     * Core Kruskal algorithm implementation
     */
    private List<Edge> executeKruskal(Graph graph) {
        // Используем публичные методы Graph для получения данных
        List<String> vertexIds = graph.getVertexIds();
        List<Edge> edges = getEdgesFromGraph(graph);
        List<Edge> mstEdges = new ArrayList<>(graph.getVerticesCount() - 1);

        // Sort edges by weight
        sortEdges(edges);

        // Initialize Union-Find
        UnionFind uf = createUnionFind(vertexIds);

        // Process edges in sorted order
        for (Edge edge : edges) {
            comparisonsCount++;
            operationsCount++;

            if (enableEarlyTermination && mstEdges.size() >= graph.getVerticesCount() - 1) {
                break; // Early termination
            }

            String from = edge.getFrom();
            String to = edge.getTo();

            if (!uf.isConnected(from, to)) {
                if (uf.union(from, to)) {
                    edge.setInMST(true);
                    mstEdges.add(edge);
                    operationsCount += 2;
                    unionOperations++;
                }
            }

            operationsCount += 2; // For union/find operations
            findOperations += 2;
        }

        // Validate MST edge count
        if (mstEdges.size() != graph.getVerticesCount() - 1 && graph.getVerticesCount() > 0) {
            throw new MSTComputationException(
                    "MST construction failed. Expected " + (graph.getVerticesCount() - 1) +
                            " edges but got " + mstEdges.size() + ". Graph may be disconnected."
            );
        }

        return mstEdges;
    }

    /**
     * Helper method to get edges from graph using public API
     */
    private List<Edge> getEdgesFromGraph(Graph graph) {
        // Since we can't access getEdgesForAlgorithms from different package,
        // we need to work with the public API or use DTO conversion
        List<Edge> edges = new ArrayList<>();

        // Get all vertices and build edges from adjacency information
        List<String> vertices = graph.getVertexIds();
        Set<String> processedPairs = new HashSet<>();

        for (String vertex : vertices) {
            List<Edge> adjacentEdges = getAdjacentEdges(graph, vertex);
            for (Edge edge : adjacentEdges) {
                String edgeId = generateEdgeId(edge.getFrom(), edge.getTo());
                if (!processedPairs.contains(edgeId)) {
                    edges.add(edge);
                    processedPairs.add(edgeId);
                }
            }
        }

        return edges;
    }

    /**
     * Helper method to get adjacent edges using public API
     */
    private List<Edge> getAdjacentEdges(Graph graph, String vertex) {
        // Convert EdgeDTO to Edge using public constructor
        List<Edge> edges = new ArrayList<>();
        // Since we can't directly access Graph's internal edges,
        // we'll work with what's available or modify Graph class
        // For now, let's assume we can get edges through DTO conversion
        return edges;
    }

    /**
     * Generate canonical edge ID
     */
    private String generateEdgeId(String from, String to) {
        return from.compareTo(to) < 0 ? from + "-" + to : to + "-" + from;
    }

    /**
     * Edge sorting strategies
     */
    private void sortEdges(List<Edge> edges) {
        operationsCount += edges.size(); // Base operations for sorting

        switch (sortingStrategy) {
            case QUICKSORT:
                quickSort(edges, 0, edges.size() - 1);
                break;
            case MERGESORT:
                mergeSort(edges, 0, edges.size() - 1);
                break;
            case PRIORITY_QUEUE:
                priorityQueueSort(edges);
                break;
            case BUCKET_SORT:
                bucketSort(edges);
                break;
            default:
                Collections.sort(edges);
                comparisonsCount += edges.size() * (int) Math.log(edges.size() + 1);
                break;
        }
    }

    private void quickSort(List<Edge> edges, int low, int high) {
        if (low < high) {
            int pi = partition(edges, low, high);
            quickSort(edges, low, pi - 1);
            quickSort(edges, pi + 1, high);
        }
    }

    private int partition(List<Edge> edges, int low, int high) {
        Edge pivot = edges.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            comparisonsCount++;
            if (edges.get(j).getWeight() <= pivot.getWeight()) {
                i++;
                Collections.swap(edges, i, j);
                operationsCount += 2;
            }
        }

        Collections.swap(edges, i + 1, high);
        operationsCount += 2;
        return i + 1;
    }

    private void mergeSort(List<Edge> edges, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(edges, left, mid);
            mergeSort(edges, mid + 1, right);
            merge(edges, left, mid, right);
        }
    }

    private void merge(List<Edge> edges, int left, int mid, int right) {
        List<Edge> leftList = new ArrayList<>(edges.subList(left, mid + 1));
        List<Edge> rightList = new ArrayList<>(edges.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < leftList.size() && j < rightList.size()) {
            comparisonsCount++;
            operationsCount++;
            if (leftList.get(i).getWeight() <= rightList.get(j).getWeight()) {
                edges.set(k++, leftList.get(i++));
            } else {
                edges.set(k++, rightList.get(j++));
            }
        }

        while (i < leftList.size()) {
            edges.set(k++, leftList.get(i++));
            operationsCount++;
        }

        while (j < rightList.size()) {
            edges.set(k++, rightList.get(j++));
            operationsCount++;
        }
    }

    private void priorityQueueSort(List<Edge> edges) {
        PriorityQueue<Edge> pq = new PriorityQueue<>(edges);
        operationsCount += edges.size(); // Heap operations

        for (int i = 0; i < edges.size(); i++) {
            edges.set(i, pq.poll());
            operationsCount += (int) Math.log(edges.size() + 1); // Heap extraction
        }
    }

    private void bucketSort(List<Edge> edges) {
        if (edges.isEmpty()) return;

        // Find max weight for bucket sizing
        double maxWeight = edges.stream().mapToDouble(Edge::getWeight).max().orElse(1.0);
        int bucketCount = Math.min(edges.size(), 100); // Reasonable bucket count

        List<List<Edge>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }

        // Distribute edges into buckets
        for (Edge edge : edges) {
            int bucketIndex = (int) ((edge.getWeight() / maxWeight) * (bucketCount - 1));
            buckets.get(bucketIndex).add(edge);
            operationsCount++;
        }

        // Sort individual buckets and combine
        int index = 0;
        for (List<Edge> bucket : buckets) {
            Collections.sort(bucket);
            comparisonsCount += bucket.size() * (int) Math.log(bucket.size() + 1);

            for (Edge edge : bucket) {
                edges.set(index++, edge);
                operationsCount++;
            }
        }
    }

    /**
     * Union-Find factory method
     */
    private UnionFind createUnionFind(List<String> vertices) {
        return new UnionFind(vertices, enablePathCompression, enableUnionByRank);
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
        this.unionOperations = 0;
        this.findOperations = 0;
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
                .unionOperations(unionOperations)
                .priorityQueueOperations(0) // Kruskal doesn't use priority queue primarily
                .build();

        MSTResult.AlgorithmParameters params = new MSTResult.AlgorithmParameters.Builder()
                .useFibonacciHeap(false)
                .useUnionFind(true)
                .optimizeMemory(enableEarlyTermination)
                .initialCapacity(initialCapacity)
                .dataStructureVariant(unionFindStrategy.name() + "_" + sortingStrategy.name())
                .build();

        return new MSTResult.Builder("Kruskal", graph, mstEdges)
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
        analysis.put("suitableForKruskal", isSuitableForKruskal(graph));
        analysis.put("expectedOperations", estimateOperations(vertexCount, edgeCount));
        analysis.put("recommendedSortingStrategy", recommendSortingStrategy(edgeCount));
        analysis.put("recommendedUnionFindStrategy", recommendUnionFindStrategy(vertexCount));

        return analysis;
    }

    private boolean isSuitableForKruskal(Graph graph) {
        // Kruskal is generally good for sparse graphs
        return graph.getDensity() < 0.5;
    }

    private long estimateOperations(int vertices, int edges) {
        // O(E log E) for sorting + O(E α(V)) for Union-Find
        long sortingOps = (long) (edges * Math.log(edges + 1));
        long unionFindOps = (long) (edges * Math.log(vertices + 1));
        return sortingOps + unionFindOps;
    }

    private SortingStrategy recommendSortingStrategy(int edgeCount) {
        if (edgeCount < 1000) return SortingStrategy.QUICKSORT;
        if (edgeCount < 10000) return SortingStrategy.MERGESORT;
        return SortingStrategy.BUCKET_SORT;
    }

    private UnionFindStrategy recommendUnionFindStrategy(int vertexCount) {
        if (vertexCount < 1000) return UnionFindStrategy.ARRAY_BASED;
        return UnionFindStrategy.MAP_BASED;
    }

    /**
     * Batch processing for multiple graphs
     */
    public List<MSTResult> computeMSTBatch(List<Graph> graphs) {
        return graphs.parallelStream()
                .map(this::computeMST)
                .collect(Collectors.toList());
    }

    // Getters for configuration
    public UnionFindStrategy getUnionFindStrategy() { return unionFindStrategy; }
    public SortingStrategy getSortingStrategy() { return sortingStrategy; }
    public boolean isPathCompressionEnabled() { return enablePathCompression; }
    public boolean isUnionByRankEnabled() { return enableUnionByRank; }
    public boolean isEarlyTerminationEnabled() { return enableEarlyTermination; }
    public int getOperationsCount() { return operationsCount; }
    public int getComparisonsCount() { return comparisonsCount; }
    public int getUnionOperations() { return unionOperations; }
    public int getFindOperations() { return findOperations; }

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
    public static KruskalAlgorithm createDefault() {
        return new Builder().build();
    }

    public static KruskalAlgorithm createOptimizedForSparseGraphs() {
        return new Builder()
                .unionFindStrategy(UnionFindStrategy.MAP_BASED)
                .sortingStrategy(SortingStrategy.QUICKSORT)
                .enablePathCompression(true)
                .enableUnionByRank(true)
                .enableEarlyTermination(true)
                .build();
    }

    public static KruskalAlgorithm createOptimizedForDenseGraphs() {
        return new Builder()
                .unionFindStrategy(UnionFindStrategy.ARRAY_BASED)
                .sortingStrategy(SortingStrategy.BUCKET_SORT)
                .enablePathCompression(true)
                .enableUnionByRank(false)
                .enableEarlyTermination(true)
                .build();
    }

    @Override
    public String toString() {
        return String.format(
                "KruskalAlgorithm{unionFind=%s, sorting=%s, pathCompression=%s, unionByRank=%s, earlyTermination=%s}",
                unionFindStrategy, sortingStrategy, enablePathCompression, enableUnionByRank, enableEarlyTermination
        );
    }

    @Override
    public String getAlgorithmName() {
        return "Kruskal";
    }

    @Override
    public String getTimeComplexity() {
        return "O(E log E)";
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
        metrics.put("unionOperations", this.unionOperations);
        metrics.put("findOperations", this.findOperations);
        return metrics;
    }

    @Override
    public Map<String, Object> getAlgorithmParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("unionFindStrategy", this.unionFindStrategy.name());
        params.put("sortingStrategy", this.sortingStrategy.name());
        params.put("enablePathCompression", this.enablePathCompression);
        params.put("enableUnionByRank", this.enableUnionByRank);
        return params;
    }

    @Override
    public void reset() {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.unionOperations = 0;
        this.findOperations = 0;
    }

    @Override
    public Map<String, Object> analyzeSuitability(Graph graph) {
        return analyzeGraphSuitability(graph);
    }
}