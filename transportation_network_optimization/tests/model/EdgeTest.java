package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Edge class
 * Tests edge construction, functionality, and MST-specific operations
 */
class EdgeTest {

    private Edge standardEdge;
    private Edge weightedEdge;
    private Edge criticalEdge;

    @BeforeEach
    void setUp() {
        standardEdge = new Edge.Builder("A", "B").build();
        weightedEdge = new Edge.Builder("X", "Y").weight(5.5).build();
        criticalEdge = new Edge.Builder("P", "Q")
                .weight(2.0)
                .type(Edge.EdgeType.CRITICAL)
                .label("Important connection")
                .build();
    }

    @Test
    @DisplayName("Should create edge with default values")
    void testEdgeCreationWithDefaults() {
        // Assert
        assertEquals("A", standardEdge.getFrom(), "Should have correct from vertex");
        assertEquals("B", standardEdge.getTo(), "Should have correct to vertex");
        assertEquals(1.0, standardEdge.getWeight(), 0.001, "Should have default weight 1.0");
        assertEquals(Edge.EdgeType.STANDARD, standardEdge.getType(), "Should have default type STANDARD");
        assertFalse(standardEdge.isInMST(), "Should not be in MST initially");
        assertFalse(standardEdge.isVisited(), "Should not be visited initially");
        assertEquals(0, standardEdge.getTraversalCount(), "Should have zero traversal count initially");
        assertEquals(Edge.EdgeStatus.ACTIVE, standardEdge.getStatus(), "Should have ACTIVE status initially");
    }

    @Test
    @DisplayName("Should create edge with custom properties")
    void testEdgeCreationWithCustomProperties() {
        // Assert
        assertEquals("X", weightedEdge.getFrom(), "Should have correct from vertex");
        assertEquals("Y", weightedEdge.getTo(), "Should have correct to vertex");
        assertEquals(5.5, weightedEdge.getWeight(), 0.001, "Should have custom weight");
        assertEquals(Edge.EdgeType.STANDARD, weightedEdge.getType(), "Should have STANDARD type");
    }

    @Test
    @DisplayName("Should create edge with all custom properties")
    void testEdgeCreationWithAllProperties() {
        // Assert
        assertEquals("P", criticalEdge.getFrom(), "Should have correct from vertex");
        assertEquals("Q", criticalEdge.getTo(), "Should have correct to vertex");
        assertEquals(2.0, criticalEdge.getWeight(), 0.001, "Should have correct weight");
        assertEquals(Edge.EdgeType.CRITICAL, criticalEdge.getType(), "Should have CRITICAL type");
        assertEquals("Important connection", criticalEdge.getLabel(), "Should have correct label");
    }

    @Test
    @DisplayName("Should generate canonical edge ID")
    void testEdgeIdGeneration() {
        // Arrange
        Edge edge1 = new Edge.Builder("A", "B").build();
        Edge edge2 = new Edge.Builder("B", "A").build();

        // Assert
        assertEquals("A-B", edge1.getId(), "Should generate canonical ID for A-B");
        assertEquals("A-B", edge2.getId(), "Should generate same canonical ID for B-A");
        assertTrue(edge1.getId().contains("-"), "ID should contain separator");
    }

    @Test
    @DisplayName("Should get other vertex correctly")
    void testGetOtherVertex() {
        // Act & Assert
        assertEquals("B", standardEdge.getOtherVertex("A"), "Should return B for vertex A");
        assertEquals("A", standardEdge.getOtherVertex("B"), "Should return A for vertex B");
    }

    @Test
    @DisplayName("Should throw exception for invalid vertex in getOtherVertex")
    void testGetOtherVertexWithInvalidVertex() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> standardEdge.getOtherVertex("C"),
                "Should throw exception for vertex not in edge");

        assertTrue(exception.getMessage().contains("C"), "Exception message should mention invalid vertex");
    }

    @Test
    @DisplayName("Should check vertex containment")
    void testContainsVertex() {
        // Assert
        assertTrue(standardEdge.containsVertex("A"), "Should contain vertex A");
        assertTrue(standardEdge.containsVertex("B"), "Should contain vertex B");
        assertFalse(standardEdge.containsVertex("C"), "Should not contain vertex C");
    }

    @Test
    @DisplayName("Should check if edge connects vertices")
    void testConnects() {
        // Assert
        assertTrue(standardEdge.connects("A", "B"), "Should connect A and B");
        assertTrue(standardEdge.connects("B", "A"), "Should connect B and A (undirected)");
        assertFalse(standardEdge.connects("A", "C"), "Should not connect A and C");
        assertFalse(standardEdge.connects("C", "D"), "Should not connect C and D");
    }

    @Test
    @DisplayName("Should mark edge as traversed")
    void testMarkTraversed() {
        // Act
        standardEdge.markTraversed();
        standardEdge.markTraversed();

        // Assert
        assertTrue(standardEdge.isVisited(), "Should be marked as visited");
        assertEquals(2, standardEdge.getTraversalCount(), "Should have traversal count 2");
    }

    @Test
    @DisplayName("Should reset traversal state")
    void testResetTraversal() {
        // Arrange
        standardEdge.markTraversed();
        standardEdge.markTraversed();

        // Act
        standardEdge.resetTraversal();

        // Assert
        assertFalse(standardEdge.isVisited(), "Should not be visited after reset");
        assertEquals(0, standardEdge.getTraversalCount(), "Should have zero traversal count after reset");
    }

    @Test
    @DisplayName("Should create reversed edge")
    void testCreateReversed() {
        // Act
        Edge reversed = standardEdge.createReversed();

        // Assert
        assertEquals("B", reversed.getFrom(), "Reversed edge should have B as from");
        assertEquals("A", reversed.getTo(), "Reversed edge should have A as to");
        assertEquals(standardEdge.getWeight(), reversed.getWeight(), 0.001, "Should have same weight");
        assertTrue(reversed.getLabel().contains("reversed"), "Label should indicate reversal");
    }

    @Test
    @DisplayName("Should validate edge correctly")
    void testIsValid() {
        // Assert
        assertTrue(standardEdge.isValid(), "Standard edge should be valid");
        assertTrue(weightedEdge.isValid(), "Weighted edge should be valid");
        assertTrue(criticalEdge.isValid(), "Critical edge should be valid");
    }

    @Test
    @DisplayName("Should detect self-loop")
    void testIsSelfLoop() {
        // Arrange
        Edge selfLoop = new Edge.Builder("A", "A").build();

        // Assert
        assertTrue(selfLoop.isSelfLoop(), "Should detect self-loop");
        assertFalse(standardEdge.isSelfLoop(), "Standard edge should not be self-loop");
    }

    @Test
    @DisplayName("Should compare edges by weight")
    void testCompareTo() {
        // Arrange
        Edge lightEdge = new Edge.Builder("A", "B").weight(1.0).build();
        Edge heavyEdge = new Edge.Builder("C", "D").weight(3.0).build();
        Edge sameWeight1 = new Edge.Builder("E", "F").weight(2.0).build();
        Edge sameWeight2 = new Edge.Builder("G", "H").weight(2.0).build();

        // Assert
        assertTrue(lightEdge.compareTo(heavyEdge) < 0, "Lighter edge should come before heavier");
        assertTrue(heavyEdge.compareTo(lightEdge) > 0, "Heavier edge should come after lighter");
        assertEquals(0, sameWeight1.compareTo(sameWeight2), "Same weight edges should be equal in comparison");
    }

    @Test
    @DisplayName("Should check equality based on vertices")
    void testEquals() {
        // Arrange
        Edge edge1 = new Edge.Builder("A", "B").weight(1.0).build();
        Edge edge2 = new Edge.Builder("B", "A").weight(2.0).build(); // Different weight, same vertices
        Edge edge3 = new Edge.Builder("A", "C").weight(1.0).build(); // Different vertices

        // Assert
        assertEquals(edge1, edge2, "Edges with same vertices should be equal regardless of weight");
        assertNotEquals(edge1, edge3, "Edges with different vertices should not be equal");
        assertEquals(edge1, edge1, "Edge should equal itself");
        assertNotEquals(edge1, null, "Edge should not equal null");
        assertNotEquals(edge1, "not an edge", "Edge should not equal different type");
    }

    @Test
    @DisplayName("Should generate consistent hash code")
    void testHashCode() {
        // Arrange
        Edge edge1 = new Edge.Builder("A", "B").weight(1.0).build();
        Edge edge2 = new Edge.Builder("B", "A").weight(2.0).build();

        // Assert
        assertEquals(edge1.hashCode(), edge2.hashCode(), "Edges with same vertices should have same hash code");
    }

    @Test
    @DisplayName("Should provide meaningful string representation")
    void testToString() {
        // Act
        String representation = standardEdge.toString();

        // Assert
        assertNotNull(representation, "String representation should not be null");
        assertTrue(representation.contains("Edge"), "Should contain class name");
        assertTrue(representation.contains("from='A'"), "Should contain from vertex");
        assertTrue(representation.contains("to='B'"), "Should contain to vertex");
        assertTrue(representation.contains("weight="), "Should contain weight");
    }

    @Test
    @DisplayName("Should provide detailed string representation")
    void testToDetailedString() {
        // Act
        String detailed = criticalEdge.toDetailedString();

        // Assert
        assertNotNull(detailed, "Detailed string should not be null");
        assertTrue(detailed.contains("id="), "Should contain ID");
        assertTrue(detailed.contains("traversals="), "Should contain traversal count");
        assertTrue(detailed.contains("label="), "Should contain label");
    }

    @Test
    @DisplayName("Should provide JSON string representation")
    void testToJsonString() {
        // Act
        String json = standardEdge.toJsonString();

        // Assert
        assertNotNull(json, "JSON string should not be null");
        assertTrue(json.contains("\"from\":\"A\""), "Should contain from in JSON");
        assertTrue(json.contains("\"to\":\"B\""), "Should contain to in JSON");
        assertTrue(json.contains("\"weight\":"), "Should contain weight in JSON");
        assertTrue(json.startsWith("{") && json.endsWith("}"), "Should be valid JSON object");
    }

    @Test
    @DisplayName("Should check if edge is available")
    void testIsAvailable() {
        // Arrange
        Edge activeEdge = new Edge.Builder("A", "B").build();
        Edge inactiveEdge = new Edge.Builder("C", "D").build();
        inactiveEdge.setStatus(Edge.EdgeStatus.INACTIVE);

        Edge selfLoop = new Edge.Builder("E", "E").build();

        // Assert
        assertTrue(activeEdge.isAvailable(), "Active edge should be available");
        assertFalse(inactiveEdge.isAvailable(), "Inactive edge should not be available");
        assertFalse(selfLoop.isAvailable(), "Self-loop should not be available");
    }

    @Test
    @DisplayName("Should check if edge is critical")
    void testIsCritical() {
        // Arrange
        Edge bridgeEdge = new Edge.Builder("A", "B").type(Edge.EdgeType.BRIDGE).build();
        Edge criticalEdge = new Edge.Builder("C", "D").type(Edge.EdgeType.CRITICAL).build();
        Edge standardEdge = new Edge.Builder("E", "F").type(Edge.EdgeType.STANDARD).build();

        // Assert
        assertTrue(bridgeEdge.isCritical(), "Bridge edge should be critical");
        assertTrue(criticalEdge.isCritical(), "Critical edge should be critical");
        assertFalse(standardEdge.isCritical(), "Standard edge should not be critical");
    }

    @Test
    @DisplayName("Should calculate normalized weight")
    void testGetNormalizedWeight() {
        // Arrange
        Edge edge = new Edge.Builder("A", "B").weight(3.0).build();

        // Act & Assert
        assertEquals(0.3, edge.getNormalizedWeight(10.0), 0.001, "Should calculate correct normalized weight");
        assertEquals(1.0, edge.getNormalizedWeight(2.0), 0.001, "Should cap at 1.0 for small max weight");
        assertEquals(0.0, edge.getNormalizedWeight(0.0), 0.001, "Should return 0 for zero max weight");
        assertEquals(0.0, edge.getNormalizedWeight(-5.0), 0.001, "Should return 0 for negative max weight");
    }

    @Test
    @DisplayName("Should create EdgeDTO")
    void testToDTO() {
        // Arrange
        standardEdge.setInMST(true);

        // Act
        Edge.EdgeDTO dto = standardEdge.toDTO();

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals("A", dto.getFrom(), "DTO should have correct from vertex");
        assertEquals("B", dto.getTo(), "DTO should have correct to vertex");
        assertEquals(1.0, dto.getWeight(), 0.001, "DTO should have correct weight");
        assertTrue(dto.isInMST(), "DTO should reflect MST status");
    }

    @ParameterizedTest
    @EnumSource(Edge.EdgeType.class)
    @DisplayName("Should work with all edge types")
    void testAllEdgeTypes(Edge.EdgeType type) {
        // Arrange & Act
        Edge edge = new Edge.Builder("A", "B").type(type).build();

        // Assert
        assertNotNull(edge, "Should create edge with type: " + type);
        assertEquals(type, edge.getType(), "Should have correct type: " + type);
        assertTrue(edge.isValid(), "Edge should be valid with type: " + type);
    }

    @ParameterizedTest
    @EnumSource(Edge.EdgeStatus.class)
    @DisplayName("Should work with all edge statuses")
    void testAllEdgeStatuses(Edge.EdgeStatus status) {
        // Arrange
        Edge edge = new Edge.Builder("A", "B").build();

        // Act
        edge.setStatus(status);

        // Assert
        assertEquals(status, edge.getStatus(), "Should have correct status: " + status);
    }

    @Test
    @DisplayName("Should handle user data")
    void testUserData() {
        // Arrange
        Object userData = new Object();
        Edge edge = new Edge.Builder("A", "B").userData(userData).build();

        // Act
        edge.setUserData("custom data");

        // Assert
        assertEquals("custom data", edge.getUserData(), "Should set and get user data");
    }

    @Test
    @DisplayName("Should handle label updates")
    void testLabelUpdates() {
        // Arrange
        Edge edge = new Edge.Builder("A", "B").label("initial").build();

        // Act
        edge.setLabel("updated");

        // Assert
        assertEquals("updated", edge.getLabel(), "Should update label");
    }

    @Test
    @DisplayName("Should handle MST status changes")
    void testMSTStatusChanges() {
        // Act
        standardEdge.setInMST(true);

        // Assert
        assertTrue(standardEdge.isInMST(), "Should be in MST after setting");

        // Act
        standardEdge.setInMST(false);

        // Assert
        assertFalse(standardEdge.isInMST(), "Should not be in MST after clearing");
    }

    @Test
    @DisplayName("Should handle visited status changes")
    void testVisitedStatusChanges() {
        // Act
        standardEdge.setVisited(true);

        // Assert
        assertTrue(standardEdge.isVisited(), "Should be visited after setting");

        // Act
        standardEdge.setVisited(false);

        // Assert
        assertFalse(standardEdge.isVisited(), "Should not be visited after clearing");
    }

    @Test
    @DisplayName("Should throw exception for null vertices in builder")
    void testBuilderNullVertices() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Edge.Builder(null, "B"),
                "Should throw exception for null from vertex");

        assertThrows(IllegalArgumentException.class,
                () -> new Edge.Builder("A", null),
                "Should throw exception for null to vertex");
    }

    @Test
    @DisplayName("Should throw exception for self-loop in builder")
    void testBuilderSelfLoop() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Edge.Builder("A", "A"),
                "Should throw exception for self-loop");

        assertTrue(exception.getMessage().contains("Self-loops"), "Exception should mention self-loops");
    }

    @Test
    @DisplayName("Should throw exception for negative weight in builder")
    void testBuilderNegativeWeight() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Edge.Builder("A", "B").weight(-1.0),
                "Should throw exception for negative weight");

        assertTrue(exception.getMessage().contains("negative"), "Exception should mention negative weight");
    }

    @Test
    @DisplayName("Should create edge from JSON-like data")
    void testFromJsonData() {
        // Act
        Edge edge = Edge.fromJsonData("X", "Y", 7.5);

        // Assert
        assertEquals("X", edge.getFrom(), "Should have correct from vertex");
        assertEquals("Y", edge.getTo(), "Should have correct to vertex");
        assertEquals(7.5, edge.getWeight(), 0.001, "Should have correct weight");
    }

    @Test
    @DisplayName("Should create edge with metadata")
    void testCreateEdgeWithMetadata() {
        // Act
        Edge edge = Edge.createEdge("U", "V", 4.0, Edge.EdgeType.HIGHWAY, "Main connection");

        // Assert
        assertEquals("U", edge.getFrom(), "Should have correct from vertex");
        assertEquals("V", edge.getTo(), "Should have correct to vertex");
        assertEquals(4.0, edge.getWeight(), 0.001, "Should have correct weight");
        assertEquals(Edge.EdgeType.HIGHWAY, edge.getType(), "Should have correct type");
        assertEquals("Main connection", edge.getLabel(), "Should have correct label");
    }

    @Test
    @DisplayName("Should compare edges by weight using static method")
    void testCompareByWeight() {
        // Arrange
        Edge lightEdge = new Edge.Builder("A", "B").weight(1.0).build();
        Edge heavyEdge = new Edge.Builder("C", "D").weight(3.0).build();

        // Act
        int result = Edge.compareByWeight(lightEdge, heavyEdge);

        // Assert
        assertTrue(result < 0, "Light edge should compare less than heavy edge");
    }

    @Test
    @DisplayName("Should compare edges by vertices using static method")
    void testCompareByVertices() {
        // Arrange
        Edge edge1 = new Edge.Builder("A", "B").build();
        Edge edge2 = new Edge.Builder("A", "C").build();
        Edge edge3 = new Edge.Builder("B", "A").build();

        // Act & Assert
        assertTrue(Edge.compareByVertices(edge1, edge2) < 0, "A-B should come before A-C");
        assertTrue(Edge.compareByVertices(edge2, edge1) > 0, "A-C should come after A-B");
        assertEquals(0, Edge.compareByVertices(edge1, edge3), "A-B and B-A should be equal in vertex comparison");
    }

    @Test
    @DisplayName("Should test EdgeDTO equality")
    void testEdgeDTOEquality() {
        // Arrange
        Edge.EdgeDTO dto1 = new Edge.EdgeDTO("A", "B", 1.0, false);
        Edge.EdgeDTO dto2 = new Edge.EdgeDTO("A", "B", 1.0, false);
        Edge.EdgeDTO dto3 = new Edge.EdgeDTO("A", "B", 2.0, false);
        Edge.EdgeDTO dto4 = new Edge.EdgeDTO("A", "B", 1.0, true);

        // Assert
        assertEquals(dto1, dto2, "DTOs with same data should be equal");
        assertNotEquals(dto1, dto3, "DTOs with different weights should not be equal");
        assertNotEquals(dto1, dto4, "DTOs with different MST status should not be equal");
        assertEquals(dto1, dto1, "DTO should equal itself");
        assertNotEquals(dto1, null, "DTO should not equal null");
        assertNotEquals(dto1, "not a dto", "DTO should not equal different type");
    }

    @Test
    @DisplayName("Should test EdgeDTO hash code")
    void testEdgeDTOHashCode() {
        // Arrange
        Edge.EdgeDTO dto1 = new Edge.EdgeDTO("A", "B", 1.0, false);
        Edge.EdgeDTO dto2 = new Edge.EdgeDTO("A", "B", 1.0, false);

        // Assert
        assertEquals(dto1.hashCode(), dto2.hashCode(), "DTOs with same data should have same hash code");
    }

    @Test
    @DisplayName("Should test EdgeDTO string representation")
    void testEdgeDTOToString() {
        // Arrange
        Edge.EdgeDTO dto = new Edge.EdgeDTO("X", "Y", 5.5, true);

        // Act
        String representation = dto.toString();

        // Assert
        assertNotNull(representation, "DTO string should not be null");
        assertTrue(representation.contains("EdgeDTO"), "Should contain class name");
        assertTrue(representation.contains("from='X'"), "Should contain from vertex");
        assertTrue(representation.contains("to='Y'"), "Should contain to vertex");
        assertTrue(representation.contains("weight=5.5"), "Should contain weight");
        assertTrue(representation.contains("inMST=true"), "Should contain MST status");
    }

    @Test
    @DisplayName("Should test copy constructor")
    void testCopyConstructor() {
        // Arrange
        Edge original = new Edge.Builder("A", "B")
                .weight(3.0)
                .type(Edge.EdgeType.BRIDGE)
                .label("Original")
                .build();
        original.setInMST(true);
        original.markTraversed();
        original.setStatus(Edge.EdgeStatus.INACTIVE);

        // Act
        Edge copy = new Edge(original);

        // Assert
        assertEquals(original.getFrom(), copy.getFrom(), "Should copy from vertex");
        assertEquals(original.getTo(), copy.getTo(), "Should copy to vertex");
        assertEquals(original.getWeight(), copy.getWeight(), 0.001, "Should copy weight");
        assertEquals(original.getType(), copy.getType(), "Should copy type");
        assertEquals(original.getId(), copy.getId(), "Should copy ID");
        assertEquals(original.isInMST(), copy.isInMST(), "Should copy MST status");
        assertEquals(original.isVisited(), copy.isVisited(), "Should copy visited status");
        assertEquals(original.getTraversalCount(), copy.getTraversalCount(), "Should copy traversal count");
        assertEquals(original.getStatus(), copy.getStatus(), "Should copy status");
        assertEquals(original.getLabel(), copy.getLabel(), "Should copy label");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.5, 1.0, 2.5, 10.0, 100.0})
    @DisplayName("Should handle various weight values")
    void testVariousWeights(double weight) {
        // Arrange & Act
        Edge edge = new Edge.Builder("A", "B").weight(weight).build();

        // Assert
        assertEquals(weight, edge.getWeight(), 0.001, "Should have correct weight: " + weight);
        assertTrue(edge.isValid(), "Edge should be valid with weight: " + weight);
    }

    @Test
    @DisplayName("Should have immutable creation timestamp")
    void testCreationTimestamp() {
        // Arrange
        long beforeCreation = System.currentTimeMillis();
        Edge edge = new Edge.Builder("A", "B").build();
        long afterCreation = System.currentTimeMillis();

        // Assert
        assertTrue(edge.getCreationTimestamp() >= beforeCreation,
                "Creation timestamp should be after start time");
        assertTrue(edge.getCreationTimestamp() <= afterCreation,
                "Creation timestamp should be before end time");
    }
}