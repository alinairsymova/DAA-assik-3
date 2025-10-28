package model;

import io.FileProcessor;
import io.JSONResultWriter;
import java.util.*;

/**
 * Extended GraphGenerator that creates organized datasets for MST testing
 * Works with the existing GraphGenerator class
 */
public class GraphDatasetGenerator extends GraphGenerator {

    private FileProcessor fileProcessor;
    private JSONResultWriter jsonWriter;

    public GraphDatasetGenerator() {
        super();
        this.fileProcessor = new FileProcessor();
        this.jsonWriter = new JSONResultWriter();
    }

    public GraphDatasetGenerator(long seed) {
        super(seed);
        this.fileProcessor = new FileProcessor();
        this.jsonWriter = new JSONResultWriter();
    }

    /**
     * Generate comprehensive test suite organized by size
     */
    public Map<String, List<Graph>> generateComprehensiveDataset() {
        Map<String, List<Graph>> dataset = new LinkedHashMap<>();

        dataset.put("small_graphs", generateSmallGraphs());
        dataset.put("medium_graphs", generateMediumGraphs());
        dataset.put("large_graphs", generateLargeGraphs());
        dataset.put("special_graphs", generateSpecialGraphs());

        return dataset;
    }

    /**
     * Generate small graphs (4-6 vertices) - 5 graphs
     */
    public List<Graph> generateSmallGraphs() {
        List<Graph> graphs = new ArrayList<>();

        // 1. Simple cycle - good for basic testing
        graphs.add(generateCycleGraph(4));

        // 2. Complete graph K4 - tests dense small graphs
        graphs.add(generateCompleteGraph(4));

        // 3. Tree structure - tests sparse graphs
        graphs.add(generateSparseGraph(5));

        // 4. Star graph - hub-and-spoke model
        graphs.add(generateStarGraph(6));

        // 5. Random connected graph
        graphs.add(generateRandomGraph(5, 0.6));

        return graphs;
    }

    /**
     * Generate medium graphs (10-15 vertices) - 4 graphs
     */
    public List<Graph> generateMediumGraphs() {
        List<Graph> graphs = new ArrayList<>();

        // 1. Grid graph - simulates city blocks
        graphs.add(generateGridGraph(3, 4));

        // 2. Dense graph - 70% connectivity
        graphs.add(generateDenseGraph(12));

        // 3. Sparse graph - tree-like
        graphs.add(generateSparseGraph(15));

        // 4. Transportation network
        graphs.add(generateTransportationNetwork(10, 8));

        return graphs;
    }

    /**
     * Generate large graphs (20-30+ vertices) - 4 graphs
     */
    public List<Graph> generateLargeGraphs() {
        List<Graph> graphs = new ArrayList<>();

        // 1. Large grid - for scalability testing
        graphs.add(generateGridGraph(5, 6));

        // 2. Clustered graph - simulates regional networks
        graphs.add(generateClusteredGraph(3, 10, 6));

        // 3. Large random graph
        graphs.add(generateRandomGraph(25, 0.4));

        // 4. Large transportation network
        graphs.add(generateTransportationNetwork(20, 15));

        return graphs;
    }

    /**
     * Generate special case graphs - 3 graphs
     */
    public List<Graph> generateSpecialGraphs() {
        List<Graph> graphs = new ArrayList<>();

        // 1. Graph with uniform weights
        graphs.add(generateUniformWeightGraph(8));

        // 2. Graph with large weight variations
        graphs.add(generateWideWeightRangeGraph(10));

        // 3. Graph with specific degree constraints
        graphs.add(generateDegreeConstrainedGraph(12));

        return graphs;
    }

    /**
     * Custom graph generation methods using existing GraphGenerator
     */

    public Graph generateCycleGraph(int nodes) {
        Graph.Builder builder = new Graph.Builder();

        // Add vertices
        for (int i = 1; i <= nodes; i++) {
            builder.addVertex("V" + i);
        }

        // Create cycle
        for (int i = 1; i <= nodes; i++) {
            int next = (i % nodes) + 1;
            double weight = 5.0 + 10.0 * Math.random();
            builder.addEdge("V" + i, "V" + next, weight);
        }

        return builder.build();
    }

    public Graph generateUniformWeightGraph(int nodes) {
        // All edges have the same weight
        return generateConnectedGraph(nodes, nodes * 2, 5.0, 5.0);
    }

    public Graph generateWideWeightRangeGraph(int nodes) {
        // Large variation in weights
        return generateConnectedGraph(nodes, nodes * 2, 1.0, 1000.0);
    }

    public Graph generateDegreeConstrainedGraph(int nodes) {
        // Generate graph where most nodes have degree 2-4
        return generateDegreeSpecificGraph(nodes, 2, 4);
    }

    /**
     * Enhanced version of generateRandomGraph that ensures connectivity
     */
    public Graph generateRandomGraph(int nodes, double connectivityProbability) {
        Graph graph = super.generateRandomGraph(nodes, connectivityProbability, 1.0, 100.0);

        // Ensure the graph is connected
        if (!graph.isConnected()) {
            // If not connected, generate a connected one with similar properties
            int minEdges = nodes - 1;
            int maxEdges = nodes * (nodes - 1) / 2;
            int targetEdges = Math.max(minEdges, (int)(connectivityProbability * maxEdges));

            graph = generateConnectedGraph(nodes, targetEdges, 1.0, 100.0);
        }

        return graph;
    }

    /**
     * Save all graphs to JSON files with organized naming
     */
    public void saveDatasetToFiles(Map<String, List<Graph>> dataset, String baseFilename) {
        int totalGraphs = dataset.values().stream().mapToInt(List::size).sum();
        System.out.println("Generating " + totalGraphs + " graphs...");

        for (Map.Entry<String, List<Graph>> category : dataset.entrySet()) {
            String categoryName = category.getKey();
            List<Graph> graphs = category.getValue();

            System.out.println("\n=== " + categoryName.toUpperCase().replace("_", " ") + " ===");

            for (int i = 0; i < graphs.size(); i++) {
                Graph graph = graphs.get(i);
                String filename = String.format("%s_%s_%d.json", baseFilename, categoryName, i + 1);

                // Use your existing JSON writer to save the graph
                fileProcessor.writeGraph(graph, filename);

                // Print graph statistics
                printGraphInfo(filename, graph);
            }
        }

        printDatasetSummary(dataset);
        validateDataset(dataset);
    }

    /**
     * Print detailed information about a graph
     */
    private void printGraphInfo(String filename, Graph graph) {
        int vertices = graph.getVerticesCount();
        int edges = graph.getEdgesCount();
        double density = graph.getDensity();
        boolean connected = graph.isConnected();

        // Calculate weight statistics
        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;
        double totalWeight = 0;

        for (Graph.EdgeDTO edge : graph.getEdges()) {
            double weight = edge.getWeight();
            minWeight = Math.min(minWeight, weight);
            maxWeight = Math.max(maxWeight, weight);
            totalWeight += weight;
        }

        double avgWeight = edges > 0 ? totalWeight / edges : 0;

        System.out.printf("  %s: %d vertices, %d edges, density: %.3f, connected: %s%n",
                filename, vertices, edges, density, connected);
        System.out.printf("    Weights: min=%.2f, max=%.2f, avg=%.2f%n",
                minWeight, maxWeight, avgWeight);
    }

    /**
     * Print dataset summary
     */
    private void printDatasetSummary(Map<String, List<Graph>> dataset) {
        System.out.println("\n=== DATASET SUMMARY ===");
        int totalGraphs = dataset.values().stream().mapToInt(List::size).sum();
        System.out.println("Total graphs generated: " + totalGraphs);

        for (Map.Entry<String, List<Graph>> category : dataset.entrySet()) {
            String categoryName = category.getKey().replace("_", " ");
            int count = category.getValue().size();
            System.out.printf("%-15s: %d graphs%n", categoryName, count);
        }
    }

    /**
     * Generate and save the complete assignment dataset
     */
    public void generateAssignmentDataset() {
        System.out.println("Generating Assignment 3 Dataset...");
        System.out.println("=================================");

        Map<String, List<Graph>> dataset = generateComprehensiveDataset();
        saveDatasetToFiles(dataset, "assign_3_input");

        System.out.println("\nDataset generation complete!");
        System.out.println("Files saved to: " + fileProcessor.getInputDirectory());
    }

    /**
     * Quick validation of generated graphs
     */
    public void validateDataset(Map<String, List<Graph>> dataset) {
        int validGraphs = 0;
        int connectedGraphs = 0;
        int totalVertices = 0;
        int totalEdges = 0;

        for (List<Graph> graphs : dataset.values()) {
            for (Graph graph : graphs) {
                if (graph != null) {
                    validGraphs++;
                    totalVertices += graph.getVerticesCount();
                    totalEdges += graph.getEdgesCount();

                    if (graph.isConnected()) {
                        connectedGraphs++;
                    }
                }
            }
        }

        System.out.println("\n=== DATASET VALIDATION ===");
        System.out.println("Valid graphs: " + validGraphs + "/" + getTotalGraphCount(dataset));
        System.out.println("Connected graphs: " + connectedGraphs + "/" + validGraphs);
        System.out.println("Total vertices across all graphs: " + totalVertices);
        System.out.println("Total edges across all graphs: " + totalEdges);

        if (validGraphs == connectedGraphs && validGraphs == getTotalGraphCount(dataset)) {
            System.out.println("✓ All graphs are valid and connected!");
        } else {
            System.out.println("⚠ Some graphs may have issues");
        }
    }

    /**
     * Get total number of graphs in dataset
     */
    private int getTotalGraphCount(Map<String, List<Graph>> dataset) {
        return dataset.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Generate specific size categories individually
     */
    public void generateSmallDataset() {
        System.out.println("Generating Small Graphs Dataset...");
        Map<String, List<Graph>> smallDataset = new HashMap<>();
        smallDataset.put("small_graphs", generateSmallGraphs());
        saveDatasetToFiles(smallDataset, "small_test");
    }

    public void generateMediumDataset() {
        System.out.println("Generating Medium Graphs Dataset...");
        Map<String, List<Graph>> mediumDataset = new HashMap<>();
        mediumDataset.put("medium_graphs", generateMediumGraphs());
        saveDatasetToFiles(mediumDataset, "medium_test");
    }

    public void generateLargeDataset() {
        System.out.println("Generating Large Graphs Dataset...");
        Map<String, List<Graph>> largeDataset = new HashMap<>();
        largeDataset.put("large_graphs", generateLargeGraphs());
        saveDatasetToFiles(largeDataset, "large_test");
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        GraphDatasetGenerator generator = new GraphDatasetGenerator(12345); // Fixed seed for reproducibility

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "small":
                    generator.generateSmallDataset();
                    break;
                case "medium":
                    generator.generateMediumDataset();
                    break;
                case "large":
                    generator.generateLargeDataset();
                    break;
                case "special":
                    Map<String, List<Graph>> specialDataset = new HashMap<>();
                    specialDataset.put("special_graphs", generator.generateSpecialGraphs());
                    generator.saveDatasetToFiles(specialDataset, "special_test");
                    break;
                default:
                    generator.generateAssignmentDataset();
            }
        } else {
            // Generate complete dataset by default
            generator.generateAssignmentDataset();
        }
    }
}