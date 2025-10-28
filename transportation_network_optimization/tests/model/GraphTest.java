package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Graph class
 * Tests graph construction, operations, and MST-specific functionality
 */
class GraphTest {

    private Graph simpleGraph;
    private Graph disconnectedGraph;
    private Graph singleVertexGraph;
    private Graph emptyGraph;

    @BeforeEach
    void setUp() {
        simpleGraph = createSimpleConnectedGraph();
        disconnectedGraph = createDisconnectedGraph();
        singleVertexGraph = createSingleVertexGraph();
        emptyGraph = Graph.createEmptyGraph();
    }

    @Test
    @DisplayName("Should create graph with correct vertex and edge counts")
    void testGraphCreation() {
        // Arrange & Act
        Graph graph = new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("A", "C", 3.0)
                .build();

        // Assert
        assertEquals(3, graph.getVerticesCount(), "Should have 3 vertices");
        assertEquals(3, graph.getEdgesCount(), "Should have 3 edges");
        assertTrue(graph.isValid(), "Graph should be valid");
    }

    @Test
    @DisplayName("Should create graph from edges list using builder")
    void testGraphCreationFromEdges() {
        // Arrange & Act - Use Builder pattern instead of direct Edge constructor
        Graph graph = new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .build();

        // Assert
        assertEquals(3, graph.getVerticesCount(), "Should infer 3 vertices from edges");
        assertEquals(2, graph.getEdgesCount(), "Should have 2 edges");
    }

    @Test
    @DisplayName("Should detect connected graph correctly")
    void testConnectedGraph() {
        // Assert
        assertTrue(simpleGraph.isConnected(), "Simple graph should be connected");
        assertFalse(disconnectedGraph.isConnected(), "Disconnected graph should not be connected");
        assertTrue(singleVertexGraph.isConnected(), "Single vertex graph should be connected");
        assertTrue(emptyGraph.isConnected(), "Empty graph should be connected");
    }

    @Test
    @DisplayName("Should get adjacent edges correctly")
    void testGetAdjacentEdges() {
        // Act
        List<Graph.EdgeDTO> adjacentA = simpleGraph.getAdjacentEdges("A");
        List<Graph.EdgeDTO> adjacentB = simpleGraph.getAdjacentEdges("B");
        List<Graph.EdgeDTO> adjacentC = simpleGraph.getAdjacentEdges("C");

        // Assert
        assertEquals(2, adjacentA.size(), "Vertex A should have 2 adjacent edges");
        assertEquals(2, adjacentB.size(), "Vertex B should have 2 adjacent edges");
        assertEquals(2, adjacentC.size(), "Vertex C should have 2 adjacent edges");

        // Verify edge contents
        assertTrue(adjacentA.stream().anyMatch(e -> e.getTo().equals("B")), "Should contain A-B edge");
        assertTrue(adjacentA.stream().anyMatch(e -> e.getTo().equals("C")), "Should contain A-C edge");
    }

    @Test
    @DisplayName("Should get adjacent vertices correctly")
    void testGetAdjacentVertices() {
        // Act
        List<String> adjacentA = simpleGraph.getAdjacentVertices("A");
        List<String> adjacentB = simpleGraph.getAdjacentVertices("B");

        // Assert
        assertEquals(2, adjacentA.size(), "Vertex A should have 2 adjacent vertices");
        assertEquals(2, adjacentB.size(), "Vertex B should have 2 adjacent vertices");
        assertTrue(adjacentA.contains("B"), "Should contain vertex B");
        assertTrue(adjacentA.contains("C"), "Should contain vertex C");
    }

    @Test
    @DisplayName("Should return correct vertex information")
    void testGetVertex() {
        // Act
        Graph.VertexDTO vertexA = simpleGraph.getVertex("A");
        Graph.VertexDTO vertexD = simpleGraph.getVertex("D"); // Non-existent

        // Assert
        assertNotNull(vertexA, "Vertex A should exist");
        assertEquals("A", vertexA.getId(), "Should return correct vertex ID");
        assertEquals(2, vertexA.getDegree(), "Vertex A should have degree 2");
        assertNull(vertexD, "Non-existent vertex should return null");
    }

    @Test
    @DisplayName("Should return all vertices")
    void testGetVertices() {
        // Act
        List<Graph.VertexDTO> vertices = simpleGraph.getVertices();
        List<String> vertexIds = simpleGraph.getVertexIds();

        // Assert
        assertEquals(3, vertices.size(), "Should return 3 vertices");
        assertEquals(3, vertexIds.size(), "Should return 3 vertex IDs");
        assertTrue(vertexIds.containsAll(Arrays.asList("A", "B", "C")),
                "Should contain all vertex IDs");
    }

    @Test
    @DisplayName("Should check vertex existence")
    void testContainsVertex() {
        // Assert
        assertTrue(simpleGraph.containsVertex("A"), "Should contain vertex A");
        assertTrue(simpleGraph.containsVertex("B"), "Should contain vertex B");
        assertFalse(simpleGraph.containsVertex("D"), "Should not contain vertex D");
    }

    @Test
    @DisplayName("Should return all edges")
    void testGetEdges() {
        // Act
        List<Graph.EdgeDTO> edges = simpleGraph.getEdges();

        // Assert
        assertEquals(3, edges.size(), "Should return 3 edges");

        // Verify edge properties
        Graph.EdgeDTO abEdge = edges.stream()
                .filter(e -> e.getFrom().equals("A") && e.getTo().equals("B"))
                .findFirst()
                .orElse(null);

        assertNotNull(abEdge, "Should contain A-B edge");
        assertEquals(1.0, abEdge.getWeight(), 0.001, "A-B edge should have weight 1.0");
        assertFalse(abEdge.isInMST(), "Edge should not be in MST initially");
    }

    @Test
    @DisplayName("Should get specific edge")
    void testGetEdge() {
        // Act
        Graph.EdgeDTO abEdge = simpleGraph.getEdge("A", "B");
        Graph.EdgeDTO baEdge = simpleGraph.getEdge("B", "A"); // Reverse
        Graph.EdgeDTO nonExistent = simpleGraph.getEdge("A", "D");

        // Assert
        assertNotNull(abEdge, "Should find A-B edge");
        assertNotNull(baEdge, "Should find B-A edge (undirected)");
        assertEquals(abEdge.getWeight(), baEdge.getWeight(), 0.001, "Reverse edge should have same weight");
        assertNull(nonExistent, "Non-existent edge should return null");
    }

    @Test
    @DisplayName("Should check edge existence")
    void testContainsEdge() {
        // Assert
        assertTrue(simpleGraph.containsEdge("A", "B"), "Should contain A-B edge");
        assertTrue(simpleGraph.containsEdge("B", "A"), "Should contain B-A edge (undirected)");
        assertFalse(simpleGraph.containsEdge("A", "D"), "Should not contain non-existent edge");
    }

    @Test
    @DisplayName("Should determine correct graph type")
    void testGraphType() {
        // Arrange
        Graph sparseGraph = createSparseGraph();
        Graph denseGraph = createDenseGraph();

        // Assert
        assertEquals(Graph.GraphType.UNKNOWN, simpleGraph.getGraphType(),
                "Simple triangle graph should be UNKNOWN type");
        assertEquals(Graph.GraphType.SPARSE, sparseGraph.getGraphType(),
                "Linear graph should be SPARSE");
        assertEquals(Graph.GraphType.DENSE, denseGraph.getGraphType(),
                "Complete graph should be DENSE");
    }

    @Test
    @DisplayName("Should calculate correct density")
    void testDensity() {
        // Act
        double simpleDensity = simpleGraph.getDensity();
        double sparseDensity = createSparseGraph().getDensity();
        double denseDensity = createDenseGraph().getDensity();

        // Assert
        assertEquals(1.0, simpleDensity, 0.001, "Triangle graph should have density 1.0");
        assertTrue(sparseDensity < 0.3, "Sparse graph should have density < 0.3");
        assertTrue(denseDensity > 0.7, "Dense graph should have density > 0.7");
    }

    @Test
    @DisplayName("Should handle MST operations correctly")
    void testMSTOperations() {
        // Arrange
        Graph graph = createSimpleConnectedGraph();

        // Act - Test MST functionality
        List<Graph.EdgeDTO> mstEdges = graph.getMSTEdges();
        double mstCost = graph.getMSTTotalCost();

        // Assert - Initially no edges should be in MST
        assertEquals(0, mstEdges.size(), "Initially should have no edges in MST");
        assertEquals(0.0, mstCost, 0.001, "Initial MST cost should be 0");

        // Test reset (should not throw exceptions)
        graph.resetMST();
        List<Graph.EdgeDTO> resetMstEdges = graph.getMSTEdges();
        double resetMstCost = graph.getMSTTotalCost();

        assertEquals(0, resetMstEdges.size(), "Should have no edges in MST after reset");
        assertEquals(0.0, resetMstCost, 0.001, "MST cost should be 0 after reset");
    }

    @Test
    @DisplayName("Should provide matrix representations")
    void testMatrixRepresentations() {
        // Act
        boolean[][] adjMatrix = simpleGraph.getAdjacencyMatrix();
        int[][] weightMatrix = simpleGraph.getWeightMatrix();
        Map<String, Integer> vertexMapping = simpleGraph.getVertexIndexMapping();

        // Assert
        assertEquals(3, adjMatrix.length, "Adjacency matrix should be 3x3");
        assertEquals(3, weightMatrix.length, "Weight matrix should be 3x3");
        assertEquals(3, vertexMapping.size(), "Should have mapping for 3 vertices");

        // Verify symmetry (undirected graph)
        assertTrue(adjMatrix[0][1], "A-B should be connected");
        assertTrue(adjMatrix[1][0], "B-A should be connected");
        assertEquals(adjMatrix[0][1], adjMatrix[1][0], "Adjacency matrix should be symmetric");
    }

    @Test
    @DisplayName("Should create subgraph correctly")
    void testCreateSubgraph() {
        // Arrange
        Set<String> subgraphVertices = new HashSet<>(Arrays.asList("A", "B"));

        // Act
        Graph subgraph = simpleGraph.createSubgraph(subgraphVertices);

        // Assert
        assertEquals(2, subgraph.getVerticesCount(), "Subgraph should have 2 vertices");
        assertEquals(1, subgraph.getEdgesCount(), "Subgraph should have 1 edge (A-B)");
        assertTrue(subgraph.containsEdge("A", "B"), "Subgraph should contain A-B edge");
        assertFalse(subgraph.containsEdge("A", "C"), "Subgraph should not contain A-C edge");
    }

    @Test
    @DisplayName("Should find connected components")
    void testGetConnectedComponents() {
        // Act
        List<Graph> components = disconnectedGraph.getConnectedComponents();

        // Assert
        assertEquals(2, components.size(), "Should find 2 connected components");

        // Verify component sizes
        Graph comp1 = components.get(0);
        Graph comp2 = components.get(1);

        assertTrue((comp1.getVerticesCount() == 2 && comp2.getVerticesCount() == 2) ||
                        (comp1.getVerticesCount() == 1 && comp2.getVerticesCount() == 3),
                "Components should have correct vertex counts");
    }

    @Test
    @DisplayName("Should provide statistics")
    void testGetStatistics() {
        // Act
        Map<String, Object> stats = simpleGraph.getStatistics();

        // Assert
        assertNotNull(stats, "Statistics should not be null");
        assertEquals(3, stats.get("vertices"), "Should have 3 vertices in stats");
        assertEquals(3, stats.get("edges"), "Should have 3 edges in stats");
        assertTrue(stats.containsKey("density"), "Should contain density");
        assertTrue(stats.containsKey("averageDegree"), "Should contain average degree");
        assertTrue(stats.containsKey("connected"), "Should contain connected flag");
    }

    @Test
    @DisplayName("Should track operation count")
    void testOperationCount() {
        // Arrange
        simpleGraph.resetOperationCount();

        // Act - Perform some operations
        simpleGraph.getVertex("A");
        simpleGraph.getAdjacentEdges("B");
        simpleGraph.containsVertex("C");

        // Assert
        assertTrue(simpleGraph.getOperationCount() > 0, "Should track operations");

        // Test reset
        simpleGraph.resetOperationCount();
        assertEquals(0, simpleGraph.getOperationCount(), "Should reset operation count");
    }

    @Test
    @DisplayName("Should validate graph correctly")
    void testIsValid() {
        // Assert
        assertTrue(simpleGraph.isValid(), "Simple graph should be valid");
        assertTrue(emptyGraph.isValid(), "Empty graph should be valid");
        assertTrue(singleVertexGraph.isValid(), "Single vertex graph should be valid");
    }

    @Test
    @DisplayName("Should handle edge cases for single vertex graph")
    void testSingleVertexGraph() {
        // Assert
        assertEquals(1, singleVertexGraph.getVerticesCount(), "Should have 1 vertex");
        assertEquals(0, singleVertexGraph.getEdgesCount(), "Should have 0 edges");
        assertTrue(singleVertexGraph.isConnected(), "Single vertex should be connected");
        assertEquals(0.0, singleVertexGraph.getDensity(), 0.001, "Density should be 0");

        List<Graph.EdgeDTO> adjacent = singleVertexGraph.getAdjacentEdges("A");
        assertTrue(adjacent.isEmpty(), "Should have no adjacent edges");
    }

    @Test
    @DisplayName("Should handle empty graph")
    void testEmptyGraph() {
        // Assert
        assertEquals(0, emptyGraph.getVerticesCount(), "Should have 0 vertices");
        assertEquals(0, emptyGraph.getEdgesCount(), "Should have 0 edges");
        assertTrue(emptyGraph.isConnected(), "Empty graph should be connected");
        assertEquals(0.0, emptyGraph.getDensity(), 0.001, "Density should be 0");

        List<Graph.VertexDTO> vertices = emptyGraph.getVertices();
        assertTrue(vertices.isEmpty(), "Should have no vertices");
    }

    @Test
    @DisplayName("Should create graph from JSON-like data")
    void testFromJsonData() {
        // Arrange
        List<String> nodes = Arrays.asList("A", "B", "C");
        List<Map<String, Object>> edgesData = Arrays.asList(
                Map.of("from", "A", "to", "B", "weight", 1.0),
                Map.of("from", "B", "to", "C", "weight", 2.0)
        );

        // Act
        Graph graph = Graph.fromJsonData(nodes, edgesData);

        // Assert
        assertEquals(3, graph.getVerticesCount(), "Should have 3 vertices");
        assertEquals(2, graph.getEdgesCount(), "Should have 2 edges");
        assertTrue(graph.containsEdge("A", "B"), "Should contain A-B edge");
        assertTrue(graph.containsEdge("B", "C"), "Should contain B-C edge");
    }

    @Test
    @DisplayName("Should provide meaningful string representation")
    void testToString() {
        // Act
        String representation = simpleGraph.toString();

        // Assert
        assertNotNull(representation, "String representation should not be null");
        assertTrue(representation.contains("Graph"), "Should contain class name");
        assertTrue(representation.contains("vertices=3"), "Should contain vertex count");
        assertTrue(representation.contains("edges=3"), "Should contain edge count");
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "C"})
    @DisplayName("Should handle various vertex IDs")
    void testVariousVertexIds(String vertexId) {
        // Assert
        assertTrue(simpleGraph.containsVertex(vertexId),
                "Should contain vertex: " + vertexId);
        assertNotNull(simpleGraph.getVertex(vertexId),
                "Should get vertex: " + vertexId);
    }

    @Test
    @DisplayName("Should handle duplicate edge addition gracefully")
    void testDuplicateEdges() {
        // Arrange & Act
        Graph graph = new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("A", "B", 2.0) // Duplicate with different weight
                .build();

        // Assert - Graph should handle duplicates based on your implementation
        // This depends on whether your Graph allows multiple edges between same vertices
        assertTrue(graph.isValid(), "Graph should still be valid");
    }

    @Test
    @DisplayName("Should calculate degree statistics correctly")
    void testDegreeCalculations() {
        // Act
        Map<String, Object> stats = simpleGraph.getStatistics();
        double avgDegree = (Double) stats.get("averageDegree");
        int minDegree = (Integer) stats.get("minDegree");
        int maxDegree = (Integer) stats.get("maxDegree");

        // Assert - In a triangle, all vertices have degree 2
        assertEquals(2.0, avgDegree, 0.001, "All vertices should have degree 2 in triangle");
        assertEquals(2, minDegree, "Minimum degree should be 2");
        assertEquals(2, maxDegree, "Maximum degree should be 2");
    }

    // Helper methods to create test graphs

    private Graph createSimpleConnectedGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("A", "C", 3.0)
                .build();
    }

    private Graph createDisconnectedGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("C", "D", 2.0) // Separate component
                .build();
    }

    private Graph createSingleVertexGraph() {
        return new Graph.Builder()
                .addVertex("A")
                .build();
    }

    private Graph createSparseGraph() {
        // Linear graph: A-B-C-D
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("C", "D", 3.0)
                .build();
    }

    private Graph createDenseGraph() {
        // Complete graph K4
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("A", "C", 2.0)
                .addEdge("A", "D", 3.0)
                .addEdge("B", "C", 4.0)
                .addEdge("B", "D", 5.0)
                .addEdge("C", "D", 6.0)
                .build();
    }
}