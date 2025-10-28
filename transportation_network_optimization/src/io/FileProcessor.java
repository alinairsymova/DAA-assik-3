package io;

import model.Graph;
import model.MSTResult;
import algorithms.MSTAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

/**
 * Coordinates all file input/output operations for the Transportation Network Optimizer
 * Acts as a facade for JSONGraphReader and JSONResultWriter
 */
public class FileProcessor {

    private JSONGraphReader graphReader;
    private JSONResultWriter resultWriter;
    private String inputDirectory;
    private String outputDirectory;

    /**
     * Constructor with dependency injection
     */
    public FileProcessor(JSONGraphReader graphReader, JSONResultWriter resultWriter) {
        this.graphReader = graphReader;
        this.resultWriter = resultWriter;
        this.inputDirectory = "data/input/";
        this.outputDirectory = "data/output/";
        ensureDirectoriesExist();
    }

    /**
     * Default constructor
     */
    public FileProcessor() {
        this.graphReader = new JSONGraphReader();
        this.resultWriter = new JSONResultWriter();
        this.inputDirectory = "data/input/";
        this.outputDirectory = "data/output/";
        ensureDirectoriesExist();
    }

    /**
     * Ensure input and output directories exist
     */
    private void ensureDirectoriesExist() {
        try {
            Files.createDirectories(Paths.get(inputDirectory));
            Files.createDirectories(Paths.get(outputDirectory));
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }

    // Graph Reading Operations

    /**
     * Read a graph from JSON file
     */
    public Graph readGraph(String filename) {
        try {
            String filePath = inputDirectory + filename;
            return graphReader.readGraphFromFile(filePath);
        } catch (Exception e) {
            System.err.println("Error reading graph from " + filename + ": " + e.getMessage());
            return createDefaultGraph(); // Fallback to default graph
        }
    }

    /**
     * Read multiple graphs from directory
     */
    public List<Graph> readAllGraphs() {
        List<Graph> graphs = new ArrayList<>();
        try {
            Files.list(Paths.get(inputDirectory))
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        Graph graph = graphReader.readGraphFromFile(path.toString());
                        if (graph != null) {
                            graphs.add(graph);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error reading graphs from directory: " + e.getMessage());
        }
        return graphs;
    }

    // Result Writing Operations

    /**
     * Write MST result to JSON file
     */
    public void writeMSTResult(MSTResult result, String filename) {
        try {
            String filePath = outputDirectory + filename;
            resultWriter.writeMSTResult(result, filePath);
        } catch (Exception e) {
            System.err.println("Error writing MST result to " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Write algorithm comparison results
     */
    public void writeComparisonResults(List<MSTResult> results, String filename) {
        try {
            String filePath = outputDirectory + filename;
            resultWriter.writeComparisonResults(results, filePath);
        } catch (Exception e) {
            System.err.println("Error writing comparison results: " + e.getMessage());
        }
    }

    /**
     * Write performance analysis report
     */
    public void writePerformanceReport(List<PerformanceData> performanceData, String filename) {
        try {
            String filePath = outputDirectory + filename;
            resultWriter.writePerformanceReport(performanceData, filePath);
        } catch (Exception e) {
            System.err.println("Error writing performance report: " + e.getMessage());
        }
    }

    // Batch Processing Operations

    /**
     * Process all graphs in input directory and run all algorithms
     */
    public void processAllGraphs(List<MSTAlgorithm> algorithms) {
        List<Graph> graphs = readAllGraphs();

        for (Graph graph : graphs) {
            String graphName = getGraphName(graph);
            System.out.println("Processing graph: " + graphName);

            for (MSTAlgorithm algorithm : algorithms) {
                try {
                    String algorithmName = algorithm.getAlgorithmName();
                    // FIX: Changed from findMST() to computeMST()
                    MSTResult result = algorithm.computeMST(graph);
                    String filename = graphName + "_" + algorithmName + "_result.json";
                    writeMSTResult(result, filename);
                } catch (Exception e) {
                    System.err.println("Error processing graph with " + algorithm.getAlgorithmName() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Process single graph with all algorithms
     */
    public List<MSTResult> processGraph(Graph graph, List<MSTAlgorithm> algorithms) {
        List<MSTResult> results = new ArrayList<>();
        String graphName = getGraphName(graph);
        System.out.println("Processing graph: " + graphName);

        for (MSTAlgorithm algorithm : algorithms) {
            try {
                // FIX: Changed from findMST() to computeMST()
                MSTResult result = algorithm.computeMST(graph);
                results.add(result);
                String filename = graphName + "_" + algorithm.getAlgorithmName() + "_result.json";
                writeMSTResult(result, filename);
            } catch (Exception e) {
                System.err.println("Error processing graph with " + algorithm.getAlgorithmName() + ": " + e.getMessage());
            }
        }

        return results;
    }

    /**
     * Generate comprehensive analysis report for all graphs and algorithms
     */
    public void generateComprehensiveReport(List<MSTAlgorithm> algorithms, List<MSTResult> comparisonResults) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String reportFilename = "comprehensive_analysis_" + timestamp + ".json";
            writeComparisonResults(comparisonResults, reportFilename);

            // Generate performance data
            List<PerformanceData> performanceData = new ArrayList<>();
            for (MSTAlgorithm algorithm : algorithms) {
                performanceData.add(new PerformanceData(
                        algorithm.getAlgorithmName(),
                        System.currentTimeMillis(),
                        Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                ));
            }

            String performanceFilename = "performance_report_" + timestamp + ".json";
            writePerformanceReport(performanceData, performanceFilename);
        } catch (Exception e) {
            System.err.println("Error generating comprehensive report: " + e.getMessage());
        }
    }

    /**
     * Generate comparison report for multiple results
     */
    public void generateComparisonReport(List<MSTResult> results, String filename) {
        try {
            writeComparisonResults(results, filename);
            System.out.println("Comparison report generated: " + filename);
        } catch (Exception e) {
            System.err.println("Error generating comparison report: " + e.getMessage());
        }
    }

    // Utility Methods

    private Graph createDefaultGraph() {
        // Create a simple default graph when file reading fails
        // FIX: Use Graph.Builder instead of Graph.GraphBuilder
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("C", "A", 3.0)
                .build();
    }

    private String getGraphName(Graph graph) {
        // Extract meaningful name from graph or use default
        return "graph_" + graph.getVerticesCount() + "v_" + graph.getEdgesCount() + "e";
    }

    public String getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
        ensureDirectoriesExist();
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        ensureDirectoriesExist();
    }

    public JSONGraphReader getGraphReader() {
        return graphReader;
    }

    public void setGraphReader(JSONGraphReader graphReader) {
        this.graphReader = graphReader;
    }

    public JSONResultWriter getResultWriter() {
        return resultWriter;
    }

    public void setResultWriter(JSONResultWriter resultWriter) {
        this.resultWriter = resultWriter;
    }

    /**
     * Check if input directory contains graph files
     */
    public boolean hasGraphFiles() {
        try {
            return Files.list(Paths.get(inputDirectory))
                    .anyMatch(path -> path.toString().endsWith(".json"));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get list of available graph files
     */
    public List<String> getAvailableGraphFiles() {
        List<String> files = new ArrayList<>();
        try {
            Files.list(Paths.get(inputDirectory))
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> files.add(path.getFileName().toString()));
        } catch (IOException e) {
            System.err.println("Error listing graph files: " + e.getMessage());
        }
        return files;
    }

    /**
     * Write graph to file
     */
    public void writeGraph(Graph graph, String filename) {
        try {
            String filePath = outputDirectory + filename;
            resultWriter.writeGraph(graph, filePath);
        } catch (Exception e) {
            System.err.println("Error writing graph to file: " + e.getMessage());
        }
    }

    /**
     * Inner class for performance data
     */
    public static class PerformanceData {
        private String algorithmName;
        private long executionTime;
        private long memoryUsed;

        public PerformanceData(String algorithmName, long executionTime, long memoryUsed) {
            this.algorithmName = algorithmName;
            this.executionTime = executionTime;
            this.memoryUsed = memoryUsed;
        }

        // Getters
        public String getAlgorithmName() { return algorithmName; }
        public long getExecutionTime() { return executionTime; }
        public long getMemoryUsed() { return memoryUsed; }
    }
}