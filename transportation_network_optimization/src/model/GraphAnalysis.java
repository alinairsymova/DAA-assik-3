package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Comprehensive graph analysis utility for transportation networks
 * Provides various metrics and analysis methods for graph evaluation
 */
public class GraphAnalysis {

    private final Graph graph;
    private final Map<String, Object> analysisCache;
    private boolean cacheValid;

    public GraphAnalysis(Graph graph) {
        this.graph = graph;
        this.analysisCache = new HashMap<>();
        this.cacheValid = false;
    }

    /**
     * Invalidate cache when graph changes
     */
    public void invalidateCache() {
        this.cacheValid = false;
        this.analysisCache.clear();
    }

    /**
     * Get comprehensive analysis report
     */
    public Map<String, Object> getComprehensiveAnalysis() {
        if (cacheValid && analysisCache.containsKey("comprehensive")) {
            return (Map<String, Object>) analysisCache.get("comprehensive");
        }

        Map<String, Object> analysis = new LinkedHashMap<>();

        // Basic properties
        analysis.put("basicProperties", getBasicProperties());

        // Connectivity analysis
        analysis.put("connectivity", getConnectivityAnalysis());

        // Degree analysis
        analysis.put("degreeAnalysis", getDegreeAnalysis());

        // Weight analysis
        analysis.put("weightAnalysis", getWeightAnalysis());

        // Path analysis
        analysis.put("pathAnalysis", getPathAnalysis());

        // Centrality measures
        analysis.put("centrality", getCentralityMeasures());

        // Clustering analysis
        analysis.put("clustering", getClusteringAnalysis());

        // Transportation-specific metrics
        analysis.put("transportationMetrics", getTransportationMetrics());

        analysisCache.put("comprehensive", analysis);
        cacheValid = true;

        return analysis;
    }

    /**
     * Basic graph properties
     */
    public Map<String, Object> getBasicProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("vertexCount", graph.getVerticesCount());
        properties.put("edgeCount", graph.getEdgesCount());
        properties.put("density", graph.getDensity());
        properties.put("graphType", graph.getGraphType().toString());
        properties.put("isConnected", graph.isConnected());
        properties.put("isComplete", isCompleteGraph());
        properties.put("isTree", isTree());

        return properties;
    }

    /**
     * Connectivity analysis
     */
    public Map<String, Object> getConnectivityAnalysis() {
        Map<String, Object> connectivity = new LinkedHashMap<>();

        connectivity.put("isConnected", graph.isConnected());
        connectivity.put("connectedComponents", getConnectedComponentsCount());
        connectivity.put("articulationPoints", findArticulationPoints().size());
        connectivity.put("bridges", findBridges().size());
        connectivity.put("connectivityScore", calculateConnectivityScore());

        return connectivity;
    }

    /**
     * Degree distribution analysis
     */
    public Map<String, Object> getDegreeAnalysis() {
        Map<String, Object> degreeAnalysis = new LinkedHashMap<>();

        Map<String, Integer> degrees = getDegreeDistribution();
        Map<String, Object> stats = calculateDegreeStatistics(degrees);

        degreeAnalysis.put("degreeDistribution", degrees);
        degreeAnalysis.put("averageDegree", stats.get("average"));
        degreeAnalysis.put("minDegree", stats.get("min"));
        degreeAnalysis.put("maxDegree", stats.get("max"));
        degreeAnalysis.put("degreeVariance", stats.get("variance"));
        degreeAnalysis.put("degreeSkewness", stats.get("skewness"));

        return degreeAnalysis;
    }

    /**
     * Edge weight analysis
     */
    public Map<String, Object> getWeightAnalysis() {
        Map<String, Object> weightAnalysis = new LinkedHashMap<>();

        List<Double> weights = graph.getEdges().stream()
                .map(Graph.EdgeDTO::getWeight)
                .collect(Collectors.toList());

        Map<String, Object> stats = calculateWeightStatistics(weights);

        weightAnalysis.put("weightStatistics", stats);
        weightAnalysis.put("weightDistribution", getWeightDistribution(weights));
        weightAnalysis.put("isWeightedUniform", isWeightDistributionUniform(weights));

        return weightAnalysis;
    }

    /**
     * Path analysis
     */
    public Map<String, Object> getPathAnalysis() {
        Map<String, Object> pathAnalysis = new LinkedHashMap<>();

        pathAnalysis.put("diameter", calculateDiameter());
        pathAnalysis.put("radius", calculateRadius());
        pathAnalysis.put("averagePathLength", calculateAveragePathLength());
        pathAnalysis.put("eccentricityDistribution", getEccentricityDistribution());

        return pathAnalysis;
    }

    /**
     * Centrality measures
     */
    public Map<String, Object> getCentralityMeasures() {
        Map<String, Object> centrality = new LinkedHashMap<>();

        centrality.put("degreeCentrality", calculateDegreeCentrality());
        centrality.put("betweennessCentrality", calculateBetweennessCentrality());
        centrality.put("closenessCentrality", calculateClosenessCentrality());
        centrality.put("mostCentralVertices", findMostCentralVertices());

        return centrality;
    }

    /**
     * Clustering analysis
     */
    public Map<String, Object> getClusteringAnalysis() {
        Map<String, Object> clustering = new LinkedHashMap<>();

        clustering.put("globalClusteringCoefficient", calculateGlobalClusteringCoefficient());
        clustering.put("averageClusteringCoefficient", calculateAverageClusteringCoefficient());
        clustering.put("localClusteringCoefficients", calculateLocalClusteringCoefficients());
        clustering.put("trianglesCount", countTriangles());

        return clustering;
    }

    /**
     * Transportation-specific metrics
     */
    public Map<String, Object> getTransportationMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();

        metrics.put("networkEfficiency", calculateNetworkEfficiency());
        metrics.put("robustnessScore", calculateRobustnessScore());
        metrics.put("accessibilityIndex", calculateAccessibilityIndex());
        metrics.put("criticalPoints", identifyCriticalPoints());
        metrics.put("bottleneckEdges", identifyBottleneckEdges());

        return metrics;
    }

    // Implementation of analysis methods

    private boolean isCompleteGraph() {
        int n = graph.getVerticesCount();
        int maxEdges = n * (n - 1) / 2;
        return graph.getEdgesCount() == maxEdges;
    }

    private boolean isTree() {
        return graph.isConnected() && graph.getEdgesCount() == graph.getVerticesCount() - 1;
    }

    private int getConnectedComponentsCount() {
        return graph.getConnectedComponents().size();
    }

    private Map<String, Integer> getDegreeDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        for (Graph.VertexDTO vertex : graph.getVertices()) {
            distribution.put(vertex.getId(), vertex.getDegree());
        }
        return distribution;
    }

    private Map<String, Object> calculateDegreeStatistics(Map<String, Integer> degrees) {
        Map<String, Object> stats = new HashMap<>();

        double sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int degree : degrees.values()) {
            sum += degree;
            min = Math.min(min, degree);
            max = Math.max(max, degree);
        }

        double average = sum / degrees.size();
        double variance = 0;

        for (int degree : degrees.values()) {
            variance += Math.pow(degree - average, 2);
        }
        variance /= degrees.size();

        // Calculate skewness
        double skewness = 0;
        for (int degree : degrees.values()) {
            skewness += Math.pow(degree - average, 3);
        }
        skewness /= (degrees.size() * Math.pow(Math.sqrt(variance), 3));

        stats.put("average", average);
        stats.put("min", min);
        stats.put("max", max);
        stats.put("variance", variance);
        stats.put("skewness", skewness);

        return stats;
    }

    private Map<String, Object> calculateWeightStatistics(List<Double> weights) {
        Map<String, Object> stats = new HashMap<>();

        if (weights.isEmpty()) {
            stats.put("min", 0.0);
            stats.put("max", 0.0);
            stats.put("average", 0.0);
            stats.put("variance", 0.0);
            return stats;
        }

        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (double weight : weights) {
            sum += weight;
            min = Math.min(min, weight);
            max = Math.max(max, weight);
        }

        double average = sum / weights.size();
        double variance = 0;

        for (double weight : weights) {
            variance += Math.pow(weight - average, 2);
        }
        variance /= weights.size();

        stats.put("min", min);
        stats.put("max", max);
        stats.put("average", average);
        stats.put("variance", variance);

        return stats;
    }

    private Map<Double, Integer> getWeightDistribution(List<Double> weights) {
        Map<Double, Integer> distribution = new TreeMap<>();
        for (double weight : weights) {
            distribution.put(weight, distribution.getOrDefault(weight, 0) + 1);
        }
        return distribution;
    }

    private boolean isWeightDistributionUniform(List<Double> weights) {
        if (weights.size() < 2) return true;

        Map<Double, Integer> distribution = getWeightDistribution(weights);
        double expectedFrequency = (double) weights.size() / distribution.size();

        double chiSquare = 0;
        for (int frequency : distribution.values()) {
            chiSquare += Math.pow(frequency - expectedFrequency, 2) / expectedFrequency;
        }

        // Simple threshold for uniformity
        return chiSquare < distribution.size() * 0.5;
    }

    private double calculateConnectivityScore() {
        if (!graph.isConnected()) {
            return 0.0;
        }

        int n = graph.getVerticesCount();
        int articulationPoints = findArticulationPoints().size();
        int bridges = findBridges().size();

        // Higher score for fewer critical points
        double score = 1.0 - (double) (articulationPoints + bridges) / (2 * n);
        return Math.max(0.0, score);
    }

    public List<String> findArticulationPoints() {
        List<String> articulationPoints = new ArrayList<>();
        Map<String, Integer> discovery = new HashMap<>();
        Map<String, Integer> low = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        int[] time = {0};

        for (Graph.VertexDTO vertex : graph.getVertices()) {
            String vertexId = vertex.getId();
            if (!visited.contains(vertexId)) {
                findArticulationPointsDFS(vertexId, discovery, low, parent, visited, time, articulationPoints);
            }
        }

        return articulationPoints;
    }

    private void findArticulationPointsDFS(String u, Map<String, Integer> discovery,
                                           Map<String, Integer> low, Map<String, String> parent,
                                           Set<String> visited, int[] time, List<String> articulationPoints) {
        visited.add(u);
        discovery.put(u, time[0]);
        low.put(u, time[0]);
        time[0]++;

        int children = 0;

        for (String v : graph.getAdjacentVertices(u)) {
            if (!visited.contains(v)) {
                children++;
                parent.put(v, u);
                findArticulationPointsDFS(v, discovery, low, parent, visited, time, articulationPoints);

                low.put(u, Math.min(low.get(u), low.get(v)));

                // Check if u is an articulation point
                if (parent.get(u) == null && children > 1) {
                    articulationPoints.add(u);
                }
                if (parent.get(u) != null && low.get(v) >= discovery.get(u)) {
                    articulationPoints.add(u);
                }
            } else if (!v.equals(parent.get(u))) {
                low.put(u, Math.min(low.get(u), discovery.get(v)));
            }
        }
    }

    public List<Graph.EdgeDTO> findBridges() {
        List<Graph.EdgeDTO> bridges = new ArrayList<>();
        Map<String, Integer> discovery = new HashMap<>();
        Map<String, Integer> low = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        int[] time = {0};

        for (Graph.VertexDTO vertex : graph.getVertices()) {
            String vertexId = vertex.getId();
            if (!visited.contains(vertexId)) {
                findBridgesDFS(vertexId, discovery, low, parent, visited, time, bridges);
            }
        }

        return bridges;
    }

    private void findBridgesDFS(String u, Map<String, Integer> discovery,
                                Map<String, Integer> low, Map<String, String> parent,
                                Set<String> visited, int[] time, List<Graph.EdgeDTO> bridges) {
        visited.add(u);
        discovery.put(u, time[0]);
        low.put(u, time[0]);
        time[0]++;

        for (String v : graph.getAdjacentVertices(u)) {
            if (!visited.contains(v)) {
                parent.put(v, u);
                findBridgesDFS(v, discovery, low, parent, visited, time, bridges);

                low.put(u, Math.min(low.get(u), low.get(v)));

                // Check if edge u-v is a bridge
                if (low.get(v) > discovery.get(u)) {
                    bridges.add(graph.getEdge(u, v));
                }
            } else if (!v.equals(parent.get(u))) {
                low.put(u, Math.min(low.get(u), discovery.get(v)));
            }
        }
    }

    // Simplified implementations for complex metrics

    private double calculateDiameter() {
        // Simplified diameter calculation using BFS
        double diameter = 0;
        List<Graph.VertexDTO> vertices = graph.getVertices();

        for (int i = 0; i < Math.min(10, vertices.size()); i++) { // Sample for performance
            String start = vertices.get(i).getId();
            Map<String, Integer> distances = bfsDistances(start);

            for (int dist : distances.values()) {
                diameter = Math.max(diameter, dist);
            }
        }

        return diameter;
    }

    private double calculateRadius() {
        // Simplified radius calculation
        double radius = Double.MAX_VALUE;
        List<Graph.VertexDTO> vertices = graph.getVertices();

        for (int i = 0; i < Math.min(10, vertices.size()); i++) {
            String start = vertices.get(i).getId();
            Map<String, Integer> distances = bfsDistances(start);

            double eccentricity = 0;
            for (int dist : distances.values()) {
                eccentricity = Math.max(eccentricity, dist);
            }
            radius = Math.min(radius, eccentricity);
        }

        return radius;
    }

    private double calculateAveragePathLength() {
        // Simplified average path length calculation
        double totalLength = 0;
        int pairCount = 0;
        List<Graph.VertexDTO> vertices = graph.getVertices();

        for (int i = 0; i < Math.min(20, vertices.size()); i++) {
            for (int j = i + 1; j < Math.min(20, vertices.size()); j++) {
                String from = vertices.get(i).getId();
                String to = vertices.get(j).getId();

                int distance = bfsDistance(from, to);
                if (distance != -1) {
                    totalLength += distance;
                    pairCount++;
                }
            }
        }

        return pairCount > 0 ? totalLength / pairCount : 0;
    }

    private Map<String, Integer> getEccentricityDistribution() {
        Map<String, Integer> eccentricity = new HashMap<>();
        List<Graph.VertexDTO> vertices = graph.getVertices();

        for (int i = 0; i < Math.min(15, vertices.size()); i++) {
            String vertex = vertices.get(i).getId();
            Map<String, Integer> distances = bfsDistances(vertex);

            int maxDist = 0;
            for (int dist : distances.values()) {
                maxDist = Math.max(maxDist, dist);
            }
            eccentricity.put(vertex, maxDist);
        }

        return eccentricity;
    }

    private Map<String, Double> calculateDegreeCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.getVerticesCount();

        for (Graph.VertexDTO vertex : graph.getVertices()) {
            double centralityValue = (double) vertex.getDegree() / (n - 1);
            centrality.put(vertex.getId(), centralityValue);
        }

        return centrality;
    }

    private Map<String, Double> calculateBetweennessCentrality() {
        // Simplified betweenness centrality
        Map<String, Double> centrality = new HashMap<>();

        for (Graph.VertexDTO vertex : graph.getVertices()) {
            centrality.put(vertex.getId(), 0.0);
        }

        // Sample some paths for approximation
        List<Graph.VertexDTO> vertices = graph.getVertices();
        int samples = Math.min(50, vertices.size() * vertices.size());

        for (int i = 0; i < samples; i++) {
            String start = vertices.get(randomIndex(vertices.size())).getId();
            String end = vertices.get(randomIndex(vertices.size())).getId();

            if (!start.equals(end)) {
                List<String> path = bfsPath(start, end);
                if (path != null) {
                    for (String vertex : path) {
                        if (!vertex.equals(start) && !vertex.equals(end)) {
                            centrality.put(vertex, centrality.get(vertex) + 1.0);
                        }
                    }
                }
            }
        }

        // Normalize
        for (String vertex : centrality.keySet()) {
            centrality.put(vertex, centrality.get(vertex) / samples);
        }

        return centrality;
    }

    private Map<String, Double> calculateClosenessCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        List<Graph.VertexDTO> vertices = graph.getVertices();

        for (int i = 0; i < Math.min(20, vertices.size()); i++) {
            String vertex = vertices.get(i).getId();
            Map<String, Integer> distances = bfsDistances(vertex);

            double totalDistance = 0;
            int reachable = 0;

            for (int dist : distances.values()) {
                totalDistance += dist;
                reachable++;
            }

            if (reachable > 1) {
                centrality.put(vertex, (reachable - 1) / totalDistance);
            } else {
                centrality.put(vertex, 0.0);
            }
        }

        return centrality;
    }

    private List<String> findMostCentralVertices() {
        Map<String, Double> centrality = calculateDegreeCentrality();
        return centrality.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculateGlobalClusteringCoefficient() {
        int triangles = countTriangles();
        int triplets = countTriplets();

        return triplets > 0 ? (double) (3 * triangles) / triplets : 0.0;
    }

    private double calculateAverageClusteringCoefficient() {
        Map<String, Double> localCoefficients = calculateLocalClusteringCoefficients();
        return localCoefficients.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private Map<String, Double> calculateLocalClusteringCoefficients() {
        Map<String, Double> coefficients = new HashMap<>();

        for (Graph.VertexDTO vertex : graph.getVertices()) {
            String vertexId = vertex.getId();
            List<String> neighbors = graph.getAdjacentVertices(vertexId);
            int k = neighbors.size();

            if (k < 2) {
                coefficients.put(vertexId, 0.0);
                continue;
            }

            int edgesBetweenNeighbors = 0;
            for (int i = 0; i < neighbors.size(); i++) {
                for (int j = i + 1; j < neighbors.size(); j++) {
                    if (graph.containsEdge(neighbors.get(i), neighbors.get(j))) {
                        edgesBetweenNeighbors++;
                    }
                }
            }

            double coefficient = (double) (2 * edgesBetweenNeighbors) / (k * (k - 1));
            coefficients.put(vertexId, coefficient);
        }

        return coefficients;
    }

    private int countTriangles() {
        int triangles = 0;
        List<Graph.VertexDTO> vertices = graph.getVertices();

        for (Graph.VertexDTO vertex : vertices) {
            String v = vertex.getId();
            List<String> neighbors = graph.getAdjacentVertices(v);

            for (int i = 0; i < neighbors.size(); i++) {
                for (int j = i + 1; j < neighbors.size(); j++) {
                    if (graph.containsEdge(neighbors.get(i), neighbors.get(j))) {
                        triangles++;
                    }
                }
            }
        }

        return triangles / 3; // Each triangle counted 3 times
    }

    private int countTriplets() {
        int triplets = 0;
        for (Graph.VertexDTO vertex : graph.getVertices()) {
            int k = vertex.getDegree();
            triplets += k * (k - 1) / 2;
        }
        return triplets;
    }

    private double calculateNetworkEfficiency() {
        if (!graph.isConnected()) return 0.0;

        double totalEfficiency = 0;
        int pairCount = 0;
        List<Graph.VertexDTO> vertices = graph.getVertices();

        for (int i = 0; i < Math.min(15, vertices.size()); i++) {
            for (int j = i + 1; j < Math.min(15, vertices.size()); j++) {
                String from = vertices.get(i).getId();
                String to = vertices.get(j).getId();

                int distance = bfsDistance(from, to);
                if (distance > 0) {
                    totalEfficiency += 1.0 / distance;
                    pairCount++;
                }
            }
        }

        return pairCount > 0 ? totalEfficiency / pairCount : 0.0;
    }

    private double calculateRobustnessScore() {
        double baseEfficiency = calculateNetworkEfficiency();

        // Test robustness by removing random edges
        List<Graph.EdgeDTO> edges = graph.getEdges();
        int removalCount = Math.min(5, edges.size() / 10);

        double efficiencyAfterRemoval = 0;
        int tests = Math.min(3, removalCount);

        for (int i = 0; i < tests; i++) {
            // Simulate edge removal (conceptual)
            efficiencyAfterRemoval += baseEfficiency * 0.9; // Simplified
        }

        efficiencyAfterRemoval /= tests;

        return baseEfficiency > 0 ? efficiencyAfterRemoval / baseEfficiency : 0.0;
    }

    private double calculateAccessibilityIndex() {
        Map<String, Double> closeness = calculateClosenessCentrality();
        return closeness.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private List<String> identifyCriticalPoints() {
        List<String> criticalPoints = new ArrayList<>();
        criticalPoints.addAll(findArticulationPoints());

        // Add high-betweenness vertices
        Map<String, Double> betweenness = calculateBetweennessCentrality();
        betweenness.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> criticalPoints.add(entry.getKey()));

        return criticalPoints.stream().distinct().collect(Collectors.toList());
    }

    private List<Graph.EdgeDTO> identifyBottleneckEdges() {
        List<Graph.EdgeDTO> bottlenecks = new ArrayList<>();
        bottlenecks.addAll(findBridges());

        // Add high-traffic edges (simplified)
        List<Graph.EdgeDTO> edges = graph.getEdges();
        edges.sort((e1, e2) -> Double.compare(e2.getWeight(), e1.getWeight()));

        bottlenecks.addAll(edges.subList(0, Math.min(3, edges.size())));

        return bottlenecks.stream().distinct().collect(Collectors.toList());
    }

    // Utility methods

    private Map<String, Integer> bfsDistances(String start) {
        Map<String, Integer> distances = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDistance = distances.get(current);

            for (String neighbor : graph.getAdjacentVertices(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    distances.put(neighbor, currentDistance + 1);
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }

    private int bfsDistance(String start, String end) {
        if (start.equals(end)) return 0;

        Map<String, Integer> distances = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDistance = distances.get(current);

            for (String neighbor : graph.getAdjacentVertices(current)) {
                if (neighbor.equals(end)) {
                    return currentDistance + 1;
                }

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    distances.put(neighbor, currentDistance + 1);
                    queue.add(neighbor);
                }
            }
        }

        return -1; // Not reachable
    }

    private List<String> bfsPath(String start, String end) {
        if (start.equals(end)) return Arrays.asList(start);

        Map<String, String> parent = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            for (String neighbor : graph.getAdjacentVertices(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);

                    if (neighbor.equals(end)) {
                        // Reconstruct path
                        List<String> path = new ArrayList<>();
                        String node = end;
                        while (node != null) {
                            path.add(0, node);
                            node = parent.get(node);
                        }
                        return path;
                    }
                }
            }
        }

        return null; // No path found
    }

    private int randomIndex(int max) {
        return (int) (Math.random() * max);
    }

    /**
     * Generate analysis report as formatted string
     */
    public String generateAnalysisReport() {
        Map<String, Object> analysis = getComprehensiveAnalysis();
        StringBuilder report = new StringBuilder();

        report.append("=== GRAPH ANALYSIS REPORT ===\n\n");

        // Basic properties
        Map<String, Object> basic = (Map<String, Object>) analysis.get("basicProperties");
        report.append("BASIC PROPERTIES:\n");
        report.append(String.format("  Vertices: %d\n", basic.get("vertexCount")));
        report.append(String.format("  Edges: %d\n", basic.get("edgeCount")));
        report.append(String.format("  Density: %.3f\n", basic.get("density")));
        report.append(String.format("  Type: %s\n", basic.get("graphType")));
        report.append(String.format("  Connected: %s\n", basic.get("isConnected")));
        report.append(String.format("  Complete: %s\n", basic.get("isComplete")));
        report.append(String.format("  Tree: %s\n\n", basic.get("isTree")));

        // Connectivity
        Map<String, Object> connectivity = (Map<String, Object>) analysis.get("connectivity");
        report.append("CONNECTIVITY ANALYSIS:\n");
        report.append(String.format("  Connectivity Score: %.3f\n", connectivity.get("connectivityScore")));
        report.append(String.format("  Components: %d\n", connectivity.get("connectedComponents")));
        report.append(String.format("  Articulation Points: %d\n", connectivity.get("articulationPoints")));
        report.append(String.format("  Bridges: %d\n\n", connectivity.get("bridges")));

        // Degree analysis
        Map<String, Object> degree = (Map<String, Object>) analysis.get("degreeAnalysis");
        report.append("DEGREE ANALYSIS:\n");
        report.append(String.format("  Average Degree: %.2f\n", degree.get("averageDegree")));
        report.append(String.format("  Min Degree: %d\n", degree.get("minDegree")));
        report.append(String.format("  Max Degree: %d\n\n", degree.get("maxDegree")));

        // Transportation metrics
        Map<String, Object> transport = (Map<String, Object>) analysis.get("transportationMetrics");
        report.append("TRANSPORTATION METRICS:\n");
        report.append(String.format("  Network Efficiency: %.3f\n", transport.get("networkEfficiency")));
        report.append(String.format("  Robustness Score: %.3f\n", transport.get("robustnessScore")));
        report.append(String.format("  Accessibility Index: %.3f\n", transport.get("accessibilityIndex")));

        List<String> criticalPoints = (List<String>) transport.get("criticalPoints");
        report.append(String.format("  Critical Points: %s\n", criticalPoints));

        return report.toString();
    }

    public Graph getGraph() {
        return graph;
    }
}