package model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates various types of graphs for testing MST algorithms
 * Supports different graph structures and transportation network simulations
 */
public class GraphGenerator {

    private Random random;

    public GraphGenerator() {
        this.random = new Random();
    }

    public GraphGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Generate a complete graph with n nodes
     */
    public Graph generateCompleteGraph(int nodes) {
        return generateCompleteGraph(nodes, 1.0, 100.0);
    }

    public Graph generateCompleteGraph(int nodes, double minWeight, double maxWeight) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add all nodes
        for (int i = 1; i <= nodes; i++) {
            builder.addVertex("V" + i);
        }

        // Connect every node to every other node
        for (int i = 1; i <= nodes; i++) {
            for (int j = i + 1; j <= nodes; j++) {
                double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
                builder.addEdge("V" + i, "V" + j, weight);
            }
        }

        return builder.build();
    }

    /**
     * Generate a connected graph with n nodes and m edges
     */
    public Graph generateConnectedGraph(int nodes, int edges) {
        return generateConnectedGraph(nodes, edges, 1.0, 100.0);
    }

    public Graph generateConnectedGraph(int nodes, int edges, double minWeight, double maxWeight) {
        if (edges < nodes - 1) {
            throw new IllegalArgumentException("For a connected graph, edges must be at least nodes - 1");
        }
        if (edges > nodes * (nodes - 1) / 2) {
            throw new IllegalArgumentException("Too many edges for the given number of nodes");
        }

        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add all nodes
        for (int i = 1; i <= nodes; i++) {
            builder.addVertex("V" + i);
        }

        // First, create a spanning tree to ensure connectivity
        List<String> connectedNodes = new ArrayList<>();
        List<String> unconnectedNodes = new ArrayList<>();

        connectedNodes.add("V1");
        for (int i = 2; i <= nodes; i++) {
            unconnectedNodes.add("V" + i);
        }

        while (!unconnectedNodes.isEmpty()) {
            String connectedNode = connectedNodes.get(random.nextInt(connectedNodes.size()));
            String unconnectedNode = unconnectedNodes.remove(random.nextInt(unconnectedNodes.size()));

            double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
            builder.addEdge(connectedNode, unconnectedNode, weight);
            connectedNodes.add(unconnectedNode);
        }

        // Add remaining edges randomly
        int edgesAdded = nodes - 1;
        Graph tempGraph = builder.build(); // Build temporary graph to check edges

        while (edgesAdded < edges) {
            int node1 = random.nextInt(nodes) + 1;
            int node2 = random.nextInt(nodes) + 1;

            if (node1 != node2) {
                String from = "V" + node1;
                String to = "V" + node2;

                if (!tempGraph.containsEdge(from, to)) {
                    double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
                    builder.addEdge(from, to, weight);
                    edgesAdded++;
                    // Rebuild temp graph to reflect new edge
                    tempGraph = builder.build();
                }
            }
        }

        return builder.build();
    }

    /**
     * Generate a sparse graph (tree-like structure)
     */
    public Graph generateSparseGraph(int nodes) {
        return generateConnectedGraph(nodes, nodes - 1, 1.0, 50.0);
    }

    /**
     * Generate a dense graph
     */
    public Graph generateDenseGraph(int nodes) {
        int maxEdges = nodes * (nodes - 1) / 2;
        int edges = (int) (maxEdges * 0.7); // 70% of possible edges
        return generateConnectedGraph(nodes, edges, 1.0, 100.0);
    }

    /**
     * Generate a grid graph (useful for transportation networks)
     */
    public Graph generateGridGraph(int rows, int cols) {
        return generateGridGraph(rows, cols, 1.0, 10.0);
    }

    public Graph generateGridGraph(int rows, int cols, double minWeight, double maxWeight) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add all nodes in grid positions
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String nodeId = "R" + i + "C" + j;
                builder.addVertex(nodeId);
            }
        }

        // Add horizontal edges
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                String node1 = "R" + i + "C" + j;
                String node2 = "R" + i + "C" + (j + 1);
                double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
                builder.addEdge(node1, node2, weight);
            }
        }

        // Add vertical edges
        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < cols; j++) {
                String node1 = "R" + i + "C" + j;
                String node2 = "R" + (i + 1) + "C" + j;
                double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
                builder.addEdge(node1, node2, weight);
            }
        }

        return builder.build();
    }

    /**
     * Generate a star graph (hub-and-spoke model - common in transportation)
     */
    public Graph generateStarGraph(int nodes) {
        return generateStarGraph(nodes, 1.0, 50.0);
    }

    public Graph generateStarGraph(int nodes, double minWeight, double maxWeight) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add all nodes
        for (int i = 1; i <= nodes; i++) {
            builder.addVertex("V" + i);
        }

        // Connect all nodes to the central node (node 1)
        for (int i = 2; i <= nodes; i++) {
            double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
            builder.addEdge("V1", "V" + i, weight);
        }

        return builder.build();
    }

    /**
     * Generate a random graph with specified connectivity probability
     */
    public Graph generateRandomGraph(int nodes, double connectivityProbability) {
        return generateRandomGraph(nodes, connectivityProbability, 1.0, 100.0);
    }

    public Graph generateRandomGraph(int nodes, double connectivityProbability, double minWeight, double maxWeight) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add all nodes
        for (int i = 1; i <= nodes; i++) {
            builder.addVertex("V" + i);
        }

        // Add edges with specified probability
        for (int i = 1; i <= nodes; i++) {
            for (int j = i + 1; j <= nodes; j++) {
                if (random.nextDouble() < connectivityProbability) {
                    double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
                    builder.addEdge("V" + i, "V" + j, weight);
                }
            }
        }

        Graph graph = builder.build();

        // Ensure graph is connected by adding a spanning tree if needed
        if (!graph.isConnected()) {
            graph = makeConnected(graph, minWeight, maxWeight);
        }

        return graph;
    }

    /**
     * Generate a transportation network graph (simulates real road/rail networks)
     */
    public Graph generateTransportationNetwork(int cities, int additionalConnections) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add cities as nodes
        for (int i = 1; i <= cities; i++) {
            builder.addVertex("City" + i);
        }

        // Create a minimum spanning tree backbone
        List<String> connected = new ArrayList<>();
        List<String> unconnected = new ArrayList<>();

        connected.add("City1");
        for (int i = 2; i <= cities; i++) {
            unconnected.add("City" + i);
        }

        while (!unconnected.isEmpty()) {
            String connectedCity = connected.get(random.nextInt(connected.size()));
            String unconnectedCity = unconnected.remove(random.nextInt(unconnected.size()));

            // Weight based on "distance" between cities
            double weight = 10 + 90 * random.nextDouble();
            builder.addEdge(connectedCity, unconnectedCity, weight);
            connected.add(unconnectedCity);
        }

        // Add additional connections (highways, main routes)
        Graph tempGraph = builder.build();
        for (int i = 0; i < additionalConnections; i++) {
            int city1 = random.nextInt(cities) + 1;
            int city2 = random.nextInt(cities) + 1;

            if (city1 != city2) {
                String from = "City" + city1;
                String to = "City" + city2;

                if (!tempGraph.containsEdge(from, to)) {
                    // Additional routes might have lower weights (better roads)
                    double weight = 5 + 45 * random.nextDouble();
                    builder.addEdge(from, to, weight);
                    tempGraph = builder.build(); // Update temp graph
                }
            }
        }

        return builder.build();
    }

    /**
     * Generate a graph with clusters (simulates regional networks)
     */
    public Graph generateClusteredGraph(int clusters, int nodesPerCluster, int interClusterEdges) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();
        int nodeId = 1;

        // Generate each cluster
        for (int cluster = 0; cluster < clusters; cluster++) {
            String clusterPrefix = "C" + (cluster + 1) + "V";

            // Add nodes for this cluster
            for (int i = 0; i < nodesPerCluster; i++) {
                builder.addVertex(clusterPrefix + (nodeId++));
            }
        }

        // Build the base graph
        Graph baseGraph = builder.build();

        // Create dense connections within each cluster
        for (int cluster = 0; cluster < clusters; cluster++) {
            String clusterPrefix = "C" + (cluster + 1) + "V";
            int startId = cluster * nodesPerCluster + 1;

            // Create a dense subgraph for this cluster
            for (int i = startId; i < startId + nodesPerCluster; i++) {
                for (int j = i + 1; j < startId + nodesPerCluster; j++) {
                    if (random.nextDouble() < 0.6) { // 60% connectivity within cluster
                        double weight = 1 + 20 * random.nextDouble(); // Shorter distances within cluster
                        builder.addEdge(clusterPrefix + i, clusterPrefix + j, weight);
                    }
                }
            }
        }

        // Add connections between clusters
        for (int i = 0; i < interClusterEdges; i++) {
            int cluster1 = random.nextInt(clusters);
            int cluster2 = random.nextInt(clusters);

            if (cluster1 != cluster2) {
                String cluster1Prefix = "C" + (cluster1 + 1) + "V";
                String cluster2Prefix = "C" + (cluster2 + 1) + "V";

                int node1 = cluster1 * nodesPerCluster + random.nextInt(nodesPerCluster) + 1;
                int node2 = cluster2 * nodesPerCluster + random.nextInt(nodesPerCluster) + 1;

                // Inter-cluster connections might have higher weights (longer distances)
                double weight = 50 + 50 * random.nextDouble();
                builder.addEdge(cluster1Prefix + node1, cluster2Prefix + node2, weight);
            }
        }

        return builder.build();
    }

    /**
     * Generate a graph with specific degree distribution
     */
    public Graph generateDegreeSpecificGraph(int nodes, int minDegree, int maxDegree) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add all nodes
        for (int i = 1; i <= nodes; i++) {
            builder.addVertex("V" + i);
        }

        // Build initial graph
        Graph graph = builder.build();
        int[] currentDegree = new int[nodes + 1];
        int attempts = 0;
        int maxAttempts = nodes * 10;

        while (attempts < maxAttempts) {
            int node1 = random.nextInt(nodes) + 1;
            int node2 = random.nextInt(nodes) + 1;

            if (node1 != node2) {
                String from = "V" + node1;
                String to = "V" + node2;

                if (!graph.containsEdge(from, to) &&
                        currentDegree[node1] < maxDegree &&
                        currentDegree[node2] < maxDegree) {

                    double weight = 1 + 99 * random.nextDouble();
                    builder.addEdge(from, to, weight);
                    graph = builder.build(); // Update graph
                    currentDegree[node1]++;
                    currentDegree[node2]++;
                }
            }

            attempts++;
        }

        // Ensure minimum degrees are met
        for (int i = 1; i <= nodes; i++) {
            while (currentDegree[i] < minDegree) {
                // Find a node to connect to
                for (int j = 1; j <= nodes; j++) {
                    if (i != j) {
                        String from = "V" + i;
                        String to = "V" + j;

                        if (!graph.containsEdge(from, to) && currentDegree[j] < maxDegree) {
                            double weight = 1 + 99 * random.nextDouble();
                            builder.addEdge(from, to, weight);
                            graph = builder.build(); // Update graph
                            currentDegree[i]++;
                            currentDegree[j]++;
                            break;
                        }
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Make a graph connected by adding necessary edges
     */
    private Graph makeConnected(Graph graph, double minWeight, double maxWeight) {
        // ИСПРАВЛЕНО: Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        // Add all existing vertices and edges
        for (Graph.VertexDTO vertex : graph.getVertices()) {
            builder.addVertex(vertex.getId());
        }
        for (Graph.EdgeDTO edge : graph.getEdges()) {
            builder.addEdge(edge.getFrom(), edge.getTo(), edge.getWeight());
        }

        // Find connected components
        List<Graph> components = graph.getConnectedComponents();

        // Connect components
        for (int i = 1; i < components.size(); i++) {
            Graph comp1 = components.get(i - 1);
            Graph comp2 = components.get(i);

            // Get first vertex from each component
            String node1 = comp1.getVertices().get(0).getId();
            String node2 = comp2.getVertices().get(0).getId();

            double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
            builder.addEdge(node1, node2, weight);
        }

        return builder.build();
    }

    /**
     * Generate multiple test graphs for comprehensive testing
     */
    public List<Graph> generateTestSuite() {
        List<Graph> testSuite = new ArrayList<>();

        // Small sparse graph
        testSuite.add(generateSparseGraph(10));

        // Medium dense graph
        testSuite.add(generateDenseGraph(20));

        // Grid graph (simulates city blocks)
        testSuite.add(generateGridGraph(5, 5));

        // Star graph (hub-and-spoke)
        testSuite.add(generateStarGraph(15));

        // Transportation network
        testSuite.add(generateTransportationNetwork(12, 8));

        // Clustered graph
        testSuite.add(generateClusteredGraph(3, 8, 4));

        return testSuite;
    }

    /**
     * Get graph statistics
     */
    public String getGraphStatistics(Graph graph) {
        int nodes = graph.getVerticesCount();
        int edges = graph.getEdgesCount();

        double totalWeight = 0;
        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;

        for (Graph.EdgeDTO edge : graph.getEdges()) {
            totalWeight += edge.getWeight();
            minWeight = Math.min(minWeight, edge.getWeight());
            maxWeight = Math.max(maxWeight, edge.getWeight());
        }

        double averageWeight = edges > 0 ? totalWeight / edges : 0;
        double density = graph.getDensity();

        return String.format(
                "Graph: %d nodes, %d edges, Density: %.3f\n" +
                        "Weights: min=%.2f, max=%.2f, avg=%.2f, Connected: %s",
                nodes, edges, density, minWeight, maxWeight, averageWeight, graph.isConnected()
        );
    }

    public void setSeed(long seed) {
        this.random = new Random(seed);
    }
}