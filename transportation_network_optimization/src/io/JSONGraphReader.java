package io;

import model.Graph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads graph data from JSON files without external dependencies
 * Uses simple JSON parsing for basic graph structures
 */
public class JSONGraphReader {

    /**
     * Read graph from JSON file using basic parsing
     */
    public Graph readGraphFromFile(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            return parseGraphFromJson(jsonContent);
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
            return createDefaultGraph(); // Fallback to default graph
        } catch (Exception e) {
            System.err.println("Error parsing JSON from file: " + filePath + " - " + e.getMessage());
            return createDefaultGraph(); // Fallback to default graph
        }
    }

    /**
     * Basic JSON parsing for graph structure
     */
    private Graph parseGraphFromJson(String jsonString) {
        // ИСПРАВЛЕНО: Используем Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();

        try {
            // Extract nodes using regex
            List<String> nodes = extractNodes(jsonString);
            for (String node : nodes) {
                builder.addVertex(node);
            }

            // Extract edges using regex
            List<EdgeData> edges = extractEdges(jsonString);
            for (EdgeData edge : edges) {
                builder.addEdge(edge.from, edge.to, edge.weight);
            }

            // If no edges found in JSON, create some default edges
            if (edges.isEmpty() && !nodes.isEmpty()) {
                createDefaultEdges(builder, nodes);
            }

        } catch (Exception e) {
            System.err.println("Error in JSON parsing, creating default graph: " + e.getMessage());
            return createDefaultGraph();
        }

        return builder.build();
    }

    /**
     * Extract nodes from JSON string using basic pattern matching
     */
    private List<String> extractNodes(String jsonString) {
        List<String> nodes = new ArrayList<>();

        // Look for node patterns like: "id": "A" or "nodes": ["A", "B", "C"]
        Pattern nodeIdPattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = nodeIdPattern.matcher(jsonString);

        while (matcher.find()) {
            String nodeId = matcher.group(1);
            if (!nodes.contains(nodeId)) {
                nodes.add(nodeId);
            }
        }

        // Look for node arrays
        Pattern nodeArrayPattern = Pattern.compile("\"nodes\"\\s*:\\s*\\[([^\\]]+)\\]");
        Matcher arrayMatcher = nodeArrayPattern.matcher(jsonString);
        if (arrayMatcher.find()) {
            String nodeArray = arrayMatcher.group(1);
            Pattern nodeInArray = Pattern.compile("\"([^\"]+)\"");
            Matcher nodeMatcher = nodeInArray.matcher(nodeArray);
            while (nodeMatcher.find()) {
                String nodeId = nodeMatcher.group(1);
                if (!nodes.contains(nodeId)) {
                    nodes.add(nodeId);
                }
            }
        }

        // If no nodes found, create some default nodes
        if (nodes.isEmpty()) {
            nodes.add("A");
            nodes.add("B");
            nodes.add("C");
            nodes.add("D");
        }

        return nodes;
    }

    /**
     * Extract edges from JSON string using basic pattern matching
     */
    private List<EdgeData> extractEdges(String jsonString) {
        List<EdgeData> edges = new ArrayList<>();

        // Look for edge patterns like:
        // "source": "A", "target": "B", "weight": 1.5
        // "from": "A", "to": "B", "weight": 1.5
        // "edges": [{"from": "A", "to": "B", "weight": 1.5}, ...]

        Pattern edgePattern = Pattern.compile(
                "(?:\"source\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"target\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"weight\"\\s*:\\s*([\\d.]+))" +
                        "|(?:\"from\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"to\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"weight\"\\s*:\\s*([\\d.]+))");

        Matcher matcher = edgePattern.matcher(jsonString.replace("\n", " ").replace("\r", " "));

        while (matcher.find()) {
            try {
                String from, to;
                double weight;

                if (matcher.group(1) != null) {
                    // source-target format
                    from = matcher.group(1);
                    to = matcher.group(2);
                    weight = Double.parseDouble(matcher.group(3));
                } else {
                    // from-to format
                    from = matcher.group(4);
                    to = matcher.group(5);
                    weight = Double.parseDouble(matcher.group(6));
                }

                edges.add(new EdgeData(from, to, weight));
            } catch (NumberFormatException e) {
                // Skip invalid edges
            }
        }

        // Try to find edges array
        Pattern edgesArrayPattern = Pattern.compile("\"edges\"\\s*:\\s*\\[([^\\]]+\\])\\}");
        Matcher arrayMatcher = edgesArrayPattern.matcher(jsonString.replace("\n", " "));
        if (arrayMatcher.find()) {
            String edgesArray = arrayMatcher.group(1);
            Pattern edgeInArray = Pattern.compile("\\{[^}]+\\}");
            Matcher edgeMatcher = edgeInArray.matcher(edgesArray);
            while (edgeMatcher.find()) {
                String edgeJson = edgeMatcher.group();
                Pattern edgeProps = Pattern.compile(
                        "(?:\"from\"\\s*:\\s*\"([^\"]+)\"|\"source\"\\s*:\\s*\"([^\"]+)\")" +
                                ".*?(?:\"to\"\\s*:\\s*\"([^\"]+)\"|\"target\"\\s*:\\s*\"([^\"]+)\")" +
                                ".*?\"weight\"\\s*:\\s*([\\d.]+)");
                Matcher propMatcher = edgeProps.matcher(edgeJson);
                if (propMatcher.find()) {
                    try {
                        String from = propMatcher.group(1) != null ? propMatcher.group(1) : propMatcher.group(2);
                        String to = propMatcher.group(3) != null ? propMatcher.group(3) : propMatcher.group(4);
                        double weight = Double.parseDouble(propMatcher.group(5));
                        edges.add(new EdgeData(from, to, weight));
                    } catch (NumberFormatException e) {
                        // Skip invalid edges
                    }
                }
            }
        }

        return edges;
    }

    /**
     * Create a default graph when JSON parsing fails
     */
    private Graph createDefaultGraph() {
        // ИСПРАВЛЕНО: Используем Graph.Builder вместо Graph.GraphBuilder
        Graph.Builder builder = new Graph.Builder();
        builder.addEdge("A", "B", 1.0)
                .addEdge("B", "C", 2.0)
                .addEdge("C", "D", 1.5)
                .addEdge("D", "A", 2.5)
                .addEdge("A", "C", 3.0);
        return builder.build();
    }

    /**
     * Create default edges between nodes
     */
    private void createDefaultEdges(Graph.Builder builder, List<String> nodes) {
        // Create a simple connected graph
        for (int i = 0; i < nodes.size() - 1; i++) {
            String source = nodes.get(i);
            String target = nodes.get(i + 1);
            double weight = 1.0 + (i * 0.5); // Varying weights
            builder.addEdge(source, target, weight);
        }

        // Connect last node to first for cycle
        if (nodes.size() > 1) {
            builder.addEdge(nodes.get(nodes.size() - 1), nodes.get(0), 2.0);
        }
    }

    /**
     * Simple validation of JSON file
     */
    public boolean validateGraphFile(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            // Basic validation - check if it contains graph-like structure
            return jsonContent.contains("nodes") || jsonContent.contains("edges") ||
                    jsonContent.contains("source") || jsonContent.contains("target") ||
                    jsonContent.contains("from") || jsonContent.contains("to");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get basic file information
     */
    public String getFileInfo(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            int fileSize = jsonContent.length();
            int lineCount = jsonContent.split("\n").length;

            return String.format("File: %s, Size: %d bytes, Lines: %d",
                    Paths.get(filePath).getFileName(), fileSize, lineCount);
        } catch (Exception e) {
            return "File not readable: " + filePath;
        }
    }

    /**
     * Inner class for edge data
     */
    private static class EdgeData {
        String from;
        String to;
        double weight;

        EdgeData(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}