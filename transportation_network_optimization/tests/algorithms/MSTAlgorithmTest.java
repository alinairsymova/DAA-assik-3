package algorithms;

import model.Graph;
import model.MSTResult;
import model.Edge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MSTAlgorithm interface
 * Tests default methods and interface contract compliance
 */
class MSTAlgorithmTest {

    // Mock implementation for testing default methods
    private static class TestMSTAlgorithm implements MSTAlgorithm {
        private final String name;
        private int computeCallCount = 0;

        public TestMSTAlgorithm(String name) {
            this.name = name;
        }

        @Override
        public MSTResult computeMST(Graph graph) {
            computeCallCount++;
            // Return a mock result for testing
            return new MSTResult.Builder(name, graph, List.of())
                    .build();
        }

        @Override
        public String getAlgorithmName() {
            return name;
        }

        @Override
        public String getTimeComplexity() {
            return "O(E log V)";
        }

        @Override
        public String getSpaceComplexity() {
            return "O(V + E)";
        }

        @Override
        public Map<String, Object> analyzeSuitability(Graph graph) {
            return Map.of("suitable", true, "reason", "Test algorithm");
        }

        @Override
        public Map<String, Object> getPerformanceMetrics() {
            return Map.of("computeCalls", computeCallCount);
        }

        @Override
        public Map<String, Object> getAlgorithmParameters() {
            return Map.of("testParam", "testValue");
        }

        @Override
        public void reset() {
            computeCallCount = 0;
        }
    }

    @Test
    @DisplayName("Should return correct algorithm name")
    void testGetAlgorithmName() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");

        // Act & Assert
        assertEquals("TestAlgorithm", algorithm.getAlgorithmName(),
                "Should return correct algorithm name");
    }

    @Test
    @DisplayName("Should return time and space complexity")
    void testGetComplexity() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");

        // Act & Assert
        assertEquals("O(E log V)", algorithm.getTimeComplexity(),
                "Should return time complexity");
        assertEquals("O(V + E)", algorithm.getSpaceComplexity(),
                "Should return space complexity");
    }

    @Test
    @DisplayName("Should support valid connected graph")
    void testSupportsGraph_ValidGraph() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph connectedGraph = createConnectedGraph();

        // Act & Assert
        assertTrue(algorithm.supportsGraph(connectedGraph),
                "Should support valid connected graph");
    }

    @Test
    @DisplayName("Should not support null graph")
    void testSupportsGraph_NullGraph() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");

        // Act & Assert
        assertFalse(algorithm.supportsGraph(null),
                "Should not support null graph");
    }

    @Test
    @DisplayName("Should not support empty graph")
    void testSupportsGraph_EmptyGraph() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph emptyGraph = createEmptyGraph();

        // Act & Assert
        assertFalse(algorithm.supportsGraph(emptyGraph),
                "Should not support empty graph");
    }

    @Test
    @DisplayName("Should provide default algorithm description")
    void testGetDescription() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");

        // Act & Assert
        assertEquals("Minimum Spanning Tree Algorithm", algorithm.getDescription(),
                "Should provide default description");
    }

    @Test
    @DisplayName("Should return default optimization preference")
    void testGetOptimizedFor() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");

        // Act & Assert
        assertEquals("GENERAL", algorithm.getOptimizedFor(),
                "Should return default optimization preference");
    }

    @Test
    @DisplayName("Should validate correct MST result")
    void testIsValidMST_CorrectResult() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph graph = createSimpleGraph();
        MSTResult validResult = createValidMSTResult(graph);

        // Act & Assert
        assertTrue(algorithm.isValidMST(graph, validResult),
                "Should validate correct MST result");
    }

    @Test
    @DisplayName("Should invalidate MST with wrong edge count")
    void testIsValidMST_WrongEdgeCount() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph graph = createSimpleGraph(); // 3 vertices, should have 2 edges
        MSTResult invalidResult = createInvalidMSTResult(graph); // Wrong edge count

        // Act & Assert
        assertFalse(algorithm.isValidMST(graph, invalidResult),
                "Should invalidate MST with wrong edge count");
    }

    @Test
    @DisplayName("Should invalidate null graph or result")
    void testIsValidMST_NullInput() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph graph = createSimpleGraph();
        MSTResult result = createValidMSTResult(graph);

        // Act & Assert
        assertFalse(algorithm.isValidMST(null, result),
                "Should invalidate null graph");
        assertFalse(algorithm.isValidMST(graph, null),
                "Should invalidate null result");
        assertFalse(algorithm.isValidMST(null, null),
                "Should invalidate both null inputs");
    }

    @Test
    @DisplayName("Should return performance metrics")
    void testGetPerformanceMetrics() {
        // Arrange
        TestMSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph graph = createSimpleGraph();

        // Act
        algorithm.computeMST(graph);
        Map<String, Object> metrics = algorithm.getPerformanceMetrics();

        // Assert
        assertNotNull(metrics, "Performance metrics should not be null");
        assertTrue(metrics.containsKey("computeCalls"),
                "Should contain compute calls metric");
        assertEquals(1, metrics.get("computeCalls"),
                "Should track compute call count");
    }

    @Test
    @DisplayName("Should return algorithm parameters")
    void testGetAlgorithmParameters() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");

        // Act
        Map<String, Object> params = algorithm.getAlgorithmParameters();

        // Assert
        assertNotNull(params, "Algorithm parameters should not be null");
        assertTrue(params.containsKey("testParam"),
                "Should contain algorithm parameters");
    }

    @Test
    @DisplayName("Should analyze graph suitability")
    void testAnalyzeSuitability() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph graph = createSimpleGraph();

        // Act
        Map<String, Object> analysis = algorithm.analyzeSuitability(graph);

        // Assert
        assertNotNull(analysis, "Suitability analysis should not be null");
        assertTrue(analysis.containsKey("suitable"),
                "Should contain suitability assessment");
        assertTrue(analysis.containsKey("reason"),
                "Should contain reasoning");
    }

    @Test
    @DisplayName("Should reset algorithm state")
    void testReset() {
        // Arrange
        TestMSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph graph = createSimpleGraph();
        algorithm.computeMST(graph); // Perform some operations

        // Act
        algorithm.reset();
        Map<String, Object> metrics = algorithm.getPerformanceMetrics();

        // Assert
        assertEquals(0, metrics.get("computeCalls"),
                "Reset should clear operation counters");
    }

    @Test
    @DisplayName("Should compute MST without throwing exceptions")
    void testComputeMST_NoExceptions() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph graph = createSimpleGraph();

        // Act & Assert
        assertDoesNotThrow(() -> {
            MSTResult result = algorithm.computeMST(graph);
            assertNotNull(result, "MST result should not be null");
        }, "Should compute MST without throwing exceptions");
    }

    @Test
    @DisplayName("Should handle single vertex graph")
    void testSupportsGraph_SingleVertex() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph singleVertexGraph = createSingleVertexGraph();

        // Act & Assert
        assertTrue(algorithm.supportsGraph(singleVertexGraph),
                "Should support single vertex graph");
    }

    @Test
    @DisplayName("Should validate MST for single vertex graph")
    void testIsValidMST_SingleVertex() {
        // Arrange
        MSTAlgorithm algorithm = new TestMSTAlgorithm("TestAlgorithm");
        Graph singleVertexGraph = createSingleVertexGraph();
        MSTResult singleVertexResult = createSingleVertexMSTResult(singleVertexGraph);

        // Act & Assert
        assertTrue(algorithm.isValidMST(singleVertexGraph, singleVertexResult),
                "Should validate MST for single vertex graph (0 edges expected)");
    }

    // Helper methods to create test graphs and results

    private Graph createConnectedGraph() {
        return new Graph(
                List.of(
                        new Edge("A", "B", 1.0),
                        new Edge("B", "C", 2.0),
                        new Edge("A", "C", 3.0)
                )
        );
    }

    private Graph createSimpleGraph() {
        return new Graph(
                List.of(
                        new Edge("A", "B", 1.0),
                        new Edge("B", "C", 2.0),
                        new Edge("A", "C", 3.0)
                )
        );
    }

    private Graph createEmptyGraph() {
        return new Graph(List.of());
    }

    private Graph createSingleVertexGraph() {
        return new Graph(List.of());
    }

    private MSTResult createValidMSTResult(Graph graph) {
        // Create a valid MST result with V-1 edges
        List<Edge> mstEdges = List.of(
                new Edge("A", "B", 1.0),
                new Edge("B", "C", 2.0)
        );
        return new MSTResult.Builder("Test", graph, mstEdges)
                .build();
    }

    private MSTResult createInvalidMSTResult(Graph graph) {
        // Create an invalid MST result with wrong number of edges
        List<Edge> mstEdges = List.of(
                new Edge("A", "B", 1.0)
                // Missing second edge for 3-vertex graph
        );
        return new MSTResult.Builder("Test", graph, mstEdges)
                .build();
    }

    private MSTResult createSingleVertexMSTResult(Graph graph) {
        // Create MST result for single vertex graph (0 edges)
        return new MSTResult.Builder("Test", graph, List.of())
                .build();
    }
}