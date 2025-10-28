package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Comprehensive MST Result class for storing and analyzing Minimum Spanning Tree results
 * Supports comparison between Prim's and Kruskal's algorithms with detailed metrics
 */
public class MSTResult {
    private final String algorithmName;
    private final Graph graph;
    private final List<Edge> mstEdges;
    private final double totalCost;
    private final PerformanceMetrics performanceMetrics;
    private final AlgorithmParameters parameters;
    private final MSTProperties mstProperties;
    private final String resultId;
    private final long timestamp;

    /**
     * Performance metrics for algorithm analysis
     */
    public static class PerformanceMetrics {
        private final long executionTimeMs;
        private final int operationsCount;
        private final long memoryUsageBytes;
        private final int comparisonsCount;
        private final int unionOperations;
        private final int priorityQueueOperations;
        private final double operationsPerMillisecond;

        private PerformanceMetrics(Builder builder) {
            this.executionTimeMs = builder.executionTimeMs;
            this.operationsCount = builder.operationsCount;
            this.memoryUsageBytes = builder.memoryUsageBytes;
            this.comparisonsCount = builder.comparisonsCount;
            this.unionOperations = builder.unionOperations;
            this.priorityQueueOperations = builder.priorityQueueOperations;
            this.operationsPerMillisecond = executionTimeMs > 0 ?
                    (double) operationsCount / executionTimeMs : 0.0;
        }

        public static class Builder {
            private long executionTimeMs;
            private int operationsCount;
            private long memoryUsageBytes;
            private int comparisonsCount;
            private int unionOperations;
            private int priorityQueueOperations;

            public Builder executionTimeMs(long executionTimeMs) {
                this.executionTimeMs = executionTimeMs;
                return this;
            }

            public Builder operationsCount(int operationsCount) {
                this.operationsCount = operationsCount;
                return this;
            }

            public Builder memoryUsageBytes(long memoryUsageBytes) {
                this.memoryUsageBytes = memoryUsageBytes;
                return this;
            }

            public Builder comparisonsCount(int comparisonsCount) {
                this.comparisonsCount = comparisonsCount;
                return this;
            }

            public Builder unionOperations(int unionOperations) {
                this.unionOperations = unionOperations;
                return this;
            }

            public Builder priorityQueueOperations(int priorityQueueOperations) {
                this.priorityQueueOperations = priorityQueueOperations;
                return this;
            }

            public PerformanceMetrics build() {
                return new PerformanceMetrics(this);
            }
        }

        // Getters
        public long getExecutionTimeMs() { return executionTimeMs; }
        public int getOperationsCount() { return operationsCount; }
        public long getMemoryUsageBytes() { return memoryUsageBytes; }
        public int getComparisonsCount() { return comparisonsCount; }
        public int getUnionOperations() { return unionOperations; }
        public int getPriorityQueueOperations() { return priorityQueueOperations; }
        public double getOperationsPerMillisecond() { return operationsPerMillisecond; }

        @Override
        public String toString() {
            return String.format(
                    "PerformanceMetrics{time=%dms, operations=%d, memory=%dB, ops/ms=%.2f}",
                    executionTimeMs, operationsCount, memoryUsageBytes, operationsPerMillisecond
            );
        }
    }

    /**
     * Algorithm parameters used for this MST computation
     */
    public static class AlgorithmParameters {
        private final boolean useFibonacciHeap;
        private final boolean useUnionFind;
        private final boolean optimizeMemory;
        private final int initialCapacity;
        private final String dataStructureVariant;

        private AlgorithmParameters(Builder builder) {
            this.useFibonacciHeap = builder.useFibonacciHeap;
            this.useUnionFind = builder.useUnionFind;
            this.optimizeMemory = builder.optimizeMemory;
            this.initialCapacity = builder.initialCapacity;
            this.dataStructureVariant = builder.dataStructureVariant;
        }

        public static class Builder {
            private boolean useFibonacciHeap = false;
            private boolean useUnionFind = true;
            private boolean optimizeMemory = false;
            private int initialCapacity = 100;
            private String dataStructureVariant = "standard";

            public Builder useFibonacciHeap(boolean useFibonacciHeap) {
                this.useFibonacciHeap = useFibonacciHeap;
                return this;
            }

            public Builder useUnionFind(boolean useUnionFind) {
                this.useUnionFind = useUnionFind;
                return this;
            }

            public Builder optimizeMemory(boolean optimizeMemory) {
                this.optimizeMemory = optimizeMemory;
                return this;
            }

            public Builder initialCapacity(int initialCapacity) {
                this.initialCapacity = initialCapacity;
                return this;
            }

            public Builder dataStructureVariant(String dataStructureVariant) {
                this.dataStructureVariant = dataStructureVariant;
                return this;
            }

            public AlgorithmParameters build() {
                return new AlgorithmParameters(this);
            }
        }

        // Getters
        public boolean isUseFibonacciHeap() { return useFibonacciHeap; }
        public boolean isUseUnionFind() { return useUnionFind; }
        public boolean isOptimizeMemory() { return optimizeMemory; }
        public int getInitialCapacity() { return initialCapacity; }
        public String getDataStructureVariant() { return dataStructureVariant; }

        @Override
        public String toString() {
            return String.format(
                    "AlgorithmParameters{fibHeap=%s, unionFind=%s, optimizeMem=%s, capacity=%d, variant=%s}",
                    useFibonacciHeap, useUnionFind, optimizeMemory, initialCapacity, dataStructureVariant
            );
        }
    }

    /**
     * MST-specific properties and characteristics
     */
    public static class MSTProperties {
        private final int vertexCount;
        private final int edgeCount;
        private final double density;
        private final double averageDegree;
        private final int diameter;
        private final double averageEdgeWeight;
        private final double maxEdgeWeight;
        private final double minEdgeWeight;
        private final List<Edge> criticalEdges;
        private final List<Edge> bridgeEdges;

        private MSTProperties(Builder builder) {
            this.vertexCount = builder.vertexCount;
            this.edgeCount = builder.edgeCount;
            this.density = builder.density;
            this.averageDegree = builder.averageDegree;
            this.diameter = builder.diameter;
            this.averageEdgeWeight = builder.averageEdgeWeight;
            this.maxEdgeWeight = builder.maxEdgeWeight;
            this.minEdgeWeight = builder.minEdgeWeight;
            this.criticalEdges = builder.criticalEdges;
            this.bridgeEdges = builder.bridgeEdges;
        }

        public static class Builder {
            private int vertexCount;
            private int edgeCount;
            private double density;
            private double averageDegree;
            private int diameter;
            private double averageEdgeWeight;
            private double maxEdgeWeight;
            private double minEdgeWeight;
            private List<Edge> criticalEdges = new ArrayList<>();
            private List<Edge> bridgeEdges = new ArrayList<>();

            public Builder vertexCount(int vertexCount) {
                this.vertexCount = vertexCount;
                return this;
            }

            public Builder edgeCount(int edgeCount) {
                this.edgeCount = edgeCount;
                return this;
            }

            public Builder density(double density) {
                this.density = density;
                return this;
            }

            public Builder averageDegree(double averageDegree) {
                this.averageDegree = averageDegree;
                return this;
            }

            public Builder diameter(int diameter) {
                this.diameter = diameter;
                return this;
            }

            public Builder averageEdgeWeight(double averageEdgeWeight) {
                this.averageEdgeWeight = averageEdgeWeight;
                return this;
            }

            public Builder maxEdgeWeight(double maxEdgeWeight) {
                this.maxEdgeWeight = maxEdgeWeight;
                return this;
            }

            public Builder minEdgeWeight(double minEdgeWeight) {
                this.minEdgeWeight = minEdgeWeight;
                return this;
            }

            public Builder criticalEdges(List<Edge> criticalEdges) {
                this.criticalEdges = criticalEdges != null ? new ArrayList<>(criticalEdges) : new ArrayList<>();
                return this;
            }

            public Builder bridgeEdges(List<Edge> bridgeEdges) {
                this.bridgeEdges = bridgeEdges != null ? new ArrayList<>(bridgeEdges) : new ArrayList<>();
                return this;
            }

            public MSTProperties build() {
                return new MSTProperties(this);
            }
        }

        // Getters
        public int getVertexCount() { return vertexCount; }
        public int getEdgeCount() { return edgeCount; }
        public double getDensity() { return density; }
        public double getAverageDegree() { return averageDegree; }
        public int getDiameter() { return diameter; }
        public double getAverageEdgeWeight() { return averageEdgeWeight; }
        public double getMaxEdgeWeight() { return maxEdgeWeight; }
        public double getMinEdgeWeight() { return minEdgeWeight; }
        public List<Edge> getCriticalEdges() { return new ArrayList<>(criticalEdges); }
        public List<Edge> getBridgeEdges() { return new ArrayList<>(bridgeEdges); }

        @Override
        public String toString() {
            return String.format(
                    "MSTProperties{vertices=%d, edges=%d, density=%.3f, avgDegree=%.2f, diameter=%d}",
                    vertexCount, edgeCount, density, averageDegree, diameter
            );
        }
    }

    /**
     * Builder pattern for MSTResult
     */
    public static class Builder {
        private final String algorithmName;
        private final Graph graph;
        private final List<Edge> mstEdges;
        private PerformanceMetrics performanceMetrics;
        private AlgorithmParameters parameters;
        private MSTProperties mstProperties;

        public Builder(String algorithmName, Graph graph, List<Edge> mstEdges) {
            if (algorithmName == null || algorithmName.trim().isEmpty()) {
                throw new IllegalArgumentException("Algorithm name cannot be null or empty");
            }
            if (graph == null) {
                throw new IllegalArgumentException("Graph cannot be null");
            }
            if (mstEdges == null) {
                throw new IllegalArgumentException("MST edges cannot be null");
            }

            this.algorithmName = algorithmName;
            this.graph = graph;
            this.mstEdges = new ArrayList<>(mstEdges);
            this.performanceMetrics = new PerformanceMetrics.Builder().build();
            this.parameters = new AlgorithmParameters.Builder().build();
            this.mstProperties = calculateMSTProperties(graph, mstEdges);
        }

        public Builder performanceMetrics(PerformanceMetrics performanceMetrics) {
            this.performanceMetrics = performanceMetrics;
            return this;
        }

        public Builder parameters(AlgorithmParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder mstProperties(MSTProperties mstProperties) {
            this.mstProperties = mstProperties;
            return this;
        }

        public MSTResult build() {
            return new MSTResult(this);
        }

        private MSTProperties calculateMSTProperties(Graph graph, List<Edge> mstEdges) {
            double totalWeight = mstEdges.stream().mapToDouble(Edge::getWeight).sum();
            double avgWeight = mstEdges.isEmpty() ? 0 : totalWeight / mstEdges.size();
            double maxWeight = mstEdges.stream().mapToDouble(Edge::getWeight).max().orElse(0);
            double minWeight = mstEdges.stream().mapToDouble(Edge::getWeight).min().orElse(0);

            return new MSTProperties.Builder()
                    .vertexCount(graph.getVerticesCount())
                    .edgeCount(mstEdges.size())
                    .density(graph.getDensity())
                    .averageDegree(calculateAverageDegree(mstEdges, graph.getVerticesCount()))
                    .diameter(estimateDiameter(mstEdges))
                    .averageEdgeWeight(avgWeight)
                    .maxEdgeWeight(maxWeight)
                    .minEdgeWeight(minWeight)
                    .criticalEdges(identifyCriticalEdges(mstEdges))
                    .bridgeEdges(identifyBridgeEdges(mstEdges))
                    .build();
        }

        private double calculateAverageDegree(List<Edge> edges, int vertexCount) {
            if (vertexCount == 0) return 0;
            return (2.0 * edges.size()) / vertexCount;
        }

        private int estimateDiameter(List<Edge> edges) {
            // Simplified diameter estimation for MST (tree diameter)
            return edges.isEmpty() ? 0 : edges.size();
        }

        private List<Edge> identifyCriticalEdges(List<Edge> edges) {
            // Edges with minimum weight that are critical for connectivity
            if (edges.isEmpty()) return new ArrayList<>();

            double minWeight = edges.stream().mapToDouble(Edge::getWeight).min().orElse(0);
            return edges.stream()
                    .filter(e -> e.getWeight() == minWeight)
                    .collect(Collectors.toList());
        }

        private List<Edge> identifyBridgeEdges(List<Edge> edges) {
            // In MST, all edges are bridges by definition
            return new ArrayList<>(edges);
        }
    }

    /**
     * Private constructor
     */
    private MSTResult(Builder builder) {
        this.algorithmName = builder.algorithmName;
        this.graph = builder.graph;
        this.mstEdges = builder.mstEdges;
        this.totalCost = calculateTotalCost(builder.mstEdges);
        this.performanceMetrics = builder.performanceMetrics;
        this.parameters = builder.parameters;
        this.mstProperties = builder.mstProperties;
        this.resultId = generateResultId();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Core functionality methods
     */
    private double calculateTotalCost(List<Edge> edges) {
        return edges.stream().mapToDouble(Edge::getWeight).sum();
    }

    private String generateResultId() {
        return algorithmName + "-" + graph.getVerticesCount() + "v-" +
                graph.getEdgesCount() + "e-" + System.currentTimeMillis();
    }

    /**
     * Validation methods
     */
    public boolean isValidMST() {
        if (mstEdges.size() != graph.getVerticesCount() - 1) {
            return false; // MST should have V-1 edges
        }

        if (Math.abs(totalCost - calculateTotalCost(mstEdges)) > 1e-10) {
            return false; // Cost consistency check
        }

        return isConnectedTree();
    }

    private boolean isConnectedTree() {
        // Basic connectivity check for MST
        Set<String> visited = new HashSet<>();
        if (mstEdges.isEmpty()) return graph.getVerticesCount() <= 1;

        String startVertex = mstEdges.get(0).getFrom();
        dfsTree(startVertex, visited);

        return visited.size() == graph.getVerticesCount();
    }

    private void dfsTree(String current, Set<String> visited) {
        visited.add(current);
        for (Edge edge : mstEdges) {
            if (edge.containsVertex(current)) {
                String neighbor = edge.getOtherVertex(current);
                if (!visited.contains(neighbor)) {
                    dfsTree(neighbor, visited);
                }
            }
        }
    }

    /**
     * Comparison methods
     */
    public boolean isEquivalentTo(MSTResult other) {
        if (other == null) return false;

        // Two MSTs are equivalent if they have the same total cost
        return Math.abs(this.totalCost - other.totalCost) < 1e-10;
    }

    public double getCostDifference(MSTResult other) {
        return Math.abs(this.totalCost - other.totalCost);
    }

    public double getPerformanceImprovement(MSTResult other) {
        if (other.performanceMetrics.getExecutionTimeMs() == 0) return 0;
        double timeRatio = (double) this.performanceMetrics.getExecutionTimeMs() /
                other.performanceMetrics.getExecutionTimeMs();
        return (1 - timeRatio) * 100; // Percentage improvement
    }

    /**
     * Analysis methods
     */
    public Map<String, Object> getDetailedAnalysis() {
        Map<String, Object> analysis = new LinkedHashMap<>();

        // Basic info
        analysis.put("algorithm", algorithmName);
        analysis.put("resultId", resultId);
        analysis.put("timestamp", new Date(timestamp));
        analysis.put("validMST", isValidMST());

        // Cost analysis
        analysis.put("totalCost", totalCost);
        analysis.put("costPerVertex", graph.getVerticesCount() > 0 ?
                totalCost / graph.getVerticesCount() : 0);
        analysis.put("costPerEdge", mstEdges.size() > 0 ?
                totalCost / mstEdges.size() : 0);

        // Performance analysis
        analysis.put("executionTimeMs", performanceMetrics.getExecutionTimeMs());
        analysis.put("operationsCount", performanceMetrics.getOperationsCount());
        analysis.put("efficiency", performanceMetrics.getOperationsPerMillisecond());
        analysis.put("memoryEfficiency", performanceMetrics.getMemoryUsageBytes() > 0 ?
                (double) performanceMetrics.getOperationsCount() / performanceMetrics.getMemoryUsageBytes() : 0);

        // MST structure analysis
        analysis.put("verticesInMST", mstProperties.getVertexCount());
        analysis.put("edgesInMST", mstProperties.getEdgeCount());
        analysis.put("mstDensity", mstProperties.getDensity());
        analysis.put("averageDegree", mstProperties.getAverageDegree());
        analysis.put("estimatedDiameter", mstProperties.getDiameter());

        return Collections.unmodifiableMap(analysis);
    }

    public List<Edge> getEdgesSortedByWeight() {
        return mstEdges.stream()
                .sorted(Edge::compareByWeight)
                .collect(Collectors.toList());
    }

    public List<Edge> getCriticalPathEdges() {
        return getEdgesSortedByWeight().subList(0,
                Math.min(3, mstEdges.size())); // Top 3 lightest edges
    }

    /**
     * Export methods
     */
    public String toJsonString() {
        return String.format(
                "{\"algorithm\":\"%s\", \"totalCost\":%.2f, \"edges\":%d, \"timeMs\":%d, \"operations\":%d, \"valid\":%s}",
                algorithmName, totalCost, mstEdges.size(),
                performanceMetrics.getExecutionTimeMs(), performanceMetrics.getOperationsCount(),
                isValidMST()
        );
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithmName", algorithmName);
        result.put("resultId", resultId);
        result.put("timestamp", timestamp);
        result.put("totalCost", totalCost);
        result.put("edgesCount", mstEdges.size());
        result.put("performance", performanceMetrics);
        result.put("parameters", parameters);
        result.put("properties", mstProperties);
        result.put("isValid", isValidMST());

        // Include edge details
        result.put("edges", mstEdges.stream()
                .map(Edge::toDTO)
                .collect(Collectors.toList()));

        return Collections.unmodifiableMap(result);
    }

    // Getters
    public String getAlgorithmName() { return algorithmName; }
    public Graph getGraph() { return graph; }
    public List<Edge> getMstEdges() { return new ArrayList<>(mstEdges); }
    public double getTotalCost() { return totalCost; }
    public PerformanceMetrics getPerformanceMetrics() { return performanceMetrics; }
    public AlgorithmParameters getParameters() { return parameters; }
    public MSTProperties getMstProperties() { return mstProperties; }
    public String getResultId() { return resultId; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format(
                "MSTResult{algorithm=%s, cost=%.2f, edges=%d, time=%dms, operations=%d, valid=%s}",
                algorithmName, totalCost, mstEdges.size(),
                performanceMetrics.getExecutionTimeMs(), performanceMetrics.getOperationsCount(),
                isValidMST()
        );
    }

    /**
     * Static utility methods
     */
    public static MSTResult compareAlgorithms(MSTResult primResult, MSTResult kruskalResult) {
        if (primResult == null || kruskalResult == null) {
            throw new IllegalArgumentException("Both results must be non-null");
        }

        // For comparison, return the more efficient result
        if (primResult.performanceMetrics.getExecutionTimeMs() <
                kruskalResult.performanceMetrics.getExecutionTimeMs()) {
            return primResult;
        } else {
            return kruskalResult;
        }
    }

    public static Map<String, Object> createComparisonReport(MSTResult result1, MSTResult result2) {
        Map<String, Object> comparison = new LinkedHashMap<>();

        comparison.put("costEquivalent", result1.isEquivalentTo(result2));
        comparison.put("costDifference", result1.getCostDifference(result2));
        comparison.put("performanceImprovement", result1.getPerformanceImprovement(result2));
        comparison.put("fasterAlgorithm",
                result1.performanceMetrics.getExecutionTimeMs() < result2.performanceMetrics.getExecutionTimeMs() ?
                        result1.getAlgorithmName() : result2.getAlgorithmName());
        comparison.put("moreEfficientAlgorithm",
                result1.performanceMetrics.getOperationsPerMillisecond() > result2.performanceMetrics.getOperationsPerMillisecond() ?
                        result1.getAlgorithmName() : result2.getAlgorithmName());

        return Collections.unmodifiableMap(comparison);
    }
}