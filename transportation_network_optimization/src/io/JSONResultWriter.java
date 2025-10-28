package io;

import model.MSTResult;
import model.Graph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Writes algorithm results to JSON files without external dependencies
 * Uses manual JSON construction
 */
public class JSONResultWriter {

    /**
     * Write MST result to file in JSON format
     */
    public void writeMSTResult(MSTResult result, String filePath) {
        try {
            String json = buildMSTResultJSON(result);
            Files.write(Paths.get(filePath), json.getBytes());
            System.out.println("MST result written to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing MST result to file: " + filePath + " - " + e.getMessage());
            // Fallback to text format
            writeTextReport(result, filePath.replace(".json", ".txt"));
        }
    }

    /**
     * Write comparison results to file
     */
    public void writeComparisonResults(List<MSTResult> results, String filePath) {
        try {
            String json = buildComparisonJSON(results);
            Files.write(Paths.get(filePath), json.getBytes());
            System.out.println("Comparison results written to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing comparison results to file: " + filePath + " - " + e.getMessage());
        }
    }

    /**
     * Write performance report to file
     */
    public void writePerformanceReport(List<FileProcessor.PerformanceData> performanceData, String filePath) {
        try {
            String json = buildPerformanceJSON(performanceData);
            Files.write(Paths.get(filePath), json.getBytes());
            System.out.println("Performance report written to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing performance report to file: " + filePath + " - " + e.getMessage());
        }
    }

    /**
     * Build JSON string for MST result
     */
    private String buildMSTResultJSON(MSTResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        // Basic result information
        json.append("  \"algorithmName\": \"").append(escapeJSON(result.getAlgorithmName())).append("\",\n");
        json.append("  \"resultId\": \"").append(escapeJSON(result.getResultId())).append("\",\n");
        json.append("  \"totalCost\": ").append(result.getTotalCost()).append(",\n");
        json.append("  \"executionTimeMs\": ").append(result.getPerformanceMetrics().getExecutionTimeMs()).append(",\n");
        json.append("  \"operationsCount\": ").append(result.getPerformanceMetrics().getOperationsCount()).append(",\n");
        json.append("  \"memoryUsedBytes\": ").append(result.getPerformanceMetrics().getMemoryUsageBytes()).append(",\n");
        json.append("  \"isValidMST\": ").append(result.isValidMST()).append(",\n");
        json.append("  \"timestamp\": \"").append(getCurrentTimestamp()).append("\",\n");

        // Performance metrics
        json.append("  \"performanceMetrics\": {\n");
        json.append("    \"executionTimeMs\": ").append(result.getPerformanceMetrics().getExecutionTimeMs()).append(",\n");
        json.append("    \"operationsCount\": ").append(result.getPerformanceMetrics().getOperationsCount()).append(",\n");
        json.append("    \"memoryUsageBytes\": ").append(result.getPerformanceMetrics().getMemoryUsageBytes()).append(",\n");
        json.append("    \"comparisonsCount\": ").append(result.getPerformanceMetrics().getComparisonsCount()).append(",\n");
        json.append("    \"unionOperations\": ").append(result.getPerformanceMetrics().getUnionOperations()).append(",\n");
        json.append("    \"priorityQueueOperations\": ").append(result.getPerformanceMetrics().getPriorityQueueOperations()).append(",\n");
        json.append("    \"operationsPerMillisecond\": ").append(result.getPerformanceMetrics().getOperationsPerMillisecond()).append("\n");
        json.append("  },\n");

        // MST properties
        json.append("  \"mstProperties\": {\n");
        json.append("    \"vertexCount\": ").append(result.getMstProperties().getVertexCount()).append(",\n");
        json.append("    \"edgeCount\": ").append(result.getMstProperties().getEdgeCount()).append(",\n");
        json.append("    \"density\": ").append(result.getMstProperties().getDensity()).append(",\n");
        json.append("    \"averageDegree\": ").append(result.getMstProperties().getAverageDegree()).append(",\n");
        json.append("    \"diameter\": ").append(result.getMstProperties().getDiameter()).append(",\n");
        json.append("    \"averageEdgeWeight\": ").append(result.getMstProperties().getAverageEdgeWeight()).append(",\n");
        json.append("    \"maxEdgeWeight\": ").append(result.getMstProperties().getMaxEdgeWeight()).append(",\n");
        json.append("    \"minEdgeWeight\": ").append(result.getMstProperties().getMinEdgeWeight()).append("\n");
        json.append("  },\n");

        // Edges array
        json.append("  \"edges\": [\n");
        List<Graph.EdgeDTO> edges = result.getGraph().getMSTEdges();
        if (edges != null && !edges.isEmpty()) {
            for (int i = 0; i < edges.size(); i++) {
                Graph.EdgeDTO edge = edges.get(i);
                json.append("    {\n");
                json.append("      \"from\": \"").append(escapeJSON(edge.getFrom())).append("\",\n");
                json.append("      \"to\": \"").append(escapeJSON(edge.getTo())).append("\",\n");
                json.append("      \"weight\": ").append(edge.getWeight()).append(",\n");
                json.append("      \"inMST\": ").append(edge.isInMST()).append("\n");
                json.append("    }");
                if (i < edges.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
        }
        json.append("  ]\n");

        json.append("}");
        return json.toString();
    }

    /**
     * Build JSON string for comparison results
     */
    private String buildComparisonJSON(List<MSTResult> results) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        json.append("  \"comparisonReport\": {\n");
        json.append("    \"timestamp\": \"").append(getCurrentTimestamp()).append("\",\n");
        json.append("    \"totalAlgorithms\": ").append(results.size()).append(",\n");

        // Algorithms array
        json.append("    \"algorithms\": [\n");
        for (int i = 0; i < results.size(); i++) {
            MSTResult result = results.get(i);
            json.append("      {\n");
            json.append("        \"name\": \"").append(escapeJSON(result.getAlgorithmName())).append("\",\n");
            json.append("        \"totalCost\": ").append(result.getTotalCost()).append(",\n");
            json.append("        \"executionTimeMs\": ").append(result.getPerformanceMetrics().getExecutionTimeMs()).append(",\n");
            json.append("        \"operationsCount\": ").append(result.getPerformanceMetrics().getOperationsCount()).append(",\n");
            json.append("        \"memoryUsedBytes\": ").append(result.getPerformanceMetrics().getMemoryUsageBytes()).append(",\n");
            json.append("        \"isValidMST\": ").append(result.isValidMST()).append(",\n");
            json.append("        \"vertexCount\": ").append(result.getMstProperties().getVertexCount()).append(",\n");
            json.append("        \"edgeCount\": ").append(result.getMstProperties().getEdgeCount()).append("\n");
            json.append("      }");
            if (i < results.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("    ],\n");

        // Find best algorithm
        String bestAlgorithm = findBestAlgorithm(results);
        json.append("    \"bestAlgorithm\": \"").append(escapeJSON(bestAlgorithm)).append("\"\n");

        json.append("  }\n");
        json.append("}");

        return json.toString();
    }

    /**
     * Build JSON string for performance data
     */
    private String buildPerformanceJSON(List<FileProcessor.PerformanceData> performanceData) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        json.append("  \"performanceReport\": {\n");
        json.append("    \"timestamp\": \"").append(getCurrentTimestamp()).append("\",\n");
        json.append("    \"testEnvironment\": \"").append(getSystemInfo()).append("\",\n");

        // Performance data array
        json.append("    \"algorithms\": [\n");
        for (int i = 0; i < performanceData.size(); i++) {
            FileProcessor.PerformanceData data = performanceData.get(i);
            json.append("      {\n");
            json.append("        \"algorithmName\": \"").append(escapeJSON(data.getAlgorithmName())).append("\",\n");
            json.append("        \"executionTime\": ").append(data.getExecutionTime()).append(",\n");
            json.append("        \"memoryUsed\": ").append(data.getMemoryUsed()).append("\n");
            json.append("      }");
            if (i < performanceData.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("    ]\n");

        json.append("  }\n");
        json.append("}");

        return json.toString();
    }

    /**
     * Find the best algorithm based on combined score
     */
    private String findBestAlgorithm(List<MSTResult> results) {
        if (results.isEmpty()) return "None";

        String bestAlgorithm = results.get(0).getAlgorithmName();
        double bestScore = Double.MAX_VALUE;

        for (MSTResult result : results) {
            // Simple scoring: cost + normalized time
            double score = result.getTotalCost() + (result.getPerformanceMetrics().getExecutionTimeMs() / 1000.0);
            if (score < bestScore) {
                bestScore = score;
                bestAlgorithm = result.getAlgorithmName();
            }
        }

        return bestAlgorithm;
    }

    /**
     * Escape JSON special characters
     */
    private String escapeJSON(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Get current timestamp
     */
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Get basic system information
     */
    private String getSystemInfo() {
        return "Java " + System.getProperty("java.version") +
                " on " + System.getProperty("os.name");
    }

    /**
     * Write simple text report as fallback
     */
    public void writeTextReport(MSTResult result, String filePath) {
        try {
            StringBuilder report = new StringBuilder();
            report.append("=== MST RESULT REPORT ===\n\n");
            report.append("Algorithm: ").append(result.getAlgorithmName()).append("\n");
            report.append("Result ID: ").append(result.getResultId()).append("\n");
            report.append("Total Cost: ").append(result.getTotalCost()).append("\n");
            report.append("Execution Time: ").append(result.getPerformanceMetrics().getExecutionTimeMs()).append(" ms\n");
            report.append("Operations Count: ").append(result.getPerformanceMetrics().getOperationsCount()).append("\n");
            report.append("Memory Used: ").append(result.getPerformanceMetrics().getMemoryUsageBytes()).append(" bytes\n");
            report.append("Valid MST: ").append(result.isValidMST()).append("\n");
            report.append("Timestamp: ").append(getCurrentTimestamp()).append("\n\n");

            report.append("MST Properties:\n");
            report.append("  Vertices: ").append(result.getMstProperties().getVertexCount()).append("\n");
            report.append("  Edges: ").append(result.getMstProperties().getEdgeCount()).append("\n");
            report.append("  Density: ").append(result.getMstProperties().getDensity()).append("\n");
            report.append("  Average Degree: ").append(result.getMstProperties().getAverageDegree()).append("\n");
            report.append("  Diameter: ").append(result.getMstProperties().getDiameter()).append("\n\n");

            report.append("MST Edges:\n");
            List<Graph.EdgeDTO> edges = result.getGraph().getMSTEdges();
            if (edges != null) {
                for (Graph.EdgeDTO edge : edges) {
                    report.append("  ").append(edge.getFrom())
                            .append(" - ").append(edge.getTo())
                            .append(" : ").append(edge.getWeight())
                            .append(edge.isInMST() ? " [IN MST]" : "").append("\n");
                }
            }

            Files.write(Paths.get(filePath), report.toString().getBytes());
            System.out.println("Text report written to: " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing text report: " + e.getMessage());
        }
    }

    /**
     * Write graph to JSON file
     */
    public void writeGraph(Graph graph, String filePath) {
        try {
            String json = buildGraphJSON(graph);
            Files.write(Paths.get(filePath), json.getBytes());
            System.out.println("Graph written to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing graph to file: " + filePath + " - " + e.getMessage());
        }
    }

    /**
     * Build JSON string for graph
     */
    private String buildGraphJSON(Graph graph) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        // Nodes array
        json.append("  \"nodes\": [\n");
        List<Graph.VertexDTO> vertices = graph.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            Graph.VertexDTO vertex = vertices.get(i);
            json.append("    {\n");
            json.append("      \"id\": \"").append(escapeJSON(vertex.getId())).append("\",\n");
            json.append("      \"degree\": ").append(vertex.getDegree()).append("\n");
            json.append("    }");
            if (i < vertices.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ],\n");

        // Edges array
        json.append("  \"edges\": [\n");
        List<Graph.EdgeDTO> edges = graph.getEdges();
        for (int i = 0; i < edges.size(); i++) {
            Graph.EdgeDTO edge = edges.get(i);
            json.append("    {\n");
            json.append("      \"from\": \"").append(escapeJSON(edge.getFrom())).append("\",\n");
            json.append("      \"to\": \"").append(escapeJSON(edge.getTo())).append("\",\n");
            json.append("      \"weight\": ").append(edge.getWeight()).append(",\n");
            json.append("      \"inMST\": ").append(edge.isInMST()).append("\n");
            json.append("    }");
            if (i < edges.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ],\n");

        // Graph properties
        json.append("  \"properties\": {\n");
        json.append("    \"vertexCount\": ").append(graph.getVerticesCount()).append(",\n");
        json.append("    \"edgeCount\": ").append(graph.getEdgesCount()).append(",\n");
        json.append("    \"density\": ").append(graph.getDensity()).append(",\n");
        json.append("    \"graphType\": \"").append(graph.getGraphType()).append("\",\n");
        json.append("    \"isConnected\": ").append(graph.isConnected()).append("\n");
        json.append("  }\n");

        json.append("}");
        return json.toString();
    }
}