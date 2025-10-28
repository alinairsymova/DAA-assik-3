package config;

import analysis.AlgorithmComparator;
import model.Graph;

/**
 * Application configuration and dependency management
 * Centralizes configuration for the algorithm comparison system
 */
public class AppConfig {

    // Singleton instance
    private static AppConfig instance;

    // Configuration properties
    private String applicationName;
    private String version;
    private boolean debugMode;
    private double consistencyThreshold;
    private long memoryThresholdBytes;
    private int maxRecommendations;

    // Dependencies
    private AlgorithmComparator algorithmComparator;
    private Graph defaultGraph;

    // Private constructor for singleton pattern
    private AppConfig() {
        initializeDefaults();
    }

    /**
     * Singleton instance getter
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    /**
     * Initialize default configuration values
     */
    private void initializeDefaults() {
        this.applicationName = "Transportation Network Optimizer";
        this.version = "1.0.0";
        this.debugMode = true;
        this.consistencyThreshold = 0.8;
        this.memoryThresholdBytes = 1024 * 1024 * 100; // 100 MB
        this.maxRecommendations = 5;

        // Initialize dependencies
        this.algorithmComparator = new AlgorithmComparator();
        this.defaultGraph = createDefaultGraph(); // Use factory method instead
    }

    /**
     * Creates a default graph for the application
     */
    private Graph createDefaultGraph() {
        // Create a simple default graph using the Builder pattern
        return new Graph.Builder()
                .addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("A", "C", 3.0)
                .build();
    }

    // Configuration getters and setters

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public double getConsistencyThreshold() {
        return consistencyThreshold;
    }

    public void setConsistencyThreshold(double consistencyThreshold) {
        this.consistencyThreshold = consistencyThreshold;
    }

    public long getMemoryThresholdBytes() {
        return memoryThresholdBytes;
    }

    public void setMemoryThresholdBytes(long memoryThresholdBytes) {
        this.memoryThresholdBytes = memoryThresholdBytes;
    }

    public int getMaxRecommendations() {
        return maxRecommendations;
    }

    public void setMaxRecommendations(int maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }

    // Dependency getters and setters

    public AlgorithmComparator getAlgorithmComparator() {
        return algorithmComparator;
    }

    public void setAlgorithmComparator(AlgorithmComparator algorithmComparator) {
        this.algorithmComparator = algorithmComparator;
    }

    public Graph getDefaultGraph() {
        return defaultGraph;
    }

    public void setDefaultGraph(Graph defaultGraph) {
        this.defaultGraph = defaultGraph;
    }

    /**
     * Validates if the configuration is properly set up
     */
    public boolean validateConfiguration() {
        if (algorithmComparator == null) {
            System.err.println("Error: Algorithm comparator is not initialized");
            return false;
        }

        if (defaultGraph == null) {
            System.err.println("Error: Default graph is not initialized");
            return false;
        }

        if (consistencyThreshold < 0 || consistencyThreshold > 1) {
            System.err.println("Error: Consistency threshold must be between 0 and 1");
            return false;
        }

        if (memoryThresholdBytes <= 0) {
            System.err.println("Error: Memory threshold must be positive");
            return false;
        }

        if (maxRecommendations <= 0) {
            System.err.println("Error: Max recommendations must be positive");
            return false;
        }

        return true;
    }

    /**
     * Prints current configuration for debugging
     */
    public void printConfiguration() {
        if (debugMode) {
            System.out.println("=== Application Configuration ===");
            System.out.println("Application: " + applicationName);
            System.out.println("Version: " + version);
            System.out.println("Debug Mode: " + debugMode);
            System.out.println("Consistency Threshold: " + consistencyThreshold);
            System.out.println("Memory Threshold: " + memoryThresholdBytes + " bytes");
            System.out.println("Max Recommendations: " + maxRecommendations);
            System.out.println("Algorithm Comparator: " + (algorithmComparator != null ? "Initialized" : "Null"));
            System.out.println("Default Graph: " + (defaultGraph != null ? "Initialized" : "Null"));
            if (defaultGraph != null) {
                System.out.println("Default Graph Vertices: " + defaultGraph.getVerticesCount());
                System.out.println("Default Graph Edges: " + defaultGraph.getEdgesCount());
            }
            System.out.println("=================================");
        }
    }

    /**
     * Resets configuration to default values
     */
    public void resetToDefaults() {
        initializeDefaults();
    }

    /**
     * Creates an empty graph (for testing or fresh starts)
     */
    public Graph createEmptyGraph() {
        return Graph.createEmptyGraph();
    }

    /**
     * Creates a sample graph for demonstration
     */
    public Graph createSampleGraph() {
        return new Graph.Builder()
                .addEdge("City1", "City2", 50.0)
                .addEdge("City2", "City3", 75.0)
                .addEdge("City3", "City4", 60.0)
                .addEdge("City1", "City4", 120.0)
                .addEdge("City2", "City4", 90.0)
                .build();
    }

    /**
     * Configuration for algorithm performance analysis
     */
    public static class PerformanceConfig {
        private int warmupIterations = 5;
        private int measurementIterations = 10;
        private boolean enableGarbageCollection = true;

        public int getWarmupIterations() {
            return warmupIterations;
        }

        public void setWarmupIterations(int warmupIterations) {
            this.warmupIterations = warmupIterations;
        }

        public int getMeasurementIterations() {
            return measurementIterations;
        }

        public void setMeasurementIterations(int measurementIterations) {
            this.measurementIterations = measurementIterations;
        }

        public boolean isEnableGarbageCollection() {
            return enableGarbageCollection;
        }

        public void setEnableGarbageCollection(boolean enableGarbageCollection) {
            this.enableGarbageCollection = enableGarbageCollection;
        }
    }

    /**
     * Configuration for file I/O operations
     */
    public static class IOConfig {
        private String inputDirectory = "data/input/";
        private String outputDirectory = "data/output/";
        private String graphFileExtension = ".json";
        private boolean createDirectories = true;

        public String getInputDirectory() {
            return inputDirectory;
        }

        public void setInputDirectory(String inputDirectory) {
            this.inputDirectory = inputDirectory;
        }

        public String getOutputDirectory() {
            return outputDirectory;
        }

        public void setOutputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
        }

        public String getGraphFileExtension() {
            return graphFileExtension;
        }

        public void setGraphFileExtension(String graphFileExtension) {
            this.graphFileExtension = graphFileExtension;
        }

        public boolean isCreateDirectories() {
            return createDirectories;
        }

        public void setCreateDirectories(boolean createDirectories) {
            this.createDirectories = createDirectories;
        }
    }
}