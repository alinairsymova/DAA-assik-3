package algorithms;

import model.Edge;
import model.Graph;
import model.MSTResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for KruskalAlgorithm
 * Tests functionality, edge cases, and performance characteristics
 */
class KruskalAlgorithmTest {

    private KruskalAlgorithm kruskal;
    private Graph testGraph;

    @BeforeEach
    void setUp() {
        kruskal = KruskalAlgorithm.createDefault();
    }

    @Test
    @DisplayName("Should compute MST for simple connected graph")
    void testComputeMST_SimpleGraph() {
        // Arrange
        testGraph = createSimpleTestGraph();

        // Act
        MSTResult result = kruskal.computeMST(testGraph);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("Kruskal", result.getAlgorithmName(), "Algorithm name should be Kruskal");
        assertTrue(result.isValidMST(), "Result should be a valid MST");
        assertEquals(3, result.getTotalCost(), 0.001, "MST total cost should be 3");
        assertEquals(2, result.getMstEdges().size(), "MST should have V-1 edges");

        // Verify MST edges
        List<Edge> mstEdges = result.getMstEdges();
        assertTrue(containsEdge(mstEdges, "A", "B", 1.0), "MST should contain edge A-B");
        assertTrue(containsEdge(mstEdges, "B", "C", 2.0), "MST should contain edge B-C");
    }

    @Test
    @DisplayName("Should handle graph with single vertex")
    void testComputeMST_SingleVertex() {
        // Arrange
        testGraph = createSingleVertexGraph();

        // Act
        MSTResult result = kruskal.computeMST(testGraph);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isValidMST(), "Single vertex graph should have valid MST");
        assertEquals(0, result.getTotalCost(), 0.001, "MST cost should be 0 for single vertex");
        assertEquals(0, result.getMstEdges().size(), "MST should have 0 edges for single vertex");
    }

    @Test
    @DisplayName("Should throw exception for null graph")
    void testComputeMST_NullGraph() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            kruskal.computeMST(null);
        }, "Should throw IllegalArgumentException for null graph");
    }

    @Test
    @DisplayName("Should produce same MST cost as Prim's algorithm for same graph")
    void testComputeMST_ConsistentWithPrim() {
        // Arrange
        testGraph = createMediumTestGraph();
        KruskalAlgorithm kruskal = KruskalAlgorithm.createDefault();
        PrimAlgorithm prim = PrimAlgorithm.createDefault();

        // Act
        MSTResult kruskalResult = kruskal.computeMST(testGraph);
        MSTResult primResult = prim.computeMST(testGraph);

        // Assert
        assertTrue(kruskalResult.isValidMST(), "Kruskal result should be valid MST");
        assertTrue(primResult.isValidMST(), "Prim result should be valid MST");
        assertEquals(kruskalResult.getTotalCost(), primResult.getTotalCost(), 0.001,
                "Both algorithms should produce same MST cost");
    }

    @Test
    @DisplayName("Should count operations correctly")
    void testOperationCounting() {
        // Arrange
        testGraph = createSimpleTestGraph();

        // Act
        MSTResult result = kruskal.computeMST(testGraph);

        // Assert
        Map<String, Object> metrics = kruskal.getPerformanceMetrics();
        assertTrue((Integer) metrics.get("operationsCount") > 0, "Should count operations");
        assertTrue((Integer) metrics.get("comparisonsCount") > 0, "Should count comparisons");
        assertTrue((Integer) metrics.get("unionOperations") > 0, "Should count union operations");
        assertTrue((Integer) metrics.get("findOperations") > 0, "Should count find operations");
    }

    @Test
    @DisplayName("Should reset operation counters")
    void testResetCounters() {
        // Arrange
        testGraph = createSimpleTestGraph();
        kruskal.computeMST(testGraph); // Perform some operations

        // Act
        kruskal.reset();
        Map<String, Object> metrics = kruskal.getPerformanceMetrics();

        // Assert
        assertEquals(0, metrics.get("operationsCount"), "Operations count should be reset to 0");
        assertEquals(0, metrics.get("comparisonsCount"), "Comparisons count should be reset to 0");
        assertEquals(0, metrics.get("unionOperations"), "Union operations should be reset to 0");
        assertEquals(0, metrics.get("findOperations"), "Find operations should be reset to 0");
    }

    @Test
    @DisplayName("Should analyze graph suitability correctly")
    void testAnalyzeSuitability() {
        // Arrange
        Graph sparseGraph = createSparseGraph();
        Graph denseGraph = createDenseGraph();

        // Act
        Map<String, Object> sparseAnalysis = kruskal.analyzeSuitability(sparseGraph);
        Map<String, Object> denseAnalysis = kruskal.analyzeSuitability(denseGraph);

        // Assert
        assertTrue((Boolean) sparseAnalysis.get("suitableForKruskal"),
                "Kruskal should be suitable for sparse graphs");
        assertFalse((Boolean) denseAnalysis.get("suitableForKruskal"),
                "Kruskal should not be suitable for dense graphs");
    }

    @Test
    @DisplayName("Should handle graph with duplicate edges")
    void testComputeMST_DuplicateEdges() {
        // Arrange
        testGraph = createGraphWithDuplicateEdges();

        // Act
        MSTResult result = kruskal.computeMST(testGraph);

        // Assert
        assertNotNull(result, "Should handle graphs with duplicate edges");
        assertTrue(result.isValidMST(), "Should produce valid MST despite duplicate edges");
    }

    @Test
    @DisplayName("Should work with different union-find strategies")
    void testDifferentUnionFindStrategies() {
        // Arrange
        Graph graph = createMediumTestGraph();
        KruskalAlgorithm mapBased = KruskalAlgorithm.createOptimizedForSparseGraphs();
        KruskalAlgorithm arrayBased = KruskalAlgorithm.createOptimizedForDenseGraphs();

        // Act
        MSTResult mapResult = mapBased.computeMST(graph);
        MSTResult arrayResult = arrayBased.computeMST(graph);

        // Assert
        assertTrue(mapResult.isValidMST(), "Map-based should produce valid MST");
        assertTrue(arrayResult.isValidMST(), "Array-based should produce valid MST");
        assertEquals(mapResult.getTotalCost(), arrayResult.getTotalCost(), 0.001,
                "Different strategies should produce same MST cost");
    }

    @Test
    @DisplayName("Should handle graph with zero-weight edges")
    void testComputeMST_ZeroWeightEdges() {
        // Arrange
        testGraph = createGraphWithZeroWeightEdges();

        // Act
        MSTResult result = kruskal.computeMST(testGraph);

        // Assert
        assertNotNull(result, "Should handle zero-weight edges");
        assertTrue(result.isValidMST(), "Should produce valid MST with zero-weight edges");
    }

    @Test
    @DisplayName("Should return correct algorithm parameters")
    void testGetAlgorithmParameters() {
        // Act
        Map<String, Object> params = kruskal.getAlgorithmParameters();

        // Assert
        assertNotNull(params, "Algorithm parameters should not be null");
        assertTrue(params.containsKey("unionFindStrategy"), "Should include union-find strategy");
        assertTrue(params.containsKey("sortingStrategy"), "Should include sorting strategy");
        assertTrue(params.containsKey("enablePathCompression"), "Should include path compression setting");
        assertTrue(params.containsKey("enableUnionByRank"), "Should include union by rank setting");
    }

    @Test
    @DisplayName("Should return correct time and space complexity")
    void testComplexityAnalysis() {
        // Act & Assert
        assertEquals("O(E log E)", kruskal.getTimeComplexity(),
                "Should return correct time complexity");
        assertEquals("O(V + E)", kruskal.getSpaceComplexity(),
                "Should return correct space complexity");
    }

    @Test
    @DisplayName("Should process multiple graphs in batch")
    void testComputeMSTBatch() {
        // Arrange
        List<Graph> graphs = List.of(
                createSimpleTestGraph(),
                createMediumTestGraph(),
                createSparseGraph()
        );

        // Act
        List<MSTResult> results = kruskal.computeMSTBatch(graphs);

        // Assert
        assertEquals(3, results.size(), "Should process all graphs in batch");
        for (MSTResult result : results) {
            assertTrue(result.isValidMST(), "All batch results should be valid MSTs");
        }
    }

    // Helper methods to create test graphs

    private Graph createSimpleTestGraph() {
        // Graph: A--1--B--2--C
        //         \         /
        //          \3      /4
        //           \     /
        //            D---E
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("A", "D", 3.0)
                .addEdge("D", "E", 1.0)
                .addEdge("C", "E", 4.0)
                .addEdge("B", "D", 2.0)
                .build();
    }

    private Graph createSingleVertexGraph() {
        return new Graph.Builder()
                .addVertex("A")
                .build();
    }

    private Graph createMediumTestGraph() {
        // More complex graph for testing
        return new Graph.Builder()
                .addEdge("A", "B", 4.0)
                .addEdge("A", "C", 2.0)
                .addEdge("B", "C", 1.0)
                .addEdge("B", "D", 5.0)
                .addEdge("C", "D", 8.0)
                .addEdge("C", "E", 10.0)
                .addEdge("D", "E", 2.0)
                .addEdge("D", "F", 6.0)
                .addEdge("E", "F", 3.0)
                .build();
    }

    private Graph createSparseGraph() {
        // Sparse graph (low density)
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("C", "D", 1.0)
                .addEdge("E", "F", 1.0)
                .addEdge("G", "H", 1.0)
                .build();
    }

    private Graph createDenseGraph() {
        // Dense graph (high density) - complete graph with 4 vertices
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("A", "C", 2.0)
                .addEdge("A", "D", 3.0)
                .addEdge("B", "C", 4.0)
                .addEdge("B", "D", 5.0)
                .addEdge("C", "D", 6.0)
                .build();
    }

    private Graph createGraphWithDuplicateEdges() {
        // Graph with duplicate edges between same vertices
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("A", "B", 2.0) // Duplicate with different weight
                .addEdge("B", "C", 3.0)
                .addEdge("A", "C", 4.0)
                .build();
    }

    private Graph createGraphWithZeroWeightEdges() {
        // Graph containing zero-weight edges
        return new Graph.Builder()
                .addEdge("A", "B", 0.0)
                .addEdge("B", "C", 1.0)
                .addEdge("A", "C", 2.0)
                .addEdge("C", "D", 0.0)
                .build();
    }

    private boolean containsEdge(List<Edge> edges, String from, String to, double weight) {
        return edges.stream().anyMatch(edge ->
                edge.getFrom().equals(from) && edge.getTo().equals(to) &&
                        Math.abs(edge.getWeight() - weight) < 0.001 ||
                        edge.getFrom().equals(to) && edge.getTo().equals(from) &&
                                Math.abs(edge.getWeight() - weight) < 0.001
        );
    }
}