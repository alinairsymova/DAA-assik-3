package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for MSTResult class
 * Tests result construction, validation, analysis, and comparison functionality
 */
class MSTResultTest {

    private Graph simpleGraph;
    private List<Edge> mstEdges;
    private MSTResult validResult;
    private MSTResult emptyResult;

    @BeforeEach
    void setUp() {
        simpleGraph = createSimpleGraph();
        mstEdges = createValidMSTEdges();
        validResult = createValidMSTResult();
        emptyResult = createEmptyMSTResult();
    }

    @Test
    @DisplayName("Should create MST result with valid data")
    void testMSTResultCreation() {
        // Arrange & Act
        MSTResult result = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .build();

        // Assert
        assertEquals("Prim", result.getAlgorithmName(), "Should have correct algorithm name");
        assertEquals(simpleGraph, result.getGraph(), "Should have correct graph");
        assertEquals(2, result.getMstEdges().size(), "Should have correct number of MST edges");
        assertEquals(3.0, result.getTotalCost(), 0.001, "Should calculate correct total cost");
        assertNotNull(result.getResultId(), "Should generate result ID");
        assertTrue(result.getTimestamp() > 0, "Should have valid timestamp");
    }

    @Test
    @DisplayName("Should create MST result with performance metrics")
    void testMSTResultWithPerformanceMetrics() {
        // Arrange
        MSTResult.PerformanceMetrics metrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(150)
                .operationsCount(1000)
                .memoryUsageBytes(1024 * 1024)
                .comparisonsCount(500)
                .unionOperations(200)
                .priorityQueueOperations(300)
                .build();

        // Act
        MSTResult result = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(metrics)
                .build();

        // Assert
        assertEquals(metrics, result.getPerformanceMetrics(), "Should have correct performance metrics");
        assertEquals(150, result.getPerformanceMetrics().getExecutionTimeMs(), "Should have correct execution time");
        assertEquals(1000, result.getPerformanceMetrics().getOperationsCount(), "Should have correct operations count");
        assertEquals(500, result.getPerformanceMetrics().getComparisonsCount(), "Should have correct comparisons count");
    }

    @Test
    @DisplayName("Should create MST result with algorithm parameters")
    void testMSTResultWithAlgorithmParameters() {
        // Arrange
        MSTResult.AlgorithmParameters params = new MSTResult.AlgorithmParameters.Builder()
                .useFibonacciHeap(true)
                .useUnionFind(false)
                .optimizeMemory(true)
                .initialCapacity(500)
                .dataStructureVariant("optimized")
                .build();

        // Act
        MSTResult result = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .parameters(params)
                .build();

        // Assert
        assertEquals(params, result.getParameters(), "Should have correct algorithm parameters");
        assertTrue(result.getParameters().isUseFibonacciHeap(), "Should use Fibonacci heap");
        assertFalse(result.getParameters().isUseUnionFind(), "Should not use union find");
        assertTrue(result.getParameters().isOptimizeMemory(), "Should optimize memory");
        assertEquals(500, result.getParameters().getInitialCapacity(), "Should have correct initial capacity");
    }

    @Test
    @DisplayName("Should create MST result with custom properties")
    void testMSTResultWithCustomProperties() {
        // Arrange
        List<Edge> criticalEdges = Collections.singletonList(mstEdges.get(0));
        MSTResult.MSTProperties properties = new MSTResult.MSTProperties.Builder()
                .vertexCount(3)
                .edgeCount(2)
                .density(1.0)
                .averageDegree(4.0 / 3.0)
                .diameter(2)
                .averageEdgeWeight(1.5)
                .maxEdgeWeight(2.0)
                .minEdgeWeight(1.0)
                .criticalEdges(criticalEdges)
                .bridgeEdges(mstEdges)
                .build();

        // Act
        MSTResult result = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .mstProperties(properties)
                .build();

        // Assert
        assertEquals(properties, result.getMstProperties(), "Should have correct MST properties");
        assertEquals(3, result.getMstProperties().getVertexCount(), "Should have correct vertex count");
        assertEquals(2, result.getMstProperties().getEdgeCount(), "Should have correct edge count");
        assertEquals(1.0, result.getMstProperties().getDensity(), 0.001, "Should have correct density");
        assertEquals(1.5, result.getMstProperties().getAverageEdgeWeight(), 0.001, "Should have correct average weight");
        assertEquals(1, result.getMstProperties().getCriticalEdges().size(), "Should have critical edges");
    }

    @Test
    @DisplayName("Should validate correct MST result")
    void testIsValidMST_ValidResult() {
        // Assert
        assertTrue(validResult.isValidMST(), "Valid MST result should pass validation");
    }

    @Test
    @DisplayName("Should invalidate MST with wrong edge count")
    void testIsValidMST_WrongEdgeCount() {
        // Arrange - Create invalid MST with wrong number of edges
        List<Edge> invalidEdges = Collections.singletonList(mstEdges.get(0)); // Only 1 edge for 3 vertices
        MSTResult invalidResult = new MSTResult.Builder("Prim", simpleGraph, invalidEdges)
                .build();

        // Assert
        assertFalse(invalidResult.isValidMST(), "MST with wrong edge count should fail validation");
    }

    @Test
    @DisplayName("Should validate single vertex graph")
    void testIsValidMST_SingleVertex() {
        // Arrange
        Graph singleVertexGraph = createSingleVertexGraph();
        List<Edge> emptyEdges = Collections.emptyList();
        MSTResult singleVertexResult = new MSTResult.Builder("Prim", singleVertexGraph, emptyEdges)
                .build();

        // Assert
        assertTrue(singleVertexResult.isValidMST(), "Single vertex graph should be valid");
    }

    @Test
    @DisplayName("Should invalidate disconnected MST")
    void testIsValidMST_Disconnected() {
        // Arrange - Create disconnected "MST"
        Graph disconnectedGraph = createDisconnectedGraph();
        List<Edge> disconnectedEdges = Arrays.asList(
                new Edge.Builder("A", "B").weight(1.0).build()
                // Missing edge to connect component with C
        );
        MSTResult disconnectedResult = new MSTResult.Builder("Prim", disconnectedGraph, disconnectedEdges)
                .build();

        // Assert
        assertFalse(disconnectedResult.isValidMST(), "Disconnected MST should fail validation");
    }

    @Test
    @DisplayName("Should check MST equivalence by cost")
    void testIsEquivalentTo() {
        // Arrange
        MSTResult result1 = createValidMSTResult();
        MSTResult result2 = createValidMSTResult(); // Same cost

        // Create result with different cost
        List<Edge> differentEdges = Arrays.asList(
                new Edge.Builder("A", "B").weight(2.0).build(),
                new Edge.Builder("B", "C").weight(3.0).build()
        );
        MSTResult differentResult = new MSTResult.Builder("Prim", simpleGraph, differentEdges)
                .build();

        // Assert
        assertTrue(result1.isEquivalentTo(result2), "Results with same cost should be equivalent");
        assertFalse(result1.isEquivalentTo(differentResult), "Results with different costs should not be equivalent");
        assertFalse(result1.isEquivalentTo(null), "Should not be equivalent to null");
    }

    @Test
    @DisplayName("Should calculate cost difference")
    void testGetCostDifference() {
        // Arrange
        MSTResult result1 = createValidMSTResult(); // Cost = 3.0
        List<Edge> differentEdges = Arrays.asList(
                new Edge.Builder("A", "B").weight(2.0).build(),
                new Edge.Builder("B", "C").weight(3.0).build()
        );
        MSTResult result2 = new MSTResult.Builder("Prim", simpleGraph, differentEdges)
                .build(); // Cost = 5.0

        // Act
        double difference = result1.getCostDifference(result2);

        // Assert
        assertEquals(2.0, difference, 0.001, "Should calculate correct cost difference");
    }

    @Test
    @DisplayName("Should calculate performance improvement")
    void testGetPerformanceImprovement() {
        // Arrange
        MSTResult.PerformanceMetrics fastMetrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(100)
                .operationsCount(500)
                .build();

        MSTResult.PerformanceMetrics slowMetrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(200)
                .operationsCount(500)
                .build();

        MSTResult fastResult = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(fastMetrics)
                .build();

        MSTResult slowResult = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(slowMetrics)
                .build();

        // Act
        double improvement = fastResult.getPerformanceImprovement(slowResult);

        // Assert
        assertEquals(50.0, improvement, 0.001, "Should calculate correct performance improvement");
    }

    @Test
    @DisplayName("Should provide detailed analysis")
    void testGetDetailedAnalysis() {
        // Act
        Map<String, Object> analysis = validResult.getDetailedAnalysis();

        // Assert
        assertNotNull(analysis, "Analysis should not be null");
        assertEquals("Prim", analysis.get("algorithm"), "Should contain algorithm name");
        assertEquals(3.0, (Double) analysis.get("totalCost"), 0.001, "Should contain total cost");
        assertTrue((Boolean) analysis.get("validMST"), "Should indicate valid MST");
        assertTrue(analysis.containsKey("executionTimeMs"), "Should contain execution time");
        assertTrue(analysis.containsKey("operationsCount"), "Should contain operations count");
        assertTrue(analysis.containsKey("efficiency"), "Should contain efficiency metric");
        assertTrue(analysis.containsKey("verticesInMST"), "Should contain vertex count");
        assertTrue(analysis.containsKey("edgesInMST"), "Should contain edge count");
    }

    @Test
    @DisplayName("Should get edges sorted by weight")
    void testGetEdgesSortedByWeight() {
        // Arrange
        List<Edge> unsortedEdges = Arrays.asList(
                new Edge.Builder("A", "B").weight(3.0).build(),
                new Edge.Builder("B", "C").weight(1.0).build(),
                new Edge.Builder("A", "C").weight(2.0).build()
        );
        MSTResult result = new MSTResult.Builder("Prim", simpleGraph, unsortedEdges)
                .build();

        // Act
        List<Edge> sortedEdges = result.getEdgesSortedByWeight();

        // Assert
        assertEquals(3, sortedEdges.size(), "Should return all edges");
        assertEquals(1.0, sortedEdges.get(0).getWeight(), 0.001, "First edge should be lightest");
        assertEquals(2.0, sortedEdges.get(1).getWeight(), 0.001, "Second edge should be middle weight");
        assertEquals(3.0, sortedEdges.get(2).getWeight(), 0.001, "Third edge should be heaviest");
    }

    @Test
    @DisplayName("Should get critical path edges")
    void testGetCriticalPathEdges() {
        // Arrange
        List<Edge> edges = Arrays.asList(
                new Edge.Builder("A", "B").weight(1.0).build(),
                new Edge.Builder("B", "C").weight(2.0).build(),
                new Edge.Builder("C", "D").weight(0.5).build(),
                new Edge.Builder("D", "E").weight(3.0).build()
        );
        Graph graph = createLargerGraph();
        MSTResult result = new MSTResult.Builder("Prim", graph, edges)
                .build();

        // Act
        List<Edge> criticalEdges = result.getCriticalPathEdges();

        // Assert
        assertEquals(3, criticalEdges.size(), "Should return top 3 lightest edges");
        assertEquals(0.5, criticalEdges.get(0).getWeight(), 0.001, "First critical edge should be lightest");
        assertEquals(1.0, criticalEdges.get(1).getWeight(), 0.001, "Second critical edge should be second lightest");
    }

    @Test
    @DisplayName("Should provide JSON string representation")
    void testToJsonString() {
        // Act
        String json = validResult.toJsonString();

        // Assert
        assertNotNull(json, "JSON string should not be null");
        assertTrue(json.contains("\"algorithm\":\"Prim\""), "Should contain algorithm in JSON");
        assertTrue(json.contains("\"totalCost\":3.00"), "Should contain total cost in JSON");
        assertTrue(json.contains("\"edges\":2"), "Should contain edge count in JSON");
        assertTrue(json.contains("\"valid\":true"), "Should contain validity in JSON");
        assertTrue(json.startsWith("{") && json.endsWith("}"), "Should be valid JSON object");
    }

    @Test
    @DisplayName("Should convert to map representation")
    void testToMap() {
        // Act
        Map<String, Object> map = validResult.toMap();

        // Assert
        assertNotNull(map, "Map should not be null");
        assertEquals("Prim", map.get("algorithmName"), "Should contain algorithm name");
        assertEquals(3.0, (Double) map.get("totalCost"), 0.001, "Should contain total cost");
        assertEquals(2, map.get("edgesCount"), "Should contain edges count");
        assertTrue((Boolean) map.get("isValid"), "Should contain validity flag");
        assertTrue(map.containsKey("performance"), "Should contain performance metrics");
        assertTrue(map.containsKey("parameters"), "Should contain algorithm parameters");
        assertTrue(map.containsKey("properties"), "Should contain MST properties");
        assertTrue(map.containsKey("edges"), "Should contain edges list");
    }

    @Test
    @DisplayName("Should provide meaningful string representation")
    void testToString() {
        // Act
        String representation = validResult.toString();

        // Assert
        assertNotNull(representation, "String representation should not be null");
        assertTrue(representation.contains("MSTResult"), "Should contain class name");
        assertTrue(representation.contains("algorithm=Prim"), "Should contain algorithm");
        assertTrue(representation.contains("cost=3.00"), "Should contain cost");
        assertTrue(representation.contains("edges=2"), "Should contain edge count");
        assertTrue(representation.contains("valid=true"), "Should contain validity");
    }

    @Test
    @DisplayName("Should throw exception for null algorithm name")
    void testBuilderNullAlgorithmName() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new MSTResult.Builder(null, simpleGraph, mstEdges),
                "Should throw exception for null algorithm name");

        assertThrows(IllegalArgumentException.class,
                () -> new MSTResult.Builder("", simpleGraph, mstEdges),
                "Should throw exception for empty algorithm name");
    }

    @Test
    @DisplayName("Should throw exception for null graph")
    void testBuilderNullGraph() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new MSTResult.Builder("Prim", null, mstEdges),
                "Should throw exception for null graph");
    }

    @Test
    @DisplayName("Should throw exception for null edges")
    void testBuilderNullEdges() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new MSTResult.Builder("Prim", simpleGraph, null),
                "Should throw exception for null edges");
    }

    @Test
    @DisplayName("Should compare algorithms and return faster one")
    void testCompareAlgorithms() {
        // Arrange
        MSTResult.PerformanceMetrics fastMetrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(100)
                .build();

        MSTResult.PerformanceMetrics slowMetrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(200)
                .build();

        MSTResult fastResult = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(fastMetrics)
                .build();

        MSTResult slowResult = new MSTResult.Builder("Kruskal", simpleGraph, mstEdges)
                .performanceMetrics(slowMetrics)
                .build();

        // Act
        MSTResult faster = MSTResult.compareAlgorithms(fastResult, slowResult);

        // Assert
        assertEquals(fastResult, faster, "Should return faster algorithm");
    }

    @Test
    @DisplayName("Should throw exception when comparing null algorithms")
    void testCompareAlgorithmsWithNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> MSTResult.compareAlgorithms(null, validResult),
                "Should throw exception for first null");

        assertThrows(IllegalArgumentException.class,
                () -> MSTResult.compareAlgorithms(validResult, null),
                "Should throw exception for second null");
    }

    @Test
    @DisplayName("Should create comparison report")
    void testCreateComparisonReport() {
        // Arrange
        MSTResult.PerformanceMetrics metrics1 = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(100)
                .operationsCount(500)
                .build();

        MSTResult.PerformanceMetrics metrics2 = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(200)
                .operationsCount(600)
                .build();

        MSTResult result1 = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(metrics1)
                .build();

        MSTResult result2 = new MSTResult.Builder("Kruskal", simpleGraph, mstEdges)
                .performanceMetrics(metrics2)
                .build();

        // Act
        Map<String, Object> comparison = MSTResult.createComparisonReport(result1, result2);

        // Assert
        assertNotNull(comparison, "Comparison report should not be null");
        assertTrue((Boolean) comparison.get("costEquivalent"), "Should indicate cost equivalence");
        assertEquals(0.0, (Double) comparison.get("costDifference"), 0.001, "Should have zero cost difference");
        assertEquals(50.0, (Double) comparison.get("performanceImprovement"), 0.001, "Should calculate improvement");
        assertEquals("Prim", comparison.get("fasterAlgorithm"), "Should identify faster algorithm");
        assertEquals("Prim", comparison.get("moreEfficientAlgorithm"), "Should identify more efficient algorithm");
    }

    @Test
    @DisplayName("Should handle empty MST result")
    void testEmptyMSTResult() {
        // Assert
        assertEquals(0, emptyResult.getMstEdges().size(), "Empty result should have no edges");
        assertEquals(0.0, emptyResult.getTotalCost(), 0.001, "Empty result should have zero cost");
        assertTrue(emptyResult.isValidMST(), "Empty result for single vertex should be valid");
    }

    @Test
    @DisplayName("Should calculate operations per millisecond correctly")
    void testOperationsPerMillisecond() {
        // Arrange
        MSTResult.PerformanceMetrics metrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(200)
                .operationsCount(1000)
                .build();

        MSTResult result = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(metrics)
                .build();

        // Assert
        assertEquals(5.0, result.getPerformanceMetrics().getOperationsPerMillisecond(), 0.001,
                "Should calculate correct operations per millisecond");
    }

    @Test
    @DisplayName("Should handle zero execution time for operations per millisecond")
    void testOperationsPerMillisecondZeroTime() {
        // Arrange
        MSTResult.PerformanceMetrics metrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(0)
                .operationsCount(1000)
                .build();

        MSTResult result = new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(metrics)
                .build();

        // Assert
        assertEquals(0.0, result.getPerformanceMetrics().getOperationsPerMillisecond(), 0.001,
                "Should return zero for zero execution time");
    }

    @Test
    @DisplayName("Should test PerformanceMetrics string representation")
    void testPerformanceMetricsToString() {
        // Arrange
        MSTResult.PerformanceMetrics metrics = new MSTResult.PerformanceMetrics.Builder()
                .executionTimeMs(150)
                .operationsCount(1000)
                .memoryUsageBytes(1024000)
                .build();

        // Act
        String representation = metrics.toString();

        // Assert
        assertNotNull(representation, "PerformanceMetrics string should not be null");
        assertTrue(representation.contains("time=150ms"), "Should contain execution time");
        assertTrue(representation.contains("operations=1000"), "Should contain operations count");
        assertTrue(representation.contains("memory=1024000"), "Should contain memory usage");
    }

    @Test
    @DisplayName("Should test AlgorithmParameters string representation")
    void testAlgorithmParametersToString() {
        // Arrange
        MSTResult.AlgorithmParameters params = new MSTResult.AlgorithmParameters.Builder()
                .useFibonacciHeap(true)
                .useUnionFind(false)
                .optimizeMemory(true)
                .initialCapacity(500)
                .dataStructureVariant("optimized")
                .build();

        // Act
        String representation = params.toString();

        // Assert
        assertNotNull(representation, "AlgorithmParameters string should not be null");
        assertTrue(representation.contains("fibHeap=true"), "Should contain Fibonacci heap setting");
        assertTrue(representation.contains("unionFind=false"), "Should contain union find setting");
        assertTrue(representation.contains("capacity=500"), "Should contain initial capacity");
    }

    @Test
    @DisplayName("Should test MSTProperties string representation")
    void testMSTPropertiesToString() {
        // Arrange
        MSTResult.MSTProperties properties = new MSTResult.MSTProperties.Builder()
                .vertexCount(5)
                .edgeCount(4)
                .density(0.8)
                .averageDegree(1.6)
                .diameter(3)
                .build();

        // Act
        String representation = properties.toString();

        // Assert
        assertNotNull(representation, "MSTProperties string should not be null");
        assertTrue(representation.contains("vertices=5"), "Should contain vertex count");
        assertTrue(representation.contains("edges=4"), "Should contain edge count");
        assertTrue(representation.contains("density=0.800"), "Should contain density");
    }

    // Helper methods to create test data

    private Graph createSimpleGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("A", "C", 3.0)
                .build();
    }

    private Graph createSingleVertexGraph() {
        return new Graph.Builder()
                .addVertex("A")
                .build();
    }

    private Graph createDisconnectedGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("C", "D", 2.0) // Separate component
                .build();
    }

    private Graph createLargerGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("C", "D", 3.0)
                .addEdge("D", "E", 4.0)
                .addEdge("A", "E", 5.0)
                .build();
    }

    private List<Edge> createValidMSTEdges() {
        return Arrays.asList(
                new Edge.Builder("A", "B").weight(1.0).build(),
                new Edge.Builder("B", "C").weight(2.0).build()
        );
    }

    private MSTResult createValidMSTResult() {
        return new MSTResult.Builder("Prim", simpleGraph, mstEdges)
                .performanceMetrics(new MSTResult.PerformanceMetrics.Builder()
                        .executionTimeMs(100)
                        .operationsCount(500)
                        .memoryUsageBytes(1024 * 1024)
                        .build())
                .build();
    }

    private MSTResult createEmptyMSTResult() {
        Graph singleVertexGraph = createSingleVertexGraph();
        List<Edge> emptyEdges = Collections.emptyList();
        return new MSTResult.Builder("Prim", singleVertexGraph, emptyEdges)
                .build();
    }
}