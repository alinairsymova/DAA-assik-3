package algorithms;

import model.Graph;
import model.MSTResult;
import model.Edge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PrimAlgorithm
 * Tests various configurations, edge cases, and performance characteristics
 */
class PrimAlgorithmTest {

    private PrimAlgorithm primAlgorithm;

    @BeforeEach
    void setUp() {
        primAlgorithm = PrimAlgorithm.createDefault();
    }

    @Test
    @DisplayName("Should compute MST for simple connected graph")
    void testComputeMST_SimpleGraph() {
        // Arrange
        Graph graph = createSimpleConnectedGraph();

        // Act
        MSTResult result = primAlgorithm.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null");
        assertEquals("Prim", result.getAlgorithmName(), "Should use Prim algorithm");
        assertEquals(2, result.getMstEdges().size(), "MST should have V-1 edges for 3 vertices");
        assertEquals(3.0, result.getTotalCost(), 0.001, "MST total cost should be correct");
        assertTrue(result.getPerformanceMetrics().getExecutionTimeMs() >= 0,
                "Should have non-negative execution time");
    }

    @Test
    @DisplayName("Should compute MST for graph with multiple equal weight edges")
    void testComputeMST_EqualWeightEdges() {
        // Arrange
        Graph graph = createGraphWithEqualWeights();

        // Act
        MSTResult result = primAlgorithm.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null");
        assertEquals(3, result.getMstEdges().size(), "MST should have V-1 edges for 4 vertices");
        assertEquals(3.0, result.getTotalCost(), 0.001, "MST total cost should be sum of minimum edges");
    }

    @Test
    @DisplayName("Should handle single vertex graph")
    void testComputeMST_SingleVertex() {
        // Arrange
        Graph graph = createSingleVertexGraph();

        // Act
        MSTResult result = primAlgorithm.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null");
        assertTrue(result.getMstEdges().isEmpty(), "MST should have no edges for single vertex");
        assertEquals(0.0, result.getTotalCost(), 0.001, "MST total cost should be zero");
    }

    @Test
    @DisplayName("Should throw exception for null graph")
    void testComputeMST_NullGraph() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> primAlgorithm.computeMST(null),
                "Should throw exception for null graph");
    }

    @Test
    @DisplayName("Should throw exception for empty graph")
    void testComputeMST_EmptyGraph() {
        // Arrange
        Graph graph = createEmptyGraph();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> primAlgorithm.computeMST(graph),
                "Should throw exception for empty graph");
    }

    @Test
    @DisplayName("Should compute MST for dense graph")
    void testComputeMST_DenseGraph() {
        // Arrange
        Graph graph = createDenseGraph();
        PrimAlgorithm denseOptimized = PrimAlgorithm.createOptimizedForDenseGraphs();

        // Act
        MSTResult result = denseOptimized.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null");
        assertEquals(4, result.getMstEdges().size(), "MST should have V-1 edges for 5 vertices");
        assertTrue(result.getTotalCost() > 0, "MST should have positive cost");
    }

    @Test
    @DisplayName("Should compute MST for sparse graph")
    void testComputeMST_SparseGraph() {
        // Arrange
        Graph graph = createSparseGraph();
        PrimAlgorithm sparseOptimized = PrimAlgorithm.createOptimizedForSparseGraphs();

        // Act
        MSTResult result = sparseOptimized.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null");
        assertEquals(4, result.getMstEdges().size(), "MST should have V-1 edges for 5 vertices");
    }

    @ParameterizedTest
    @EnumSource(PrimAlgorithm.PriorityQueueStrategy.class)
    @DisplayName("Should work with all priority queue strategies")
    void testComputeMST_AllQueueStrategies(PrimAlgorithm.PriorityQueueStrategy strategy) {
        // Arrange
        Graph graph = createSimpleConnectedGraph();
        PrimAlgorithm algorithm = new PrimAlgorithm.Builder()
                .queueStrategy(strategy)
                .build();

        // Act
        MSTResult result = algorithm.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null with strategy: " + strategy);
        assertEquals(2, result.getMstEdges().size(),
                "Should compute correct MST with strategy: " + strategy);
    }

    @Test
    @DisplayName("Should track performance metrics")
    void testPerformanceMetricsTracking() {
        // Arrange
        Graph graph = createSimpleConnectedGraph();

        // Act
        MSTResult result = primAlgorithm.computeMST(graph);
        Map<String, Object> metrics = primAlgorithm.getPerformanceMetrics();

        // Assert
        assertNotNull(metrics, "Performance metrics should not be null");
        assertTrue(metrics.containsKey("operationsCount"), "Should track operations count");
        assertTrue(metrics.containsKey("comparisonsCount"), "Should track comparisons count");
        assertTrue(metrics.containsKey("queueOperations"), "Should track queue operations");

        // Verify metrics are populated
        assertTrue((Integer) metrics.get("operationsCount") > 0, "Should have positive operations count");
    }

    @Test
    @DisplayName("Should return algorithm parameters")
    void testGetAlgorithmParameters() {
        // Act
        Map<String, Object> params = primAlgorithm.getAlgorithmParameters();

        // Assert
        assertNotNull(params, "Algorithm parameters should not be null");
        assertTrue(params.containsKey("queueStrategy"), "Should contain queue strategy");
        assertTrue(params.containsKey("optimizeDenseGraphs"), "Should contain optimization flag");
    }

    @Test
    @DisplayName("Should analyze graph suitability")
    void testAnalyzeSuitability() {
        // Arrange
        Graph graph = createSimpleConnectedGraph();

        // Act
        Map<String, Object> analysis = primAlgorithm.analyzeSuitability(graph);

        // Assert
        assertNotNull(analysis, "Suitability analysis should not be null");
        assertTrue(analysis.containsKey("suitableForPrim"), "Should contain suitability assessment");
        assertTrue(analysis.containsKey("vertexCount"), "Should contain vertex count");
        assertTrue(analysis.containsKey("edgeCount"), "Should contain edge count");
        assertTrue(analysis.containsKey("density"), "Should contain density");
        assertTrue(analysis.containsKey("recommendedQueueStrategy"), "Should contain queue recommendation");
    }

    @Test
    @DisplayName("Should reset algorithm state")
    void testReset() {
        // Arrange
        Graph graph = createSimpleConnectedGraph();
        primAlgorithm.computeMST(graph); // Perform some operations

        // Act
        primAlgorithm.reset();
        Map<String, Object> metrics = primAlgorithm.getPerformanceMetrics();

        // Assert
        assertEquals(0, metrics.get("operationsCount"), "Reset should clear operations count");
        assertEquals(0, metrics.get("comparisonsCount"), "Reset should clear comparisons count");
        assertEquals(0, metrics.get("queueOperations"), "Reset should clear queue operations");
    }

    @Test
    @DisplayName("Should return correct algorithm name")
    void testGetAlgorithmName() {
        // Act & Assert
        assertEquals("Prim", primAlgorithm.getAlgorithmName(),
                "Should return correct algorithm name");
    }

    @Test
    @DisplayName("Should return correct time complexity")
    void testGetTimeComplexity() {
        // Act & Assert
        assertNotNull(primAlgorithm.getTimeComplexity(), "Time complexity should not be null");
        assertTrue(primAlgorithm.getTimeComplexity().contains("O"),
                "Time complexity should be in Big O notation");
    }

    @Test
    @DisplayName("Should return correct space complexity")
    void testGetSpaceComplexity() {
        // Act & Assert
        assertEquals("O(V + E)", primAlgorithm.getSpaceComplexity(),
                "Should return correct space complexity");
    }

    @Test
    @DisplayName("Should handle graph with negative weights")
    void testComputeMST_NegativeWeights() {
        // Arrange
        Graph graph = createGraphWithNegativeWeights();

        // Act
        MSTResult result = primAlgorithm.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null for graph with negative weights");
        assertEquals(3, result.getMstEdges().size(), "MST should have V-1 edges for 4 vertices");
    }

    @Test
    @DisplayName("Should compute MST for star graph")
    void testComputeMST_StarGraph() {
        // Arrange
        Graph graph = createStarGraph();

        // Act
        MSTResult result = primAlgorithm.computeMST(graph);

        // Assert
        assertNotNull(result, "MST result should not be null");
        assertEquals(4, result.getMstEdges().size(), "MST should have V-1 edges for 5 vertices");

        // In a star graph, MST should include all edges from center to leaves
        long edgesFromCenter = result.getMstEdges().stream()
                .filter(edge -> edge.getFrom().equals("Center") || edge.getTo().equals("Center"))
                .count();
        assertEquals(4, edgesFromCenter, "MST should include all center-to-leaf edges");
    }

    @Test
    @DisplayName("Should handle large graph efficiently")
    void testComputeMST_LargeGraph() {
        // Arrange
        Graph graph = createLargeGraph(100); // 100 vertices
        PrimAlgorithm optimized = PrimAlgorithm.createOptimizedForLargeGraphs();

        // Act
        long startTime = System.nanoTime();
        MSTResult result = optimized.computeMST(graph);
        long endTime = System.nanoTime();

        // Assert
        assertNotNull(result, "MST result should not be null for large graph");
        assertEquals(99, result.getMstEdges().size(), "MST should have V-1 edges for 100 vertices");

        long durationMs = (endTime - startTime) / 1_000_000;
        assertTrue(durationMs < 1000, "Should compute MST for 100 vertices in reasonable time");
    }

    @Test
    @DisplayName("Should batch process multiple graphs")
    void testComputeMSTBatch() {
        // Arrange
        List<Graph> graphs = List.of(
                createSimpleConnectedGraph(),
                createDenseGraph(),
                createSparseGraph()
        );

        // Act
        List<MSTResult> results = primAlgorithm.computeMSTBatch(graphs);

        // Assert
        assertNotNull(results, "Batch results should not be null");
        assertEquals(3, results.size(), "Should process all graphs in batch");

        for (MSTResult result : results) {
            assertNotNull(result, "Each result in batch should not be null");
            assertTrue(result.getTotalCost() >= 0, "Each MST should have non-negative cost");
        }
    }

    @Test
    @DisplayName("Should optimize for sparse graphs")
    void testOptimizeForSparseGraphs() {
        // Arrange
        PrimAlgorithm algorithm = PrimAlgorithm.createOptimizedForSparseGraphs();

        // Act & Assert
        assertFalse(algorithm.isOptimizeDenseGraphs(),
                "Should not optimize for dense graphs when optimized for sparse");
        assertEquals(PrimAlgorithm.PriorityQueueStrategy.BINARY_HEAP,
                algorithm.getQueueStrategy(), "Should use binary heap for sparse graphs");
    }

    @Test
    @DisplayName("Should optimize for dense graphs")
    void testOptimizeForDenseGraphs() {
        // Arrange
        PrimAlgorithm algorithm = PrimAlgorithm.createOptimizedForDenseGraphs();

        // Act & Assert
        assertTrue(algorithm.isOptimizeDenseGraphs(),
                "Should optimize for dense graphs");
        assertEquals(PrimAlgorithm.PriorityQueueStrategy.ARRAY_BASED,
                algorithm.getQueueStrategy(), "Should use array-based queue for dense graphs");
    }

    @Test
    @DisplayName("Should provide meaningful string representation")
    void testToString() {
        // Act
        String representation = primAlgorithm.toString();

        // Assert
        assertNotNull(representation, "String representation should not be null");
        assertTrue(representation.contains("PrimAlgorithm"),
                "Should include class name in representation");
        assertTrue(representation.contains("queueStrategy"),
                "Should include queue strategy in representation");
    }

    // Helper methods to create test graphs

    private Graph createSimpleConnectedGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("A", "C", 3.0)
                .build();
    }

    private Graph createGraphWithEqualWeights() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 1.0)
                .addEdge("C", "D", 1.0)
                .addEdge("A", "D", 2.0)
                .addEdge("B", "D", 2.0)
                .build();
    }

    private Graph createSingleVertexGraph() {
        return new Graph.Builder()
                .addVertex("A")
                .build();
    }

    private Graph createEmptyGraph() {
        return Graph.createEmptyGraph();
    }

    private Graph createDenseGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 2.0)
                .addEdge("A", "C", 3.0)
                .addEdge("A", "D", 1.0)
                .addEdge("A", "E", 4.0)
                .addEdge("B", "C", 2.0)
                .addEdge("B", "D", 3.0)
                .addEdge("B", "E", 1.0)
                .addEdge("C", "D", 2.0)
                .addEdge("C", "E", 3.0)
                .addEdge("D", "E", 2.0)
                .build();
    }

    private Graph createSparseGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("C", "D", 3.0)
                .addEdge("D", "E", 1.0)
                .build();
    }

    private Graph createGraphWithNegativeWeights() {
        return new Graph.Builder()
                .addEdge("A", "B", -1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("C", "D", -2.0)
                .addEdge("A", "D", 3.0)
                .build();
    }

    private Graph createStarGraph() {
        return new Graph.Builder()
                .addEdge("Center", "Leaf1", 1.0)
                .addEdge("Center", "Leaf2", 2.0)
                .addEdge("Center", "Leaf3", 3.0)
                .addEdge("Center", "Leaf4", 1.0)
                .addEdge("Leaf1", "Leaf2", 5.0) // Extra edge that shouldn't be in MST
                .build();
    }

    private Graph createLargeGraph(int vertexCount) {
        Graph.Builder builder = new Graph.Builder();

        // Create a connected graph with vertexCount vertices
        for (int i = 0; i < vertexCount; i++) {
            builder.addVertex("V" + i);
        }

        // Create a tree structure (minimum edges for connectivity)
        for (int i = 1; i < vertexCount; i++) {
            builder.addEdge("V0", "V" + i, Math.random() * 10 + 1);
        }

        // Add some extra edges to make it more interesting
        for (int i = 0; i < vertexCount / 2; i++) {
            int from = (int) (Math.random() * vertexCount);
            int to = (int) (Math.random() * vertexCount);
            if (from != to) {
                builder.addEdge("V" + from, "V" + to, Math.random() * 10 + 1);
            }
        }

        return builder.build();
    }
}