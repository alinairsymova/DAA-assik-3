package model;

import java.util.Objects;

/**
 * Comprehensive Edge class for representing weighted edges in graphs
 * Supports MST algorithms with full encapsulation and professional design patterns
 */
public class Edge implements Comparable<Edge> {
    // Immutable properties
    private final String from;
    private final String to;
    private final double weight;
    private final String id;
    private final EdgeType type;

    // Mutable state
    private boolean inMST;
    private boolean visited;
    private int traversalCount;
    private EdgeStatus status;

    // Metadata
    private final long creationTimestamp;
    private String label;
    private Object userData;

    /**
     * Edge type enumeration
     */
    public enum EdgeType {
        STANDARD,          // Regular edge
        BRIDGE,            // Bridge edge (if removed, graph becomes disconnected)
        CRITICAL,          // Critical for connectivity
        HIGHWAY,           // High capacity/importance
        LOCAL              // Low capacity/importance
    }

    /**
     * Edge status enumeration
     */
    public enum EdgeStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        BLOCKED
    }

    /**
     * Builder pattern for Edge construction
     */
    public static class Builder {
        private final String from;
        private final String to;
        private double weight = 1.0;
        private EdgeType type = EdgeType.STANDARD;
        private String label = "";
        private Object userData = null;

        public Builder(String from, String to) {
            if (from == null || to == null) {
                throw new IllegalArgumentException("From and To vertices cannot be null");
            }
            if (from.equals(to)) {
                throw new IllegalArgumentException("Self-loops are not allowed");
            }
            this.from = from;
            this.to = to;
        }

        public Builder weight(double weight) {
            if (weight < 0) {
                throw new IllegalArgumentException("Edge weight cannot be negative");
            }
            this.weight = weight;
            return this;
        }

        public Builder type(EdgeType type) {
            this.type = type;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder userData(Object userData) {
            this.userData = userData;
            return this;
        }

        public Edge build() {
            return new Edge(this);
        }
    }

    /**
     * Private constructor using Builder pattern
     */
    private Edge(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.weight = builder.weight;
        this.type = builder.type;
        this.label = builder.label;
        this.userData = builder.userData;

        this.id = generateEdgeId(from, to);
        this.inMST = false;
        this.visited = false;
        this.traversalCount = 0;
        this.status = EdgeStatus.ACTIVE;
        this.creationTimestamp = System.currentTimeMillis();
    }

    /**
     * Copy constructor
     */
    public Edge(Edge other) {
        this.from = other.from;
        this.to = other.to;
        this.weight = other.weight;
        this.type = other.type;
        this.id = other.id;
        this.inMST = other.inMST;
        this.visited = other.visited;
        this.traversalCount = other.traversalCount;
        this.status = other.status;
        this.creationTimestamp = other.creationTimestamp;
        this.label = other.label;
        this.userData = other.userData;
    }

    /**
     * Generates a canonical edge ID (order-independent)
     */
    private String generateEdgeId(String from, String to) {
        return from.compareTo(to) < 0 ? from + "-" + to : to + "-" + from;
    }

    // Core functionality methods

    /**
     * Gets the other vertex connected by this edge
     */
    public String getOtherVertex(String vertex) {
        if (vertex.equals(from)) return to;
        if (vertex.equals(to)) return from;
        throw new IllegalArgumentException("Vertex " + vertex + " is not part of this edge");
    }

    /**
     * Checks if this edge contains the specified vertex
     */
    public boolean containsVertex(String vertex) {
        return from.equals(vertex) || to.equals(vertex);
    }

    /**
     * Checks if this edge connects the two specified vertices
     */
    public boolean connects(String vertex1, String vertex2) {
        return (from.equals(vertex1) && to.equals(vertex2)) ||
                (from.equals(vertex2) && to.equals(vertex1));
    }

    /**
     * Marks the edge as traversed and increments counter
     */
    public void markTraversed() {
        this.traversalCount++;
        this.visited = true;
    }

    /**
     * Resets traversal state
     */
    public void resetTraversal() {
        this.visited = false;
        this.traversalCount = 0;
    }

    /**
     * Creates a reversed version of this edge (same vertices, different direction context)
     */
    public Edge createReversed() {
        return new Builder(to, from)
                .weight(weight)
                .type(type)
                .label(label + " (reversed)")
                .userData(userData)
                .build();
    }

    // Validation methods

    /**
     * Validates if the edge data is consistent
     */
    public boolean isValid() {
        return from != null &&
                to != null &&
                !from.equals(to) &&
                weight >= 0 &&
                id != null &&
                !id.isEmpty();
    }

    /**
     * Checks if this edge is a self-loop (should always be false due to validation)
     */
    public boolean isSelfLoop() {
        return from.equals(to);
    }

    // Comparison and equality methods

    @Override
    public int compareTo(Edge other) {
        int weightComparison = Double.compare(this.weight, other.weight);
        if (weightComparison != 0) return weightComparison;

        int fromComparison = this.from.compareTo(other.from);
        if (fromComparison != 0) return fromComparison;

        return this.to.compareTo(other.to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(id, edge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // String representation methods

    @Override
    public String toString() {
        return String.format("Edge{from='%s', to='%s', weight=%.2f, type=%s, inMST=%s, status=%s}",
                from, to, weight, type, inMST, status);
    }

    /**
     * Returns a detailed string representation
     */
    public String toDetailedString() {
        return String.format(
                "Edge{id='%s', from='%s', to='%s', weight=%.2f, type=%s, " +
                        "inMST=%s, visited=%s, traversals=%d, status=%s, label='%s'}",
                id, from, to, weight, type, inMST, visited, traversalCount, status, label
        );
    }

    /**
     * Returns a JSON-like string representation
     */
    public String toJsonString() {
        return String.format(
                "{\"from\":\"%s\", \"to\":\"%s\", \"weight\":%.2f, \"type\":\"%s\", \"inMST\":%s}",
                from, to, weight, type, inMST
        );
    }

    // Getters and setters with proper encapsulation

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getWeight() { return weight; }
    public String getId() { return id; }
    public EdgeType getType() { return type; }
    public boolean isInMST() { return inMST; }
    public boolean isVisited() { return visited; }
    public int getTraversalCount() { return traversalCount; }
    public EdgeStatus getStatus() { return status; }
    public long getCreationTimestamp() { return creationTimestamp; }
    public String getLabel() { return label; }
    public Object getUserData() { return userData; }

    public void setInMST(boolean inMST) { this.inMST = inMST; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public void setStatus(EdgeStatus status) { this.status = status; }
    public void setLabel(String label) { this.label = label; }
    public void setUserData(Object userData) { this.userData = userData; }

    // Utility methods for algorithm support

    /**
     * Checks if this edge is available for traversal
     */
    public boolean isAvailable() {
        return status == EdgeStatus.ACTIVE && !isSelfLoop();
    }

    /**
     * Checks if this edge is critical (based on type)
     */
    public boolean isCritical() {
        return type == EdgeType.BRIDGE || type == EdgeType.CRITICAL;
    }

    /**
     * Returns the normalized weight (0-1 scale) based on provided max weight
     */
    public double getNormalizedWeight(double maxWeight) {
        if (maxWeight <= 0) return 0.0;
        return Math.min(weight / maxWeight, 1.0);
    }

    /**
     * Creates an EdgeDTO for external API use
     */
    public EdgeDTO toDTO() {
        return new EdgeDTO(from, to, weight, inMST);
    }

    /**
     * Public immutable Edge Data Transfer Object for external use
     */
    public static class EdgeDTO {
        private final String from;
        private final String to;
        private final double weight;
        private final boolean inMST;

        public EdgeDTO(String from, String to, double weight, boolean inMST) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.inMST = inMST;
        }

        public EdgeDTO(Edge edge) {
            this.from = edge.getFrom();
            this.to = edge.getTo();
            this.weight = edge.getWeight();
            this.inMST = edge.isInMST();
        }

        // Getters only - immutable DTO
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public double getWeight() { return weight; }
        public boolean isInMST() { return inMST; }

        @Override
        public String toString() {
            return "EdgeDTO{from='" + from + "', to='" + to + "', weight=" + weight + ", inMST=" + inMST + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EdgeDTO edgeDTO = (EdgeDTO) o;
            return Double.compare(weight, edgeDTO.weight) == 0 &&
                    inMST == edgeDTO.inMST &&
                    Objects.equals(from, edgeDTO.from) &&
                    Objects.equals(to, edgeDTO.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, weight, inMST);
        }
    }

    // Static utility methods

    /**
     * Creates an edge from JSON-like data
     */
    public static Edge fromJsonData(String from, String to, double weight) {
        return new Builder(from, to).weight(weight).build();
    }

    /**
     * Creates an edge with additional metadata
     */
    public static Edge createEdge(String from, String to, double weight, EdgeType type, String label) {
        return new Builder(from, to)
                .weight(weight)
                .type(type)
                .label(label)
                .build();
    }

    /**
     * Compares two edges by weight
     */
    public static int compareByWeight(Edge e1, Edge e2) {
        return Double.compare(e1.getWeight(), e2.getWeight());
    }

    /**
     * Compares two edges by from vertex then to vertex
     */
    public static int compareByVertices(Edge e1, Edge e2) {
        int fromCompare = e1.getFrom().compareTo(e2.getFrom());
        if (fromCompare != 0) return fromCompare;
        return e1.getTo().compareTo(e2.getTo());
    }
}