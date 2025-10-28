package algorithms;

import model.Graph;
import model.MSTResult;

import java.util.Map;

/**
 * Interface for Minimum Spanning Tree algorithms
 * Provides a common contract for all MST algorithm implementations
 */
public interface MSTAlgorithm {

    /**
     * Computes the Minimum Spanning Tree for the given graph
     *
     * @param graph the input graph
     * @return MSTResult containing the MST edges, total cost, and performance metrics
     * @throws IllegalArgumentException if graph is null or invalid
     */
    MSTResult computeMST(Graph graph);

    /**
     * Returns the name of the algorithm
     *
     * @return algorithm name (e.g., "Prim", "Kruskal")
     */
    String getAlgorithmName();

    /**
     * Returns the time complexity of the algorithm in Big O notation
     *
     * @return time complexity string (e.g., "O(E log V)")
     */
    String getTimeComplexity();

    /**
     * Returns the space complexity of the algorithm in Big O notation
     *
     * @return space complexity string (e.g., "O(V + E)")
     */
    String getSpaceComplexity();

    /**
     * Analyzes whether this algorithm is suitable for the given graph
     * based on graph characteristics (density, size, etc.)
     *
     * @param graph the graph to analyze
     * @return analysis results including suitability score and recommendations
     */
    Map<String, Object> analyzeSuitability(Graph graph);

    /**
     * Returns detailed performance metrics from the last computation
     *
     * @return map containing performance data (execution time, operations count, etc.)
     */
    Map<String, Object> getPerformanceMetrics();

    /**
     * Returns algorithm-specific parameters and configuration
     *
     * @return map containing algorithm configuration
     */
    Map<String, Object> getAlgorithmParameters();

    /**
     * Resets internal counters and state for a fresh computation
     */
    void reset();

    /**
     * Checks if the algorithm supports the given graph type
     *
     * @param graph the graph to check
     * @return true if the algorithm can handle this graph
     */
    default boolean supportsGraph(Graph graph) {
        if (graph == null) return false;
        return graph.getVerticesCount() > 0 && graph.isConnected();
    }

    /**
     * Provides a description of the algorithm
     *
     * @return algorithm description
     */
    default String getDescription() {
        return "Minimum Spanning Tree Algorithm";
    }

    /**
     * Returns the type of graph this algorithm is optimized for
     *
     * @return graph type preference ("SPARSE", "DENSE", "GENERAL")
     */
    default String getOptimizedFor() {
        return "GENERAL";
    }

    /**
     * Validates if the result is a valid MST for the given graph
     *
     * @param graph the original graph
     * @param result the computed MST result
     * @return true if the result is a valid MST
     */
    default boolean isValidMST(Graph graph, MSTResult result) {
        if (graph == null || result == null) return false;

        int expectedEdges = graph.getVerticesCount() - 1;
        int actualEdges = result.getMstEdges().size();

        return actualEdges == expectedEdges && result.isValidMST();
    }

}