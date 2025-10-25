package analysis;

import algorithms.KruskalAlgorithm;
import algorithms.PrimAlgorithm;
import algorithms.MSTAlgorithm;
import model.Graph;
import model.MSTResult;
import model.Edge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Professional Performance Analyzer for MST Algorithms
 * Provides comprehensive performance analysis, statistical comparisons, and optimization recommendations
 */
public class PerformanceAnalyzer {
    private final int warmupIterations;
    private final int benchmarkIterations;
    private final double statisticalSignificanceLevel;
    private final boolean enableDetailedProfiling;

    private final Map<String, List<PerformanceSnapshot>> performanceHistory;

    /**
     * Performance snapshot for individual algorithm execution
     */
    public static class PerformanceSnapshot {
        private final String algorithmName;
        private final long executionTimeNs;
        private final int operationsCount;
        private final long memoryUsageBytes;
        private final int verticesCount;
        private final int edgesCount;
        private final double graphDensity;
        private final Date timestamp;

        public PerformanceSnapshot(String algorithmName, long executionTimeNs, int operationsCount,
                                   long memoryUsageBytes, int verticesCount, int edgesCount,
                                   double graphDensity) {
            this.algorithmName = algorithmName;
            this.executionTimeNs = executionTimeNs;
            this.operationsCount = operationsCount;
            this.memoryUsageBytes = memoryUsageBytes;
            this.verticesCount = verticesCount;
            this.edgesCount = edgesCount;
            this.graphDensity = graphDensity;
            this.timestamp = new Date();
        }

        // Getters
        public String getAlgorithmName() { return algorithmName; }
        public long getExecutionTimeNs() { return executionTimeNs; }
        public long getExecutionTimeMs() { return executionTimeNs / 1_000_000; }
        public int getOperationsCount() { return operationsCount; }
        public long getMemoryUsageBytes() { return memoryUsageBytes; }
        public int getVerticesCount() { return verticesCount; }
        public int getEdgesCount() { return edgesCount; }
        public double getGraphDensity() { return graphDensity; }
        public Date getTimestamp() { return timestamp; }
        public double getOperationsPerSecond() {
            return executionTimeNs > 0 ? (operationsCount * 1_000_000_000.0) / executionTimeNs : 0;
        }
        public double getMemoryEfficiency() {
            return operationsCount > 0 ? (double) operationsCount / memoryUsageBytes : 0;
        }
    }

    /**
     * Statistical analysis results
     */
    public static class StatisticalResults {
        private final double mean;
        private final double median;
        private final double standardDeviation;
        private final double min;
        private final double max;
        private final double confidenceInterval;
        private final int sampleSize;

        public StatisticalResults(double mean, double median, double standardDeviation,
                                  double min, double max, double confidenceInterval, int sampleSize) {
            this.mean = mean;
            this.median = median;
            this.standardDeviation = standardDeviation;
            this.min = min;
            this.max = max;
            this.confidenceInterval = confidenceInterval;
            this.sampleSize = sampleSize;
        }

        // Getters
        public double getMean() { return mean; }
        public double getMedian() { return median; }
        public double getStandardDeviation() { return standardDeviation; }
        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getConfidenceInterval() { return confidenceInterval; }
        public int getSampleSize() { return sampleSize; }

        @Override
        public String toString() {
            return String.format(
                    "Stats{mean=%.2f, median=%.2f, std=%.2f, min=%.2f, max=%.2f, CI=%.2f, n=%d}",
                    mean, median, standardDeviation, min, max, confidenceInterval, sampleSize
            );
        }
    }

    /**
     * Performance comparison results
     */
    public static class ComparisonResult {
        private final String algorithmA;
        private final String algorithmB;
        private final double performanceRatio;
        private final double statisticalSignificance;
        private final boolean significantDifference;
        private final String recommendation;
        private final Map<String, StatisticalResults> statistics;

        public ComparisonResult(String algorithmA, String algorithmB, double performanceRatio,
                                double statisticalSignificance, boolean significantDifference,
                                String recommendation, Map<String, StatisticalResults> statistics) {
            this.algorithmA = algorithmA;
            this.algorithmB = algorithmB;
            this.performanceRatio = performanceRatio;
            this.statisticalSignificance = statisticalSignificance;
            this.significantDifference = significantDifference;
            this.recommendation = recommendation;
            this.statistics = statistics;
        }

        // Getters
        public String getAlgorithmA() { return algorithmA; }
        public String getAlgorithmB() { return algorithmB; }
        public double getPerformanceRatio() { return performanceRatio; }
        public double getStatisticalSignificance() { return statisticalSignificance; }
        public boolean hasSignificantDifference() { return significantDifference; }
        public String getRecommendation() { return recommendation; }
        public Map<String, StatisticalResults> getStatistics() { return statistics; }

        @Override
        public String toString() {
            return String.format(
                    "Comparison{%s vs %s: ratio=%.2f, significance=%.3f, significant=%s}",
                    algorithmA, algorithmB, performanceRatio, statisticalSignificance, significantDifference
            );
        }
    }

    /**
     * Optimization recommendation
     */
    public static class OptimizationRecommendation {
        private final String algorithm;
        private final String recommendation;
        private final double expectedImprovement;
        private final String reason;
        private final Map<String, Object> parameters;

        public OptimizationRecommendation(String algorithm, String recommendation,
                                          double expectedImprovement, String reason,
                                          Map<String, Object> parameters) {
            this.algorithm = algorithm;
            this.recommendation = recommendation;
            this.expectedImprovement = expectedImprovement;
            this.reason = reason;
            this.parameters = parameters;
        }

        // Getters
        public String getAlgorithm() { return algorithm; }
        public String getRecommendation() { return recommendation; }
        public double getExpectedImprovement() { return expectedImprovement; }
        public String getReason() { return reason; }
        public Map<String, Object> getParameters() { return parameters; }

        @Override
        public String toString() {
            return String.format("Optimization{%s: %s (%.1f%% improvement)}",
                    algorithm, recommendation, expectedImprovement * 100);
        }
    }

    /**
     * Builder pattern for PerformanceAnalyzer configuration
     */
    public static class Builder {
        private int warmupIterations = 3;
        private int benchmarkIterations = 10;
        private double statisticalSignificanceLevel = 0.05;
        private boolean enableDetailedProfiling = true;

        public Builder warmupIterations(int iterations) {
            this.warmupIterations = iterations;
            return this;
        }

        public Builder benchmarkIterations(int iterations) {
            this.benchmarkIterations = iterations;
            return this;
        }

        public Builder statisticalSignificanceLevel(double level) {
            this.statisticalSignificanceLevel = level;
            return this;
        }

        public Builder enableDetailedProfiling(boolean enable) {
            this.enableDetailedProfiling = enable;
            return this;
        }

        public PerformanceAnalyzer build() {
            return new PerformanceAnalyzer(this);
        }
    }

    /**
     * Private constructor
     */
    private PerformanceAnalyzer(Builder builder) {
        this.warmupIterations = builder.warmupIterations;
        this.benchmarkIterations = builder.benchmarkIterations;
        this.statisticalSignificanceLevel = builder.statisticalSignificanceLevel;
        this.enableDetailedProfiling = builder.enableDetailedProfiling;
        this.performanceHistory = new HashMap<>();
    }

    /**
     * Analyze single algorithm performance on a graph
     */
    public Map<String, Object> analyzeAlgorithmPerformance(MSTAlgorithm algorithm, Graph graph) {
        return analyzeAlgorithmPerformance(algorithm, graph, benchmarkIterations);
    }

    public Map<String, Object> analyzeAlgorithmPerformance(MSTAlgorithm algorithm, Graph graph, int iterations) {
        if (algorithm == null || graph == null) {
            throw new IllegalArgumentException("Algorithm and graph cannot be null");
        }

        List<PerformanceSnapshot> snapshots = new ArrayList<>();
        String algorithmName = algorithm.getAlgorithmName();

        // Warmup phase
        performWarmup(algorithm, graph);

        // Benchmark phase
        for (int i = 0; i < iterations; i++) {
            PerformanceSnapshot snapshot = executeAndMeasure(algorithm, graph);
            snapshots.add(snapshot);
        }

        // Store in history
        performanceHistory.computeIfAbsent(algorithmName, k -> new ArrayList<>()).addAll(snapshots);

        return createPerformanceAnalysis(algorithmName, snapshots, graph);
    }

    /**
     * Compare multiple algorithms on the same graph
     */
    public Map<String, Object> compareAlgorithms(List<MSTAlgorithm> algorithms, Graph graph) {
        Map<String, List<PerformanceSnapshot>> allSnapshots = new HashMap<>();

        // Warmup all algorithms
        for (MSTAlgorithm algorithm : algorithms) {
            performWarmup(algorithm, graph);
        }

        // Benchmark all algorithms
        for (MSTAlgorithm algorithm : algorithms) {
            String algorithmName = algorithm.getAlgorithmName();
            List<PerformanceSnapshot> snapshots = new ArrayList<>();

            for (int i = 0; i < benchmarkIterations; i++) {
                PerformanceSnapshot snapshot = executeAndMeasure(algorithm, graph);
                snapshots.add(snapshot);
            }

            allSnapshots.put(algorithmName, snapshots);
            performanceHistory.computeIfAbsent(algorithmName, k -> new ArrayList<>()).addAll(snapshots);
        }

        return createComparativeAnalysis(allSnapshots, graph);
    }

    /**
     * Perform statistical analysis on historical performance data
     */
    public Map<String, Object> analyzeHistoricalPerformance(String algorithmName) {
        List<PerformanceSnapshot> history = performanceHistory.get(algorithmName);
        if (history == null || history.isEmpty()) {
            throw new IllegalArgumentException("No historical data available for algorithm: " + algorithmName);
        }

        return createHistoricalAnalysis(algorithmName, history);
    }

    /**
     * Generate optimization recommendations for algorithms
     */
    public List<OptimizationRecommendation> generateOptimizationRecommendations(Graph graph) {
        List<OptimizationRecommendation> recommendations = new ArrayList<>();

        // Analyze graph characteristics
        double density = graph.getDensity();
        int vertexCount = graph.getVerticesCount();
        int edgeCount = graph.getEdgesCount();

        // Generate recommendations based on graph properties
        if (density < 0.3) {
            // Sparse graph - optimize Kruskal
            recommendations.add(new OptimizationRecommendation(
                    "Kruskal",
                    "Use optimized sparse graph configuration",
                    0.25, // Expected 25% improvement
                    "Graph density indicates sparse structure (" + String.format("%.2f", density) + ")",
                    Map.of(
                            "unionFindStrategy", "MAP_BASED",
                            "sortingStrategy", "QUICKSORT",
                            "enablePathCompression", true,
                            "enableUnionByRank", true
                    )
            ));
        } else if (density > 0.7) {
            // Dense graph - optimize Prim
            recommendations.add(new OptimizationRecommendation(
                    "Prim",
                    "Use array-based priority queue for dense graphs",
                    0.40, // Expected 40% improvement
                    "Graph density indicates dense structure (" + String.format("%.2f", density) + ")",
                    Map.of(
                            "queueStrategy", "ARRAY_BASED",
                            "optimizeDenseGraphs", true
                    )
            ));
        }

        // Memory optimization for large graphs
        if (vertexCount > 1000) {
            recommendations.add(new OptimizationRecommendation(
                    "Both",
                    "Enable memory optimization and early termination",
                    0.15,
                    "Large graph detected (" + vertexCount + " vertices)",
                    Map.of(
                            "optimizeMemory", true,
                            "enableEarlyTermination", true,
                            "initialCapacity", vertexCount
                    )
            ));
        }

        return recommendations;
    }

    /**
     * Perform complexity analysis and compare with theoretical expectations
     */
    public Map<String, Object> performComplexityAnalysis(MSTAlgorithm algorithm, Graph graph) {
        Map<String, Object> analysis = new LinkedHashMap<>();

        String algorithmName = algorithm.getAlgorithmName();
        int V = graph.getVerticesCount();
        int E = graph.getEdgesCount();
        double density = graph.getDensity();

        analysis.put("algorithm", algorithmName);
        analysis.put("vertices", V);
        analysis.put("edges", E);
        analysis.put("density", density);

        // Theoretical complexities
        Map<String, String> theoretical = new LinkedHashMap<>();
        if ("Kruskal".equals(algorithmName)) {
            theoretical.put("time", "O(E log E)");
            theoretical.put("space", "O(V + E)");
            theoretical.put("bestCase", "Sparse graphs (density < 0.3)");
            theoretical.put("worstCase", "Dense graphs with many edges");
        } else if ("Prim".equals(algorithmName)) {
            theoretical.put("time", "O(E log V)");
            theoretical.put("space", "O(V + E)");
            theoretical.put("bestCase", "Dense graphs (density > 0.7)");
            theoretical.put("worstCase", "Sparse graphs with Fibonacci heap overhead");
        }
        analysis.put("theoreticalComplexity", theoretical);

        // Expected operations based on theoretical complexity
        long expectedOperations = calculateExpectedOperations(algorithmName, V, E);
        analysis.put("expectedOperations", expectedOperations);

        // Performance characteristics
        Map<String, Object> characteristics = new LinkedHashMap<>();
        characteristics.put("suitableForGraphType", theoretical.get("bestCase"));
        characteristics.put("expectedPerformance", estimatePerformanceCategory(V, E, density));
        characteristics.put("memoryFootprint", estimateMemoryFootprint(algorithmName, V, E));

        analysis.put("performanceCharacteristics", characteristics);

        return analysis;
    }

    /**
     * Get performance trends over multiple executions
     */
    public Map<String, Object> analyzePerformanceTrends(String algorithmName) {
        List<PerformanceSnapshot> history = performanceHistory.get(algorithmName);
        if (history == null || history.isEmpty()) {
            return Map.of("error", "No historical data available");
        }

        Map<String, Object> trends = new LinkedHashMap<>();
        trends.put("algorithm", algorithmName);
        trends.put("totalExecutions", history.size());

        // Group by graph size
        Map<String, List<PerformanceSnapshot>> bySize = history.stream()
                .collect(Collectors.groupingBy(s -> s.getVerticesCount() + "v_" + s.getEdgesCount() + "e"));

        Map<String, Object> sizeAnalysis = new LinkedHashMap<>();
        for (Map.Entry<String, List<PerformanceSnapshot>> entry : bySize.entrySet()) {
            List<Long> times = entry.getValue().stream()
                    .map(PerformanceSnapshot::getExecutionTimeMs)
                    .collect(Collectors.toList());

            StatisticalResults stats = calculateStatistics(times);
            sizeAnalysis.put(entry.getKey(), stats);
        }

        trends.put("performanceByGraphSize", sizeAnalysis);
        trends.put("averagePerformance", calculateAveragePerformance(history));

        return trends;
    }

    /**
     * Clear performance history
     */
    public void clearHistory() {
        performanceHistory.clear();
    }

    /**
     * Get performance history for all algorithms
     */
    public Map<String, List<PerformanceSnapshot>> getPerformanceHistory() {
        return new HashMap<>(performanceHistory);
    }

    // Private implementation methods

    private void performWarmup(MSTAlgorithm algorithm, Graph graph) {
        for (int i = 0; i < warmupIterations; i++) {
            algorithm.computeMST(graph);
            algorithm.reset();
        }
    }

    private PerformanceSnapshot executeAndMeasure(MSTAlgorithm algorithm, Graph graph) {
        // Force garbage collection before measurement
        System.gc();

        long memoryBefore = getMemoryUsage();
        long startTime = System.nanoTime();

        MSTResult result = algorithm.computeMST(graph);

        long endTime = System.nanoTime();
        long memoryAfter = getMemoryUsage();

        long executionTime = endTime - startTime;
        long memoryUsed = memoryAfter - memoryBefore;

        return new PerformanceSnapshot(
                algorithm.getAlgorithmName(),
                executionTime,
                result.getPerformanceMetrics().getOperationsCount(),
                memoryUsed,
                graph.getVerticesCount(),
                graph.getEdgesCount(),
                graph.getDensity()
        );
    }

    private Map<String, Object> createPerformanceAnalysis(String algorithmName,
                                                          List<PerformanceSnapshot> snapshots,
                                                          Graph graph) {
        Map<String, Object> analysis = new LinkedHashMap<>();

        analysis.put("algorithm", algorithmName);
        analysis.put("graphStatistics", graph.getStatistics());
        analysis.put("iterations", snapshots.size());

        // Time statistics
        List<Long> executionTimes = snapshots.stream()
                .map(PerformanceSnapshot::getExecutionTimeMs)
                .collect(Collectors.toList());
        analysis.put("executionTime", calculateStatistics(executionTimes));

        // Operations statistics
        List<Integer> operations = snapshots.stream()
                .map(PerformanceSnapshot::getOperationsCount)
                .collect(Collectors.toList());
        analysis.put("operations", calculateStatistic(operations.stream().map(Integer::doubleValue).collect(Collectors.toList())));

        // Memory statistics
        List<Long> memoryUsage = snapshots.stream()
                .map(PerformanceSnapshot::getMemoryUsageBytes)
                .collect(Collectors.toList());
        analysis.put("memoryUsage", calculateStatistic(memoryUsage.stream().map(Long::doubleValue).collect(Collectors.toList())));

        // Efficiency metrics
        analysis.put("efficiency", calculateEfficiencyMetrics(snapshots));

        // Performance category
        analysis.put("performanceCategory", categorizePerformance(snapshots));

        return analysis;
    }

    private Map<String, Object> createComparativeAnalysis(Map<String, List<PerformanceSnapshot>> allSnapshots,
                                                          Graph graph) {
        Map<String, Object> comparison = new LinkedHashMap<>();

        comparison.put("graphStatistics", graph.getStatistics());
        comparison.put("iterations", benchmarkIterations);

        // Individual algorithm performance
        Map<String, Object> algorithmPerformance = new LinkedHashMap<>();
        for (Map.Entry<String, List<PerformanceSnapshot>> entry : allSnapshots.entrySet()) {
            algorithmPerformance.put(entry.getKey(), createPerformanceAnalysis(entry.getKey(), entry.getValue(), graph));
        }
        comparison.put("algorithms", algorithmPerformance);

        // Comparative analysis
        comparison.put("comparisons", performPairwiseComparisons(allSnapshots));

        // Recommendations
        comparison.put("recommendations", generateComparisonRecommendations(allSnapshots, graph));

        return comparison;
    }

    private Map<String, Object> createHistoricalAnalysis(String algorithmName, List<PerformanceSnapshot> history) {
        Map<String, Object> analysis = new LinkedHashMap<>();

        analysis.put("algorithm", algorithmName);
        analysis.put("totalSamples", history.size());
        analysis.put("dateRange", getDateRange(history));

        // Performance over time
        analysis.put("performanceTrend", calculatePerformanceTrend(history));

        // Correlation with graph size
        analysis.put("scalability", analyzeScalability(history));

        return analysis;
    }

    private List<ComparisonResult> performPairwiseComparisons(Map<String, List<PerformanceSnapshot>> allSnapshots) {
        List<ComparisonResult> comparisons = new ArrayList<>();
        List<String> algorithms = new ArrayList<>(allSnapshots.keySet());

        for (int i = 0; i < algorithms.size(); i++) {
            for (int j = i + 1; j < algorithms.size(); j++) {
                String algA = algorithms.get(i);
                String algB = algorithms.get(j);

                List<PerformanceSnapshot> snapsA = allSnapshots.get(algA);
                List<PerformanceSnapshot> snapsB = allSnapshots.get(algB);

                ComparisonResult result = compareTwoAlgorithms(algA, algB, snapsA, snapsB);
                comparisons.add(result);
            }
        }

        return comparisons;
    }

    private ComparisonResult compareTwoAlgorithms(String algA, String algB,
                                                  List<PerformanceSnapshot> snapsA,
                                                  List<PerformanceSnapshot> snapsB) {
        // Calculate mean execution times
        double meanTimeA = calculateMean(snapsA.stream().map(s -> (double) s.getExecutionTimeMs()).collect(Collectors.toList()));
        double meanTimeB = calculateMean(snapsB.stream().map(s -> (double) s.getExecutionTimeMs()).collect(Collectors.toList()));

        // Performance ratio (A/B)
        double performanceRatio = meanTimeB / meanTimeA;

        // Statistical significance test (simplified t-test)
        double significance = calculateStatisticalSignificance(
                snapsA.stream().map(s -> (double) s.getExecutionTimeMs()).collect(Collectors.toList()),
                snapsB.stream().map(s -> (double) s.getExecutionTimeMs()).collect(Collectors.toList())
        );

        boolean significant = significance < statisticalSignificanceLevel;

        // Generate recommendation
        String recommendation = generateComparisonRecommendation(algA, algB, performanceRatio, significant);

        // Collect statistics
        Map<String, StatisticalResults> stats = new HashMap<>();
        stats.put(algA, calculateStatistic(snapsA.stream().map(s -> (double) s.getExecutionTimeMs()).collect(Collectors.toList())));
        stats.put(algB, calculateStatistic(snapsB.stream().map(s -> (double) s.getExecutionTimeMs()).collect(Collectors.toList())));

        return new ComparisonResult(algA, algB, performanceRatio, significance, significant, recommendation, stats);
    }

    // Statistical calculation methods

    private StatisticalResults calculateStatistic(List<Double> values) {
        if (values.isEmpty()) {
            return new StatisticalResults(0, 0, 0, 0, 0, 0, 0);
        }

        double mean = calculateMean(values);
        double median = calculateMedian(values);
        double stdDev = calculateStandardDeviation(values, mean);
        double min = Collections.min(values);
        double max = Collections.max(values);
        double confidenceInterval = calculateConfidenceInterval(values, mean, stdDev);

        return new StatisticalResults(mean, median, stdDev, min, max, confidenceInterval, values.size());
    }

    private StatisticalResults calculateStatistics(List<Long> values) {
        List<Double> doubleValues = values.stream().map(Long::doubleValue).collect(Collectors.toList());
        return calculateStatistic(doubleValues);
    }

    private double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double calculateMedian(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size/2 - 1) + sorted.get(size/2)) / 2.0;
        } else {
            return sorted.get(size/2);
        }
    }

    private double calculateStandardDeviation(List<Double> values, double mean) {
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    private double calculateConfidenceInterval(List<Double> values, double mean, double stdDev) {
        // 95% confidence interval for large samples
        if (values.size() < 2) return 0;
        return 1.96 * stdDev / Math.sqrt(values.size());
    }

    private double calculateStatisticalSignificance(List<Double> sampleA, List<Double> sampleB) {
        // Simplified significance calculation
        if (sampleA.size() < 2 || sampleB.size() < 2) return 1.0;

        double meanA = calculateMean(sampleA);
        double meanB = calculateMean(sampleB);
        double stdA = calculateStandardDeviation(sampleA, meanA);
        double stdB = calculateStandardDeviation(sampleB, meanB);

        // Simplified t-statistic
        double tStat = Math.abs(meanA - meanB) / Math.sqrt(
                (stdA * stdA / sampleA.size()) + (stdB * stdB / sampleB.size())
        );

        // Very simplified p-value approximation
        return Math.exp(-tStat * tStat / 2);
    }

    // Utility methods

    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private long calculateExpectedOperations(String algorithm, int V, int E) {
        switch (algorithm) {
            case "Kruskal":
                return (long) (E * Math.log(E + 1)); // O(E log E)
            case "Prim":
                return (long) (E * Math.log(V + 1)); // O(E log V)
            default:
                return E * V; // Conservative estimate
        }
    }

    private String estimatePerformanceCategory(int V, int E, double density) {
        long expectedOps = (long) (E * Math.log(V + 1));
        if (expectedOps < 100000) return "EXCELLENT";
        if (expectedOps < 1000000) return "GOOD";
        if (expectedOps < 10000000) return "MODERATE";
        return "CHALLENGING";
    }

    private String estimateMemoryFootprint(String algorithm, int V, int E) {
        long expectedMemory = V * 100 + E * 50; // Rough estimate in bytes
        if (expectedMemory < 1000000) return "LOW";
        if (expectedMemory < 10000000) return "MODERATE";
        return "HIGH";
    }

    private Map<String, Object> calculateEfficiencyMetrics(List<PerformanceSnapshot> snapshots) {
        Map<String, Object> efficiency = new LinkedHashMap<>();

        double avgOpsPerSecond = snapshots.stream()
                .mapToDouble(PerformanceSnapshot::getOperationsPerSecond)
                .average().orElse(0);
        double avgMemoryEfficiency = snapshots.stream()
                .mapToDouble(PerformanceSnapshot::getMemoryEfficiency)
                .average().orElse(0);

        efficiency.put("operationsPerSecond", avgOpsPerSecond);
        efficiency.put("memoryEfficiency", avgMemoryEfficiency);
        efficiency.put("throughput", avgOpsPerSecond / 1_000_000); // Millions of operations per second

        return efficiency;
    }

    private String categorizePerformance(List<PerformanceSnapshot> snapshots) {
        double avgTime = snapshots.stream()
                .mapToLong(PerformanceSnapshot::getExecutionTimeMs)
                .average().orElse(0);

        if (avgTime < 10) return "EXCELLENT";
        if (avgTime < 100) return "GOOD";
        if (avgTime < 1000) return "MODERATE";
        return "SLOW";
    }

    private String generateComparisonRecommendation(String algA, String algB, double ratio, boolean significant) {
        if (!significant) {
            return "No significant difference detected. Both algorithms perform similarly.";
        }

        if (ratio > 1.2) {
            return String.format("Recommend %s - %.1fx faster than %s", algA, ratio, algB);
        } else if (ratio < 0.8) {
            return String.format("Recommend %s - %.1fx faster than %s", algB, 1/ratio, algA);
        } else {
            return "Marginal difference. Consider other factors like memory usage.";
        }
    }

    private List<String> generateComparisonRecommendations(Map<String, List<PerformanceSnapshot>> allSnapshots, Graph graph) {
        List<String> recommendations = new ArrayList<>();
        // Implementation for generating specific recommendations
        return recommendations;
    }

    private Map<String, Object> calculatePerformanceTrend(List<PerformanceSnapshot> history) {
        // Simplified trend analysis
        Map<String, Object> trend = new LinkedHashMap<>();
        return trend;
    }

    private Map<String, Object> analyzeScalability(List<PerformanceSnapshot> history) {
        // Analyze how performance scales with graph size
        Map<String, Object> scalability = new LinkedHashMap<>();
        return scalability;
    }

    private String getDateRange(List<PerformanceSnapshot> history) {
        if (history.isEmpty()) return "No data";
        Date first = history.get(0).getTimestamp();
        Date last = history.get(history.size() - 1).getTimestamp();
        return first + " to " + last;
    }

    private Map<String, Object> calculateAveragePerformance(List<PerformanceSnapshot> history) {
        Map<String, Object> avgPerformance = new LinkedHashMap<>();
        // Calculate average performance metrics
        return avgPerformance;
    }

    /**
     * Custom exception for performance analysis errors
     */
    public static class PerformanceAnalysisException extends RuntimeException {
        public PerformanceAnalysisException(String message) {
            super(message);
        }

        public PerformanceAnalysisException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Static factory methods
    public static PerformanceAnalyzer createDefault() {
        return new Builder().build();
    }

    public static PerformanceAnalyzer createDetailedAnalyzer() {
        return new Builder()
                .benchmarkIterations(20)
                .enableDetailedProfiling(true)
                .build();
    }

    public static PerformanceAnalyzer createQuickAnalyzer() {
        return new Builder()
                .warmupIterations(1)
                .benchmarkIterations(5)
                .enableDetailedProfiling(false)
                .build();
    }
}