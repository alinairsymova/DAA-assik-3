package analysis;

import algorithms.KruskalAlgorithm;
import algorithms.PrimAlgorithm;
import algorithms.MSTAlgorithm;
import model.Graph;
import model.MSTResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Professional Algorithm Comparator for MST Algorithms
 * Provides comprehensive comparison between Kruskal and Prim algorithms
 * with detailed analysis and recommendations
 */
public class AlgorithmComparator {
    private final int comparisonIterations;
    private final double significanceThreshold;
    private final boolean enableDetailedAnalysis;

    public AlgorithmComparator() {

        comparisonIterations = 0;
        significanceThreshold = 0;
        enableDetailedAnalysis = false;
    }

    /**
     * Comparison result with detailed metrics
     */
    public static class ComparisonResult {
        private final String algorithmA;
        private final String algorithmB;
        private final double performanceRatio;
        private final double timeDifferenceMs;
        private final double operationsDifference;
        private final double memoryDifferenceBytes;
        private final double statisticalSignificance;
        private final boolean isSignificant;
        private final String winner;
        private final Map<String, Object> detailedMetrics;
        private final List<String> recommendations;

        public ComparisonResult(String algorithmA, String algorithmB, double performanceRatio,
                                double timeDifferenceMs, double operationsDifference,
                                double memoryDifferenceBytes, double statisticalSignificance,
                                boolean isSignificant, String winner,
                                Map<String, Object> detailedMetrics, List<String> recommendations) {
            this.algorithmA = algorithmA;
            this.algorithmB = algorithmB;
            this.performanceRatio = performanceRatio;
            this.timeDifferenceMs = timeDifferenceMs;
            this.operationsDifference = operationsDifference;
            this.memoryDifferenceBytes = memoryDifferenceBytes;
            this.statisticalSignificance = statisticalSignificance;
            this.isSignificant = isSignificant;
            this.winner = winner;
            this.detailedMetrics = detailedMetrics;
            this.recommendations = recommendations;
        }

        // Getters
        public String getAlgorithmA() { return algorithmA; }
        public String getAlgorithmB() { return algorithmB; }
        public double getPerformanceRatio() { return performanceRatio; }
        public double getTimeDifferenceMs() { return timeDifferenceMs; }
        public double getOperationsDifference() { return operationsDifference; }
        public double getMemoryDifferenceBytes() { return memoryDifferenceBytes; }
        public double getStatisticalSignificance() { return statisticalSignificance; }
        public boolean isSignificant() { return isSignificant; }
        public String getWinner() { return winner; }
        public Map<String, Object> getDetailedMetrics() { return detailedMetrics; }
        public List<String> getRecommendations() { return recommendations; }

        @Override
        public String toString() {
            return String.format(
                    "ComparisonResult{%s vs %s: winner=%s, ratio=%.2f, significant=%s}",
                    algorithmA, algorithmB, winner, performanceRatio, isSignificant
            );
        }
    }

    /**
     * Algorithm performance profile
     */
    public static class AlgorithmProfile {
        private final String algorithmName;
        private final double averageTimeMs;
        private final double averageOperations;
        private final double averageMemoryBytes;
        private final double consistencyScore;
        private final String performanceCategory;
        private final Map<String, Double> efficiencyMetrics;

        public AlgorithmProfile(String algorithmName, double averageTimeMs, double averageOperations,
                                double averageMemoryBytes, double consistencyScore,
                                String performanceCategory, Map<String, Double> efficiencyMetrics) {
            this.algorithmName = algorithmName;
            this.averageTimeMs = averageTimeMs;
            this.averageOperations = averageOperations;
            this.averageMemoryBytes = averageMemoryBytes;
            this.consistencyScore = consistencyScore;
            this.performanceCategory = performanceCategory;
            this.efficiencyMetrics = efficiencyMetrics;
        }

        // Getters
        public String getAlgorithmName() { return algorithmName; }
        public double getAverageTimeMs() { return averageTimeMs; }
        public double getAverageOperations() { return averageOperations; }
        public double getAverageMemoryBytes() { return averageMemoryBytes; }
        public double getConsistencyScore() { return consistencyScore; }
        public String getPerformanceCategory() { return performanceCategory; }
        public Map<String, Double> getEfficiencyMetrics() { return efficiencyMetrics; }

        @Override
        public String toString() {
            return String.format(
                    "AlgorithmProfile{%s: time=%.2fms, ops=%.0f, memory=%.0f bytes}",
                    algorithmName, averageTimeMs, averageOperations, averageMemoryBytes
            );
        }
    }

    /**
     * Graph characteristics for context-aware comparison
     */
    public static class GraphCharacteristics {
        private final int vertices;
        private final int edges;
        private final double density;
        private final String graphType;
        private final boolean isConnected;
        private final double averageDegree;

        public GraphCharacteristics(int vertices, int edges, double density,
                                    String graphType, boolean isConnected, double averageDegree) {
            this.vertices = vertices;
            this.edges = edges;
            this.density = density;
            this.graphType = graphType;
            this.isConnected = isConnected;
            this.averageDegree = averageDegree;
        }

        // Getters
        public int getVertices() { return vertices; }
        public int getEdges() { return edges; }
        public double getDensity() { return density; }
        public String getGraphType() { return graphType; }
        public boolean isConnected() { return isConnected; }
        public double getAverageDegree() { return averageDegree; }

        @Override
        public String toString() {
            return String.format(
                    "GraphCharacteristics{vertices=%d, edges=%d, density=%.3f, type=%s}",
                    vertices, edges, density, graphType
            );
        }
    }

    /**
     * Builder pattern for AlgorithmComparator configuration
     */
    public static class Builder {
        private int comparisonIterations = 5;
        private double significanceThreshold = 0.05;
        private boolean enableDetailedAnalysis = true;

        public Builder comparisonIterations(int iterations) {
            this.comparisonIterations = iterations;
            return this;
        }

        public Builder significanceThreshold(double threshold) {
            this.significanceThreshold = threshold;
            return this;
        }

        public Builder enableDetailedAnalysis(boolean enable) {
            this.enableDetailedAnalysis = enable;
            return this;
        }

        public AlgorithmComparator build() {
            return new AlgorithmComparator(this);
        }
    }

    /**
     * Private constructor
     */
    public AlgorithmComparator(Builder builder) {
        this.comparisonIterations = builder.comparisonIterations;
        this.significanceThreshold = builder.significanceThreshold;
        this.enableDetailedAnalysis = builder.enableDetailedAnalysis;
    }

    /**
     * Main comparison method between Kruskal and Prim algorithms
     */
    public ComparisonResult compareAlgorithms(Graph graph) {
        return compareAlgorithms(graph,
                KruskalAlgorithm.createDefault(),
                PrimAlgorithm.createDefault()
        );
    }

    /**
     * Compare custom algorithm instances
     */
    public ComparisonResult compareAlgorithms(Graph graph, MSTAlgorithm algorithm1, MSTAlgorithm algorithm2) {
        validateInput(graph, algorithm1, algorithm2);

        // Perform multiple iterations for statistical significance
        List<MSTResult> results1 = runMultipleIterations(algorithm1, graph, comparisonIterations);
        List<MSTResult> results2 = runMultipleIterations(algorithm2, graph, comparisonIterations);

        // Create algorithm profiles
        AlgorithmProfile profile1 = createAlgorithmProfile(algorithm1.getAlgorithmName(), results1);
        AlgorithmProfile profile2 = createAlgorithmProfile(algorithm2.getAlgorithmName(), results2);

        // Perform statistical comparison
        return performDetailedComparison(profile1, profile2, graph);
    }

    /**
     * Compare algorithms across multiple graphs
     */
    public Map<String, Object> compareAlgorithmsOnGraphSet(List<Graph> graphs) {
        Map<String, Object> comparisonResults = new LinkedHashMap<>();
        List<ComparisonResult> individualComparisons = new ArrayList<>();

        KruskalAlgorithm kruskal = KruskalAlgorithm.createDefault();
        PrimAlgorithm prim = PrimAlgorithm.createDefault();

        for (Graph graph : graphs) {
            ComparisonResult result = compareAlgorithms(graph, kruskal, prim);
            individualComparisons.add(result);
        }

        // Aggregate results
        comparisonResults.put("totalGraphs", graphs.size());
        comparisonResults.put("individualComparisons", individualComparisons);
        comparisonResults.put("aggregateAnalysis", performAggregateAnalysis(individualComparisons));
        comparisonResults.put("overallRecommendation", generateOverallRecommendation(individualComparisons));

        return comparisonResults;
    }

    /**
     * Context-aware comparison based on graph characteristics
     */
    public ComparisonResult compareAlgorithmsWithContext(Graph graph) {
        GraphCharacteristics characteristics = analyzeGraphCharacteristics(graph);
        ComparisonResult basicComparison = compareAlgorithms(graph);

        // Enhance with context-aware analysis
        return enhanceWithContextAnalysis(basicComparison, characteristics);
    }

    /**
     * Get optimal algorithm recommendation for a graph
     */
    public String recommendOptimalAlgorithm(Graph graph) {
        ComparisonResult comparison = compareAlgorithmsWithContext(graph);
        return comparison.getWinner();
    }

    /**
     * Generate detailed comparison report
     */
    public Map<String, Object> generateComparisonReport(Graph graph) {
        ComparisonResult comparison = compareAlgorithmsWithContext(graph);
        GraphCharacteristics characteristics = analyzeGraphCharacteristics(graph);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("timestamp", new Date());
        report.put("graphCharacteristics", characteristics);
        report.put("comparisonResult", comparison);
        report.put("performanceSummary", generatePerformanceSummary(comparison));
        report.put("optimizationSuggestions", generateOptimizationSuggestions(comparison, characteristics));

        return report;
    }

    // Private implementation methods

    private void validateInput(Graph graph, MSTAlgorithm alg1, MSTAlgorithm alg2) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        if (alg1 == null || alg2 == null) {
            throw new IllegalArgumentException("Algorithms cannot be null");
        }
        if (alg1.getAlgorithmName().equals(alg2.getAlgorithmName())) {
            throw new IllegalArgumentException("Cannot compare algorithm with itself");
        }
    }

    private List<MSTResult> runMultipleIterations(MSTAlgorithm algorithm, Graph graph, int iterations) {
        List<MSTResult> results = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            // Reset algorithm state between iterations
            algorithm.reset();
            MSTResult result = algorithm.computeMST(graph);
            results.add(result);
        }

        return results;
    }

    private AlgorithmProfile createAlgorithmProfile(String algorithmName, List<MSTResult> results) {
        // Calculate average metrics
        double avgTime = results.stream()
                .mapToLong(r -> r.getPerformanceMetrics().getExecutionTimeMs())
                .average().orElse(0);

        double avgOperations = results.stream()
                .mapToLong(r -> r.getPerformanceMetrics().getOperationsCount())
                .average().orElse(0);

        double avgMemory = results.stream()
                .mapToLong(r -> r.getPerformanceMetrics().getMemoryUsageBytes())
                .average().orElse(0);

        // Calculate consistency (lower standard deviation = more consistent)
        double timeStdDev = calculateStandardDeviation(
                results.stream().map(r -> (double)r.getPerformanceMetrics().getExecutionTimeMs()).collect(Collectors.toList()),
                avgTime
        );
        double consistencyScore = Math.max(0, 1 - (timeStdDev / avgTime));

        // Performance category
        String performanceCategory = categorizePerformance(avgTime);

        // Efficiency metrics
        Map<String, Double> efficiencyMetrics = calculateEfficiencyMetrics(results);

        return new AlgorithmProfile(
                algorithmName, avgTime, avgOperations, avgMemory,
                consistencyScore, performanceCategory, efficiencyMetrics
        );
    }

    private ComparisonResult performDetailedComparison(AlgorithmProfile profile1, AlgorithmProfile profile2, Graph graph) {
        String algA = profile1.getAlgorithmName();
        String algB = profile2.getAlgorithmName();

        // Calculate performance ratios
        double timeRatio = profile2.getAverageTimeMs() / profile1.getAverageTimeMs();
        double operationsRatio = profile2.getAverageOperations() / profile1.getAverageOperations();
        double memoryRatio = profile2.getAverageMemoryBytes() / profile1.getAverageMemoryBytes();

        // Overall performance ratio (weighted)
        double overallRatio = calculateOverallPerformanceRatio(profile1, profile2);

        // Calculate differences
        double timeDiff = profile2.getAverageTimeMs() - profile1.getAverageTimeMs();
        double operationsDiff = profile2.getAverageOperations() - profile1.getAverageOperations();
        double memoryDiff = profile2.getAverageMemoryBytes() - profile1.getAverageMemoryBytes();

        // Statistical significance (simplified)
        double significance = calculateStatisticalSignificance(profile1, profile2);
        boolean isSignificant = significance < significanceThreshold;

        // Determine winner
        String winner = determineWinner(profile1, profile2, overallRatio, isSignificant);

        // Detailed metrics
        Map<String, Object> detailedMetrics = createDetailedMetrics(profile1, profile2);

        // Recommendations
        List<String> recommendations = generateRecommendations(profile1, profile2, graph, winner);

        return new ComparisonResult(
                algA, algB, overallRatio, timeDiff, operationsDiff, memoryDiff,
                significance, isSignificant, winner, detailedMetrics, recommendations
        );
    }

    private double calculateOverallPerformanceRatio(AlgorithmProfile profile1, AlgorithmProfile profile2) {
        // Weighted combination of time, operations, and memory
        double timeWeight = 0.6;    // Execution time is most important
        double operationsWeight = 0.3; // Operations count
        double memoryWeight = 0.1;  // Memory usage

        double timeRatio = profile2.getAverageTimeMs() / profile1.getAverageTimeMs();
        double operationsRatio = profile2.getAverageOperations() / profile1.getAverageOperations();
        double memoryRatio = profile2.getAverageMemoryBytes() / profile1.getAverageMemoryBytes();

        return (timeRatio * timeWeight) + (operationsRatio * operationsWeight) + (memoryRatio * memoryWeight);
    }

    private double calculateStatisticalSignificance(AlgorithmProfile profile1, AlgorithmProfile profile2) {
        // Simplified significance calculation based on performance difference and consistency
        double performanceDifference = Math.abs(profile1.getAverageTimeMs() - profile2.getAverageTimeMs());
        double averagePerformance = (profile1.getAverageTimeMs() + profile2.getAverageTimeMs()) / 2;

        if (averagePerformance == 0) return 1.0;

        double relativeDifference = performanceDifference / averagePerformance;
        double consistencyFactor = (profile1.getConsistencyScore() + profile2.getConsistencyScore()) / 2;

        // Higher relative difference and consistency = lower p-value (more significant)
        return Math.max(0.001, Math.exp(-relativeDifference * consistencyFactor * 10));
    }

    private String determineWinner(AlgorithmProfile profile1, AlgorithmProfile profile2,
                                   double performanceRatio, boolean isSignificant) {
        if (!isSignificant) {
            return "Tie (No significant difference)";
        }

        if (performanceRatio > 1.1) {
            return profile1.getAlgorithmName();
        } else if (performanceRatio < 0.9) {
            return profile2.getAlgorithmName();
        } else {
            // Check secondary factors
            if (profile1.getAverageMemoryBytes() < profile2.getAverageMemoryBytes()) {
                return profile1.getAlgorithmName() + " (Better memory usage)";
            } else {
                return profile2.getAlgorithmName() + " (Better memory usage)";
            }
        }
    }

    private Map<String, Object> createDetailedMetrics(AlgorithmProfile profile1, AlgorithmProfile profile2) {
        Map<String, Object> metrics = new LinkedHashMap<>();

        metrics.put("timeImprovement", calculateImprovementPercentage(
                profile1.getAverageTimeMs(), profile2.getAverageTimeMs()));
        metrics.put("operationsImprovement", calculateImprovementPercentage(
                profile1.getAverageOperations(), profile2.getAverageOperations()));
        metrics.put("memoryImprovement", calculateImprovementPercentage(
                profile1.getAverageMemoryBytes(), profile2.getAverageMemoryBytes()));
        metrics.put("consistencyComparison", compareConsistency(profile1, profile2));
        metrics.put("efficiencyComparison", compareEfficiency(profile1, profile2));

        return metrics;
    }

    private List<String> generateRecommendations(AlgorithmProfile profile1, AlgorithmProfile profile2,
                                                 Graph graph, String winner) {
        List<String> recommendations = new ArrayList<>();
        GraphCharacteristics characteristics = analyzeGraphCharacteristics(graph);

        // Basic recommendation
        if (!winner.contains("Tie")) {
            recommendations.add("Primary recommendation: Use " + winner + " for this graph type");
        } else {
            recommendations.add("Both algorithms perform similarly. Choose based on other constraints.");
        }

        // Graph-specific recommendations
        if (characteristics.getDensity() < 0.3) {
            recommendations.add("Graph is sparse - Kruskal typically performs better on sparse graphs");
        } else if (characteristics.getDensity() > 0.7) {
            recommendations.add("Graph is dense - Prim typically performs better on dense graphs");
        }

        // Performance-based recommendations
        if (profile1.getConsistencyScore() > 0.9 && profile2.getConsistencyScore() > 0.9) {
            recommendations.add("Both algorithms show excellent consistency");
        }

        // Memory considerations
        if (profile1.getAverageMemoryBytes() < profile2.getAverageMemoryBytes() * 0.8) {
            recommendations.add(profile1.getAlgorithmName() + " uses significantly less memory");
        } else if (profile2.getAverageMemoryBytes() < profile1.getAverageMemoryBytes() * 0.8) {
            recommendations.add(profile2.getAlgorithmName() + " uses significantly less memory");
        }

        return recommendations;
    }

    private GraphCharacteristics analyzeGraphCharacteristics(Graph graph) {
        int vertices = graph.getVerticesCount();
        int edges = graph.getEdgesCount();
        double density = graph.getDensity();
        double averageDegree = (2.0 * edges) / vertices;

        String graphType;
        if (density < 0.3) graphType = "SPARSE";
        else if (density > 0.7) graphType = "DENSE";
        else graphType = "MODERATE";

        boolean isConnected = graph.isConnected();

        return new GraphCharacteristics(vertices, edges, density, graphType, isConnected, averageDegree);
    }

    private ComparisonResult enhanceWithContextAnalysis(ComparisonResult basicResult, GraphCharacteristics characteristics) {
        // Add context-aware insights to the basic comparison
        List<String> enhancedRecommendations = new ArrayList<>(basicResult.getRecommendations());

        // Add context-specific recommendations
        if (characteristics.getDensity() < 0.3) {
            enhancedRecommendations.add("Context: Sparse graphs favor Kruskal's algorithm due to sorting efficiency");
        } else if (characteristics.getDensity() > 0.7) {
            enhancedRecommendations.add("Context: Dense graphs favor Prim's algorithm due to better cache performance");
        }

        if (characteristics.getVertices() > 1000) {
            enhancedRecommendations.add("Context: Large graphs may benefit from memory-optimized configurations");
        }

        // Create enhanced result
        return new ComparisonResult(
                basicResult.getAlgorithmA(),
                basicResult.getAlgorithmB(),
                basicResult.getPerformanceRatio(),
                basicResult.getTimeDifferenceMs(),
                basicResult.getOperationsDifference(),
                basicResult.getMemoryDifferenceBytes(),
                basicResult.getStatisticalSignificance(),
                basicResult.isSignificant(),
                basicResult.getWinner(),
                basicResult.getDetailedMetrics(),
                enhancedRecommendations
        );
    }

    // Utility methods

    private double calculateStandardDeviation(List<Double> values, double mean) {
        if (values.size() < 2) return 0;
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    private String categorizePerformance(double avgTimeMs) {
        if (avgTimeMs < 10) return "EXCELLENT";
        if (avgTimeMs < 100) return "GOOD";
        if (avgTimeMs < 1000) return "MODERATE";
        return "SLOW";
    }

    private Map<String, Double> calculateEfficiencyMetrics(List<MSTResult> results) {
        Map<String, Double> efficiency = new HashMap<>();

        double avgOpsPerMs = results.stream()
                .mapToDouble(r -> (double) r.getPerformanceMetrics().getOperationsCount() /
                        r.getPerformanceMetrics().getExecutionTimeMs())
                .average().orElse(0);

        double avgMemoryEfficiency = results.stream()
                .mapToDouble(r -> (double) r.getPerformanceMetrics().getOperationsCount() /
                        r.getPerformanceMetrics().getMemoryUsageBytes())
                .average().orElse(0);

        efficiency.put("operationsPerMillisecond", avgOpsPerMs);
        efficiency.put("memoryEfficiency", avgMemoryEfficiency);

        return efficiency;
    }

    private double calculateImprovementPercentage(double value1, double value2) {
        if (value1 == 0) return 0;
        return ((value1 - value2) / value1) * 100;
    }

    private String compareConsistency(AlgorithmProfile profile1, AlgorithmProfile profile2) {
        double diff = profile1.getConsistencyScore() - profile2.getConsistencyScore();
        if (Math.abs(diff) < 0.1) return "Similar consistency";
        return diff > 0 ?
                profile1.getAlgorithmName() + " is more consistent" :
                profile2.getAlgorithmName() + " is more consistent";
    }

    private String compareEfficiency(AlgorithmProfile profile1, AlgorithmProfile profile2) {
        double eff1 = profile1.getEfficiencyMetrics().get("operationsPerMillisecond");
        double eff2 = profile2.getEfficiencyMetrics().get("operationsPerMillisecond");

        if (Math.abs(eff1 - eff2) < 0.1) return "Similar efficiency";
        return eff1 > eff2 ?
                profile1.getAlgorithmName() + " is more efficient" :
                profile2.getAlgorithmName() + " is more efficient";
    }

    private Map<String, Object> performAggregateAnalysis(List<ComparisonResult> comparisons) {
        Map<String, Object> aggregate = new LinkedHashMap<>();

        long kruskalWins = comparisons.stream()
                .filter(c -> c.getWinner().contains("Kruskal"))
                .count();
        long primWins = comparisons.stream()
                .filter(c -> c.getWinner().contains("Prim"))
                .count();
        long ties = comparisons.stream()
                .filter(c -> c.getWinner().contains("Tie"))
                .count();

        aggregate.put("kruskalWins", kruskalWins);
        aggregate.put("primWins", primWins);
        aggregate.put("ties", ties);
        aggregate.put("totalComparisons", comparisons.size());
        aggregate.put("kruskalWinRate", (double) kruskalWins / comparisons.size());
        aggregate.put("primWinRate", (double) primWins / comparisons.size());

        return aggregate;
    }

    private String generateOverallRecommendation(List<ComparisonResult> comparisons) {
        Map<String, Object> aggregate = performAggregateAnalysis(comparisons);
        double kruskalWinRate = (double) aggregate.get("kruskalWinRate");
        double primWinRate = (double) aggregate.get("primWinRate");

        if (kruskalWinRate > primWinRate + 0.1) {
            return "Overall recommendation: Kruskal algorithm";
        } else if (primWinRate > kruskalWinRate + 0.1) {
            return "Overall recommendation: Prim algorithm";
        } else {
            return "Overall: Both algorithms perform similarly across different graphs";
        }
    }

    private Map<String, Object> generatePerformanceSummary(ComparisonResult comparison) {
        Map<String, Object> summary = new LinkedHashMap<>();

        summary.put("winner", comparison.getWinner());
        summary.put("performanceAdvantage", String.format("%.1f%%",
                Math.abs(comparison.getPerformanceRatio() - 1) * 100));
        summary.put("significanceLevel", comparison.getStatisticalSignificance());
        summary.put("isStatisticallySignificant", comparison.isSignificant());

        return summary;
    }

    private List<String> generateOptimizationSuggestions(ComparisonResult comparison, GraphCharacteristics characteristics) {
        List<String> suggestions = new ArrayList<>();

        if (comparison.getTimeDifferenceMs() > 100) {
            suggestions.add("Consider optimizing the slower algorithm for better performance");
        }

        if (characteristics.getDensity() < 0.3) {
            suggestions.add("For sparse graphs, try Kruskal with optimized sorting");
        }

        if (characteristics.getDensity() > 0.7) {
            suggestions.add("For dense graphs, try Prim with array-based priority queue");
        }

        if (comparison.getMemoryDifferenceBytes() > 1000000) {
            suggestions.add("Memory usage differs significantly - consider memory-optimized configurations");
        }

        return suggestions;
    }

    /**
     * Custom exception for comparison errors
     */
    public static class ComparisonException extends RuntimeException {
        public ComparisonException(String message) {
            super(message);
        }

        public ComparisonException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Static factory methods
    public static AlgorithmComparator createDefault() {
        return new Builder().build();
    }

    public static AlgorithmComparator createDetailedComparator() {
        return new Builder()
                .comparisonIterations(10)
                .enableDetailedAnalysis(true)
                .build();
    }

    public static AlgorithmComparator createQuickComparator() {
        return new Builder()
                .comparisonIterations(3)
                .enableDetailedAnalysis(false)
                .build();
    }
}