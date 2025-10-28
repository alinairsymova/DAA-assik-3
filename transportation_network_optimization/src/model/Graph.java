package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Comprehensive Graph class for representing weighted undirected graphs
 * Supports multiple representations and operations for MST algorithms
 */
public class Graph {
    private final Map<String, Vertex> vertices;
    private final List<Edge> edges; // Теперь использует model.Edge
    private final Map<String, List<Edge>> adjacencyList; // Теперь использует model.Edge
    private final boolean[][] adjacencyMatrix;
    private final int[][] weightMatrix;
    private final GraphType graphType;
    private int operationCount;

    public enum GraphType {
        SPARSE, DENSE, UNKNOWN
    }

    public List<Edge> getAllEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * Private Vertex representation with full encapsulation
     */
    private static class Vertex implements Comparable<Vertex> {
        private final String id;
        private int degree;
        private boolean visited;
        private double key; // For Prim's algorithm
        private Vertex parent; // For MST construction

        public Vertex(String id) {
            this.id = id;
            this.degree = 0;
            this.visited = false;
            this.key = Double.MAX_VALUE;
            this.parent = null;
        }

        @Override
        public int compareTo(Vertex other) {
            return Double.compare(this.key, other.key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return Objects.equals(id, vertex.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Vertex{id='" + id + "', degree=" + degree + "}";
        }

        // Getters and setters
        public String getId() { return id; }
        public int getDegree() { return degree; }
        public void setDegree(int degree) { this.degree = degree; }
        public boolean isVisited() { return visited; }
        public void setVisited(boolean visited) { this.visited = visited; }
        public double getKey() { return key; }
        public void setKey(double key) { this.key = key; }
        public Vertex getParent() { return parent; }
        public void setParent(Vertex parent) { this.parent = parent; }
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

        public EdgeDTO(Edge edge) { // Теперь принимает model.Edge
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
    }

    /**
     * Public immutable Vertex Data Transfer Object for external use
     */
    public static class VertexDTO {
        private final String id;
        private final int degree;

        public VertexDTO(String id, int degree) {
            this.id = id;
            this.degree = degree;
        }

        public VertexDTO(Vertex vertex) {
            this.id = vertex.getId();
            this.degree = vertex.getDegree();
        }

        // Getters only - immutable DTO
        public String getId() { return id; }
        public int getDegree() { return degree; }

        @Override
        public String toString() {
            return "VertexDTO{id='" + id + "', degree=" + degree + "}";
        }
    }

    /**
     * Builder pattern for Graph construction
     */
    public static class Builder {
        private final List<String> vertices = new ArrayList<>();
        private final List<Edge> edges = new ArrayList<>(); // Теперь использует model.Edge

        public Builder addVertex(String vertex) {
            if (!vertices.contains(vertex)) {
                vertices.add(vertex);
            }
            return this;
        }

        public Builder addEdge(String from, String to, double weight) {
            addVertex(from);
            addVertex(to);
            // Создаем model.Edge напрямую
            Edge edge = new Edge.Builder(from, to)
                    .weight(weight)
                    .build();
            edges.add(edge);
            return this;
        }

        public Graph build() {
            return new Graph(edges);
        }
    }

    /**
     * Constructor that infers vertices from edges
     */
    public Graph(List<Edge> edges) { // Теперь принимает List<model.Edge>
        // Extract unique vertices from edges
        Set<String> vertexIds = new HashSet<>();
        for (Edge edge : edges) {
            vertexIds.add(edge.getFrom());
            vertexIds.add(edge.getTo());
        }

        // Convert to Vertex objects
        List<Vertex> vertices = vertexIds.stream()
                .map(Vertex::new)
                .collect(Collectors.toList());

        // Initialize the graph using the extracted vertices and edges
        this.vertices = new HashMap<>();
        this.edges = new ArrayList<>(edges);
        this.adjacencyList = new HashMap<>();
        this.operationCount = 0;

        // Initialize vertices map
        for (Vertex vertex : vertices) {
            this.vertices.put(vertex.getId(), vertex);
            adjacencyList.put(vertex.getId(), new ArrayList<>());
        }

        // Build adjacency list and calculate degrees
        for (Edge edge : edges) {
            adjacencyList.get(edge.getFrom()).add(edge);
            adjacencyList.get(edge.getTo()).add(edge);

            this.vertices.get(edge.getFrom()).setDegree(
                    this.vertices.get(edge.getFrom()).getDegree() + 1
            );
            this.vertices.get(edge.getTo()).setDegree(
                    this.vertices.get(edge.getTo()).getDegree() + 1
            );
        }

        // Build matrix representations
        int size = vertices.size();
        this.adjacencyMatrix = new boolean[size][size];
        this.weightMatrix = new int[size][size];

        // Initialize matrices
        for (int i = 0; i < size; i++) {
            Arrays.fill(weightMatrix[i], Integer.MAX_VALUE);
        }

        // Create vertex index mapping
        Map<String, Integer> vertexIndexMap = new HashMap<>();
        int index = 0;
        for (String vertexId : this.vertices.keySet()) {
            vertexIndexMap.put(vertexId, index++);
        }

        // Fill matrices
        for (Edge edge : edges) {
            int fromIndex = vertexIndexMap.get(edge.getFrom());
            int toIndex = vertexIndexMap.get(edge.getTo());

            adjacencyMatrix[fromIndex][toIndex] = true;
            adjacencyMatrix[toIndex][fromIndex] = true;
            weightMatrix[fromIndex][toIndex] = (int) edge.getWeight();
            weightMatrix[toIndex][fromIndex] = (int) edge.getWeight();
        }

        // Determine graph type
        this.graphType = determineGraphType();
    }

    /**
     * Graph analysis methods
     */
    private GraphType determineGraphType() {
        int V = getVerticesCount();
        int E = getEdgesCount();
        int maxEdges = V * (V - 1) / 2;
        double density = (double) E / maxEdges;

        return density < 0.3 ? GraphType.SPARSE :
                density > 0.7 ? GraphType.DENSE : GraphType.UNKNOWN;
    }

    public boolean isConnected() {
        if (vertices.isEmpty()) return true;

        Set<String> visited = new HashSet<>();
        dfs(vertices.keySet().iterator().next(), visited);
        return visited.size() == vertices.size();
    }

    private void dfs(String current, Set<String> visited) {
        visited.add(current);
        for (Edge edge : getAdjacentEdgesInternal(current)) {
            String neighbor = edge.getOtherVertex(current);
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited);
            }
        }
    }

    /**
     * Public API methods with proper encapsulation
     */

    // Vertex operations
    public VertexDTO getVertex(String id) {
        operationCount++;
        Vertex vertex = vertices.get(id);
        return vertex != null ? new VertexDTO(vertex) : null;
    }

    public List<VertexDTO> getVertices() {
        return vertices.values().stream()
                .map(VertexDTO::new)
                .collect(Collectors.toList());
    }

    public List<String> getVertexIds() {
        return new ArrayList<>(vertices.keySet());
    }

    public boolean containsVertex(String id) {
        operationCount++;
        return vertices.containsKey(id);
    }

    // Edge operations
    public List<EdgeDTO> getEdges() {
        return edges.stream()
                .map(EdgeDTO::new) // Теперь создает DTO из model.Edge
                .collect(Collectors.toList());
    }

    public List<EdgeDTO> getAdjacentEdges(String vertexId) {
        operationCount++;
        return adjacencyList.getOrDefault(vertexId, new ArrayList<>())
                .stream()
                .map(EdgeDTO::new) // Теперь создает DTO из model.Edge
                .collect(Collectors.toList());
    }

    public List<String> getAdjacentVertices(String vertexId) {
        operationCount++;
        return adjacencyList.getOrDefault(vertexId, new ArrayList<>())
                .stream()
                .map(edge -> edge.getOtherVertex(vertexId))
                .collect(Collectors.toList());
    }

    public EdgeDTO getEdge(String from, String to) {
        operationCount++;
        Edge edge = getEdgeInternal(from, to);
        return edge != null ? new EdgeDTO(edge) : null;
    }

    public boolean containsEdge(String from, String to) {
        return getEdgeInternal(from, to) != null;
    }

    /**
     * Internal private methods for actual operations
     */
    private List<Edge> getAdjacentEdgesInternal(String vertexId) {
        return new ArrayList<>(adjacencyList.getOrDefault(vertexId, new ArrayList<>()));
    }

    private Edge getEdgeInternal(String from, String to) {
        for (Edge edge : adjacencyList.getOrDefault(from, new ArrayList<>())) {
            if (edge.containsVertex(to)) {
                return edge;
            }
        }
        return null;
    }

    // Internal method for algorithms to access actual Edge objects
    Edge getEdgeForAlgorithms(String from, String to) {
        operationCount++;
        return getEdgeInternal(from, to);
    }

    // Internal method for algorithms to access actual Vertex objects
    Vertex getVertexForAlgorithms(String id) {
        operationCount++;
        return vertices.get(id);
    }

    // Internal method for algorithms to access all edges
    List<Edge> getEdgesForAlgorithms() {
        return new ArrayList<>(edges);
    }

    // Internal method for algorithms to access adjacency list
    Map<String, List<Edge>> getAdjacencyListForAlgorithms() {
        return adjacencyList;
    }

    /**
     * Graph properties - Getters only
     */
    public int getVerticesCount() {
        return vertices.size();
    }

    public int getEdgesCount() {
        return edges.size();
    }

    public GraphType getGraphType() {
        return graphType;
    }

    public int getOperationCount() {
        return operationCount;
    }

    public void resetOperationCount() {
        this.operationCount = 0;
    }

    /**
     * MST-specific methods
     */
    public List<EdgeDTO> getMSTEdges() {
        return edges.stream()
                .filter(Edge::isInMST)
                .map(EdgeDTO::new)
                .collect(Collectors.toList());
    }

    public double getMSTTotalCost() {
        return edges.stream()
                .filter(Edge::isInMST)
                .mapToDouble(Edge::getWeight)
                .sum();
    }

    public void resetMST() {
        for (Edge edge : edges) {
            edge.setInMST(false);
        }
        for (Vertex vertex : vertices.values()) {
            vertex.setParent(null);
            vertex.setKey(Double.MAX_VALUE);
        }
    }

    /**
     * Matrix representations accessors - defensive copying
     */
    public boolean[][] getAdjacencyMatrix() {
        boolean[][] copy = new boolean[adjacencyMatrix.length][];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            copy[i] = Arrays.copyOf(adjacencyMatrix[i], adjacencyMatrix[i].length);
        }
        return copy;
    }

    public int[][] getWeightMatrix() {
        int[][] copy = new int[weightMatrix.length][];
        for (int i = 0; i < weightMatrix.length; i++) {
            copy[i] = Arrays.copyOf(weightMatrix[i], weightMatrix[i].length);
        }
        return copy;
    }

    public Map<String, Integer> getVertexIndexMapping() {
        Map<String, Integer> mapping = new HashMap<>();
        int index = 0;
        for (String vertexId : vertices.keySet()) {
            mapping.put(vertexId, index++);
        }
        return Collections.unmodifiableMap(mapping);
    }

    /**
     * Utility methods
     */
    public Graph createSubgraph(Set<String> vertexSubset) {
        Builder builder = new Builder();

        for (Edge edge : edges) {
            if (vertexSubset.contains(edge.getFrom()) && vertexSubset.contains(edge.getTo())) {
                builder.addEdge(edge.getFrom(), edge.getTo(), edge.getWeight());
            }
        }

        return builder.build();
    }

    public List<Graph> getConnectedComponents() {
        List<Graph> components = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String vertexId : vertices.keySet()) {
            if (!visited.contains(vertexId)) {
                Set<String> componentVertices = new HashSet<>();
                dfsComponent(vertexId, visited, componentVertices);
                components.add(createSubgraph(componentVertices));
            }
        }

        return components;
    }

    private void dfsComponent(String current, Set<String> visited, Set<String> component) {
        visited.add(current);
        component.add(current);
        for (Edge edge : getAdjacentEdgesInternal(current)) {
            String neighbor = edge.getOtherVertex(current);
            if (!visited.contains(neighbor)) {
                dfsComponent(neighbor, visited, component);
            }
        }
    }

    /**
     * Validation methods
     */
    public boolean isValid() {
        // Check for null vertices
        if (vertices.containsValue(null)) return false;

        // Check for duplicate edges
        Set<String> edgeIds = new HashSet<>();
        for (Edge edge : edges) {
            if (!edgeIds.add(edge.getId())) return false;
        }

        // Check that all edges reference existing vertices
        for (Edge edge : edges) {
            if (!vertices.containsKey(edge.getFrom()) || !vertices.containsKey(edge.getTo())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("Graph{vertices=%d, edges=%d, type=%s, density=%.3f, connected=%s}",
                getVerticesCount(), getEdgesCount(), graphType, getDensity(), isConnected());
    }

    /**
     * Statistical information
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("vertices", getVerticesCount());
        stats.put("edges", getEdgesCount());
        stats.put("density", getDensity());
        stats.put("graphType", graphType);
        stats.put("connected", isConnected());
        stats.put("averageDegree", calculateAverageDegree());
        stats.put("minDegree", calculateMinDegree());
        stats.put("maxDegree", calculateMaxDegree());

        return Collections.unmodifiableMap(stats);
    }

    public double getDensity() {
        int V = getVerticesCount();
        if (V <= 1) return 0;
        int maxEdges = V * (V - 1) / 2;
        return (double) getEdgesCount() / maxEdges;
    }

    private double calculateAverageDegree() {
        return vertices.values().stream()
                .mapToInt(Vertex::getDegree)
                .average()
                .orElse(0.0);
    }

    private int calculateMinDegree() {
        return vertices.values().stream()
                .mapToInt(Vertex::getDegree)
                .min()
                .orElse(0);
    }

    private int calculateMaxDegree() {
        return vertices.values().stream()
                .mapToInt(Vertex::getDegree)
                .max()
                .orElse(0);
    }

    /**
     * Factory method from JSON-like structure
     */
    public static Graph fromJsonData(List<String> nodes, List<Map<String, Object>> edgesData) {
        Builder builder = new Builder();

        for (String node : nodes) {
            builder.addVertex(node);
        }

        for (Map<String, Object> edgeData : edgesData) {
            String from = (String) edgeData.get("from");
            String to = (String) edgeData.get("to");
            double weight = ((Number) edgeData.get("weight")).doubleValue();
            builder.addEdge(from, to, weight);
        }

        return builder.build();
    }

    /**
     * Factory method for creating empty graph
     */
    public static Graph createEmptyGraph() {
        return new Builder().build();
    }
}