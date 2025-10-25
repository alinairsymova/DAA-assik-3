package analysis;

import algorithms.KruskalAlgorithm;
import algorithms.PrimAlgorithm;
import algorithms.MSTAlgorithm;
import model.Graph;
import model.MSTResult;
import model.Edge;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

/**
 * Professional Report Generator for MST Algorithm Analysis
 * Generates comprehensive reports in multiple formats (JSON, HTML, Markdown, CSV)
 */
public class ReportGenerator {
    private final String outputDirectory;
    private final boolean generateVisualizations;
    private final ReportFormat defaultFormat;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public enum ReportFormat {
        JSON,
        HTML,
        MARKDOWN,
        CSV,
        TEXT
    }

    /**
     * Builder pattern for ReportGenerator configuration
     */
    public static class Builder {
        private String outputDirectory = "reports";
        private boolean generateVisualizations = true;
        private ReportFormat defaultFormat = ReportFormat.HTML;

        public Builder outputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public Builder generateVisualizations(boolean generate) {
            this.generateVisualizations = generate;
            return this;
        }

        public Builder defaultFormat(ReportFormat format) {
            this.defaultFormat = format;
            return this;
        }

        public ReportGenerator build() {
            return new ReportGenerator(this);
        }
    }

    /**
     * Private constructor
     */
    private ReportGenerator(Builder builder) {
        this.outputDirectory = builder.outputDirectory;
        this.generateVisualizations = builder.generateVisualizations;
        this.defaultFormat = builder.defaultFormat;
        createOutputDirectory();
    }

    /**
     * Generate comprehensive report for single algorithm result
     */
    public void generateReport(MSTResult result, String filename, ReportFormat format) {
        try {
            switch (format) {
                case JSON:
                    generateJSONReport(result, filename);
                    break;
                case HTML:
                    generateHTMLReport(Collections.singletonList(result), filename);
                    break;
                case MARKDOWN:
                    generateMarkdownReport(Collections.singletonList(result), filename);
                    break;
                case CSV:
                    generateCSVReport(Collections.singletonList(result), filename);
                    break;
                case TEXT:
                    generateTextReport(result, filename);
                    break;
            }
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to generate report: " + e.getMessage(), e);
        }
    }

    /**
     * Generate comparative report for multiple algorithm results
     */
    public void generateComparativeReport(List<MSTResult> results, String filename, ReportFormat format) {
        try {
            switch (format) {
                case JSON:
                    generateJSONComparativeReport(results, filename);
                    break;
                case HTML:
                    generateHTMLReport(results, filename);
                    break;
                case MARKDOWN:
                    generateMarkdownReport(results, filename);
                    break;
                case CSV:
                    generateCSVReport(results, filename);
                    break;
                case TEXT:
                    generateComparativeTextReport(results, filename);
                    break;
            }
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to generate comparative report: " + e.getMessage(), e);
        }
    }

    /**
     * Generate performance analysis report
     */
    public void generatePerformanceReport(List<MSTResult> results, String filename) {
        try {
            String htmlContent = generatePerformanceHTML(results);
            String filePath = outputDirectory + File.separator + filename + "_performance.html";
            Files.write(Paths.get(filePath), htmlContent.getBytes());
            System.out.println("Performance report generated: " + filePath);
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to generate performance report", e);
        }
    }

    /**
     * JSON Report Generation
     */
    private void generateJSONReport(MSTResult result, String filename) throws IOException {
        Map<String, Object> reportData = createJSONReportData(result);
        String jsonContent = formatJSON(reportData);
        String filePath = outputDirectory + File.separator + filename + ".json";
        Files.write(Paths.get(filePath), jsonContent.getBytes());
        System.out.println("JSON report generated: " + filePath);
    }

    private void generateJSONComparativeReport(List<MSTResult> results, String filename) throws IOException {
        Map<String, Object> reportData = createComparativeJSONData(results);
        String jsonContent = formatJSON(reportData);
        String filePath = outputDirectory + File.separator + filename + "_comparison.json";
        Files.write(Paths.get(filePath), jsonContent.getBytes());
        System.out.println("JSON comparative report generated: " + filePath);
    }

    /**
     * HTML Report Generation
     */
    private void generateHTMLReport(List<MSTResult> results, String filename) throws IOException {
        String htmlContent = generateHTMLContent(results);
        String filePath = outputDirectory + File.separator + filename + ".html";
        Files.write(Paths.get(filePath), htmlContent.getBytes());
        System.out.println("HTML report generated: " + filePath);
    }

    /**
     * Markdown Report Generation
     */
    private void generateMarkdownReport(List<MSTResult> results, String filename) throws IOException {
        String markdownContent = generateMarkdownContent(results);
        String filePath = outputDirectory + File.separator + filename + ".md";
        Files.write(Paths.get(filePath), markdownContent.getBytes());
        System.out.println("Markdown report generated: " + filePath);
    }

    /**
     * CSV Report Generation
     */
    private void generateCSVReport(List<MSTResult> results, String filename) throws IOException {
        String csvContent = generateCSVContent(results);
        String filePath = outputDirectory + File.separator + filename + ".csv";
        Files.write(Paths.get(filePath), csvContent.getBytes());
        System.out.println("CSV report generated: " + filePath);
    }

    /**
     * Text Report Generation
     */
    private void generateTextReport(MSTResult result, String filename) throws IOException {
        String textContent = generateTextContent(result);
        String filePath = outputDirectory + File.separator + filename + ".txt";
        Files.write(Paths.get(filePath), textContent.getBytes());
        System.out.println("Text report generated: " + filePath);
    }

    private void generateComparativeTextReport(List<MSTResult> results, String filename) throws IOException {
        String textContent = generateComparativeTextContent(results);
        String filePath = outputDirectory + File.separator + filename + "_comparison.txt";
        Files.write(Paths.get(filePath), textContent.getBytes());
        System.out.println("Comparative text report generated: " + filePath);
    }

    /**
     * JSON Content Generation
     */
    private Map<String, Object> createJSONReportData(MSTResult result) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("reportType", "MST Analysis");
        data.put("generatedAt", DATE_FORMAT.format(new Date()));
        data.put("algorithm", result.getAlgorithmName());
        data.put("graphStatistics", result.getGraph().getStatistics());

        // MST Results
        Map<String, Object> mstData = new LinkedHashMap<>();
        mstData.put("totalCost", result.getTotalCost());
        mstData.put("edgesCount", result.getMstEdges().size());
        mstData.put("isValid", result.isValidMST());
        mstData.put("edges", result.getMstEdges().stream()
                .map(this::edgeToMap)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
        data.put("mstResults", mstData);

        // Performance Metrics
        data.put("performance", result.getPerformanceMetrics());

        // Algorithm Parameters
        data.put("parameters", result.getParameters());

        return data;
    }

    private Map<String, Object> createComparativeJSONData(List<MSTResult> results) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("reportType", "MST Algorithm Comparison");
        data.put("generatedAt", DATE_FORMAT.format(new Date()));
        data.put("totalGraphs", results.size());

        // Results by algorithm
        Map<String, Object> algorithmResults = new LinkedHashMap<>();
        for (MSTResult result : results) {
            algorithmResults.put(result.getAlgorithmName(), createJSONReportData(result));
        }
        data.put("algorithmResults", algorithmResults);

        // Comparison metrics
        data.put("comparison", generateComparisonMetrics(results));

        return data;
    }

    /**
     * HTML Content Generation
     */
    private String generateHTMLContent(List<MSTResult> results) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='en'>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("    <title>MST Algorithm Analysis Report</title>\n");
        html.append("    <style>\n");
        html.append("        ").append(getCSSStyles()).append("\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class='container'>\n");
        html.append("        <header>\n");
        html.append("            <h1>Minimum Spanning Tree Analysis Report</h1>\n");
        html.append("            <p class='timestamp'>Generated on: ").append(DATE_FORMAT.format(new Date())).append("</p>\n");
        html.append("        </header>\n");

        if (results.size() == 1) {
            html.append(generateSingleResultHTML(results.get(0)));
        } else {
            html.append(generateComparativeHTML(results));
        }

        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    private String generateSingleResultHTML(MSTResult result) {
        StringBuilder html = new StringBuilder();

        html.append("        <div class='section'>\n");
        html.append("            <h2>Algorithm: ").append(result.getAlgorithmName()).append("</h2>\n");

        // Graph Information
        html.append("            <div class='subsection'>\n");
        html.append("                <h3>Graph Information</h3>\n");
        html.append("                ").append(generateGraphInfoHTML(result.getGraph())).append("\n");
        html.append("            </div>\n");

        // MST Results
        html.append("            <div class='subsection'>\n");
        html.append("                <h3>MST Results</h3>\n");
        html.append("                ").append(generateMSTResultsHTML(result)).append("\n");
        html.append("            </div>\n");

        // Performance Metrics
        html.append("            <div class='subsection'>\n");
        html.append("                <h3>Performance Metrics</h3>\n");
        html.append("                ").append(generatePerformanceHTML(result)).append("\n");
        html.append("            </div>\n");

        // MST Edges Table
        html.append("            <div class='subsection'>\n");
        html.append("                <h3>MST Edges</h3>\n");
        html.append("                ").append(generateEdgesTableHTML(result.getMstEdges())).append("\n");
        html.append("            </div>\n");

        html.append("        </div>\n");

        return html.toString();
    }

    private String generateComparativeHTML(List<MSTResult> results) {
        StringBuilder html = new StringBuilder();

        html.append("        <div class='section'>\n");
        html.append("            <h2>Algorithm Comparison</h2>\n");

        // Summary Table
        html.append("            <div class='subsection'>\n");
        html.append("                <h3>Performance Summary</h3>\n");
        html.append("                ").append(generateComparisonTableHTML(results)).append("\n");
        html.append("            </div>\n");

        // Detailed Results
        for (MSTResult result : results) {
            html.append("            <div class='subsection'>\n");
            html.append("                <h3>").append(result.getAlgorithmName()).append(" Details</h3>\n");
            html.append("                ").append(generateMSTResultsHTML(result)).append("\n");
            html.append("            </div>\n");
        }

        html.append("        </div>\n");

        return html.toString();
    }

    /**
     * Markdown Content Generation
     */
    private String generateMarkdownContent(List<MSTResult> results) {
        StringBuilder md = new StringBuilder();

        md.append("# Minimum Spanning Tree Analysis Report\n\n");
        md.append("**Generated on:** ").append(DATE_FORMAT.format(new Date())).append("\n\n");

        if (results.size() == 1) {
            md.append(generateSingleResultMarkdown(results.get(0)));
        } else {
            md.append(generateComparativeMarkdown(results));
        }

        return md.toString();
    }

    private String generateSingleResultMarkdown(MSTResult result) {
        StringBuilder md = new StringBuilder();

        md.append("## Algorithm: ").append(result.getAlgorithmName()).append("\n\n");

        // Graph Information
        md.append("### Graph Information\n\n");
        md.append(generateGraphInfoMarkdown(result.getGraph())).append("\n");

        // MST Results
        md.append("### MST Results\n\n");
        md.append(generateMSTResultsMarkdown(result)).append("\n");

        // Performance Metrics
        md.append("### Performance Metrics\n\n");
        md.append(generatePerformanceMarkdown(result)).append("\n");

        // MST Edges
        md.append("### MST Edges\n\n");
        md.append(generateEdgesTableMarkdown(result.getMstEdges())).append("\n");

        return md.toString();
    }

    private String generateComparativeMarkdown(List<MSTResult> results) {
        StringBuilder md = new StringBuilder();

        md.append("## Algorithm Comparison\n\n");

        // Summary Table
        md.append("### Performance Summary\n\n");
        md.append(generateComparisonTableMarkdown(results)).append("\n");

        // Detailed Results
        for (MSTResult result : results) {
            md.append("### ").append(result.getAlgorithmName()).append(" Details\n\n");
            md.append(generateMSTResultsMarkdown(result)).append("\n");
        }

        return md.toString();
    }

    /**
     * CSV Content Generation
     */
    private String generateCSVContent(List<MSTResult> results) {
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("Algorithm,TotalCost,ExecutionTimeMs,OperationsCount,VerticesCount,EdgesCount,IsValid\n");

        // Data rows
        for (MSTResult result : results) {
            csv.append(String.format("\"%s\",%.2f,%d,%d,%d,%d,%s\n",
                    result.getAlgorithmName(),
                    result.getTotalCost(),
                    result.getPerformanceMetrics().getExecutionTimeMs(),
                    result.getPerformanceMetrics().getOperationsCount(),
                    result.getGraph().getVerticesCount(),
                    result.getGraph().getEdgesCount(),
                    result.isValidMST()));
        }

        return csv.toString();
    }

    /**
     * Text Content Generation
     */
    private String generateTextContent(MSTResult result) {
        StringBuilder text = new StringBuilder();

        text.append("MINIMUM SPANNING TREE ANALYSIS REPORT\n");
        text.append("=====================================\n\n");
        text.append("Generated: ").append(DATE_FORMAT.format(new Date())).append("\n\n");

        text.append("ALGORITHM: ").append(result.getAlgorithmName()).append("\n");
        text.append("Time Complexity: ").append(result.getAlgorithmName().equals("Kruskal") ? "O(E log E)" : "O(E log V)").append("\n\n");

        text.append("GRAPH INFORMATION:\n");
        text.append("  Vertices: ").append(result.getGraph().getVerticesCount()).append("\n");
        text.append("  Edges: ").append(result.getGraph().getEdgesCount()).append("\n");
        text.append("  Density: ").append(String.format("%.4f", result.getGraph().getDensity())).append("\n\n");

        text.append("MST RESULTS:\n");
        text.append("  Total Cost: ").append(String.format("%.2f", result.getTotalCost())).append("\n");
        text.append("  MST Edges: ").append(result.getMstEdges().size()).append("\n");
        text.append("  Valid MST: ").append(result.isValidMST() ? "Yes" : "No").append("\n\n");

        text.append("PERFORMANCE METRICS:\n");
        text.append("  Execution Time: ").append(result.getPerformanceMetrics().getExecutionTimeMs()).append(" ms\n");
        text.append("  Operations Count: ").append(result.getPerformanceMetrics().getOperationsCount()).append("\n");
        text.append("  Memory Usage: ").append(result.getPerformanceMetrics().getMemoryUsageBytes()).append(" bytes\n\n");

        text.append("MST EDGES:\n");
        for (Edge edge : result.getMstEdges()) {
            text.append(String.format("  %s -- %s (weight: %.2f)\n",
                    edge.getFrom(), edge.getTo(), edge.getWeight()));
        }

        return text.toString();
    }

    private String generateComparativeTextContent(List<MSTResult> results) {
        StringBuilder text = new StringBuilder();

        text.append("MST ALGORITHM COMPARISON REPORT\n");
        text.append("===============================\n\n");
        text.append("Generated: ").append(DATE_FORMAT.format(new Date())).append("\n\n");

        text.append("SUMMARY:\n");
        text.append(String.format("%-12s %-12s %-12s %-12s %-8s\n",
                "Algorithm", "Cost", "Time(ms)", "Operations", "Valid"));
        text.append(String.format("%-12s %-12s %-12s %-12s %-8s\n",
                "--------", "----", "--------", "---------", "-----"));

        for (MSTResult result : results) {
            text.append(String.format("%-12s %-12.2f %-12d %-12d %-8s\n",
                    result.getAlgorithmName(),
                    result.getTotalCost(),
                    result.getPerformanceMetrics().getExecutionTimeMs(),
                    result.getPerformanceMetrics().getOperationsCount(),
                    result.isValidMST() ? "Yes" : "No"));
        }

        text.append("\nDETAILED RESULTS:\n");
        text.append("================\n\n");

        for (MSTResult result : results) {
            text.append(result.getAlgorithmName()).append(":\n");
            text.append("  Total Cost: ").append(String.format("%.2f", result.getTotalCost())).append("\n");
            text.append("  Execution Time: ").append(result.getPerformanceMetrics().getExecutionTimeMs()).append(" ms\n");
            text.append("  Operations: ").append(result.getPerformanceMetrics().getOperationsCount()).append("\n");
            text.append("  Memory: ").append(result.getPerformanceMetrics().getMemoryUsageBytes()).append(" bytes\n");
            text.append("  Valid: ").append(result.isValidMST()).append("\n\n");
        }

        return text.toString();
    }

    /**
     * Performance HTML for detailed performance analysis
     */
    private String generatePerformanceHTML(List<MSTResult> results) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='en'>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("    <title>MST Performance Analysis</title>\n");
        html.append("    <script src='https://cdn.jsdelivr.net/npm/chart.js'></script>\n");
        html.append("    <style>\n");
        html.append("        ").append(getCSSStyles()).append("\n");
        html.append("        .chart-container { margin: 20px 0; height: 400px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class='container'>\n");
        html.append("        <h1>MST Algorithm Performance Analysis</h1>\n");
        html.append("        <p>Generated on: ").append(DATE_FORMAT.format(new Date())).append("</p>\n");

        // Performance Charts
        html.append("        <div class='chart-container'>\n");
        html.append("            <canvas id='executionTimeChart'></canvas>\n");
        html.append("        </div>\n");
        html.append("        <div class='chart-container'>\n");
        html.append("            <canvas id='operationsChart'></canvas>\n");
        html.append("        </div>\n");

        html.append("    </div>\n");

        // JavaScript for charts
        html.append("    <script>\n");
        html.append(generateChartJS(results));
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Helper methods for content generation
     */
    private String generateGraphInfoHTML(Graph graph) {
        Map<String, Object> stats = graph.getStatistics();
        return String.format(
                "<div class='info-grid'>" +
                        "<div><strong>Vertices:</strong> %d</div>" +
                        "<div><strong>Edges:</strong> %d</div>" +
                        "<div><strong>Density:</strong> %.4f</div>" +
                        "<div><strong>Type:</strong> %s</div>" +
                        "</div>",
                stats.get("vertices"), stats.get("edges"), stats.get("density"), stats.get("graphType")
        );
    }

    private String generateMSTResultsHTML(MSTResult result) {
        return String.format(
                "<div class='info-grid'>" +
                        "<div><strong>Total Cost:</strong> %.2f</div>" +
                        "<div><strong>MST Edges:</strong> %d</div>" +
                        "<div><strong>Valid MST:</strong> %s</div>" +
                        "<div><strong>Execution Time:</strong> %d ms</div>" +
                        "</div>",
                result.getTotalCost(), result.getMstEdges().size(),
                result.isValidMST() ? "Yes" : "No",
                result.getPerformanceMetrics().getExecutionTimeMs()
        );
    }

    private String generatePerformanceHTML(MSTResult result) {
        return String.format(
                "<div class='info-grid'>" +
                        "<div><strong>Operations:</strong> %d</div>" +
                        "<div><strong>Comparisons:</strong> %d</div>" +
                        "<div><strong>Memory Usage:</strong> %d bytes</div>" +
                        "<div><strong>Operations/ms:</strong> %.2f</div>" +
                        "</div>",
                result.getPerformanceMetrics().getOperationsCount(),
                result.getPerformanceMetrics().getComparisonsCount(),
                result.getPerformanceMetrics().getMemoryUsageBytes(),
                result.getPerformanceMetrics().getOperationsPerMillisecond()
        );
    }

    private String generateEdgesTableHTML(List<Edge> edges) {
        StringBuilder html = new StringBuilder();
        html.append("<table class='data-table'>\n");
        html.append("<thead><tr><th>From</th><th>To</th><th>Weight</th></tr></thead>\n");
        html.append("<tbody>\n");
        for (Edge edge : edges) {
            html.append(String.format("<tr><td>%s</td><td>%s</td><td>%.2f</td></tr>\n",
                    edge.getFrom(), edge.getTo(), edge.getWeight()));
        }
        html.append("</tbody>\n");
        html.append("</table>\n");
        return html.toString();
    }

    private String generateComparisonTableHTML(List<MSTResult> results) {
        StringBuilder html = new StringBuilder();
        html.append("<table class='data-table'>\n");
        html.append("<thead><tr><th>Algorithm</th><th>Cost</th><th>Time (ms)</th><th>Operations</th><th>Memory</th><th>Valid</th></tr></thead>\n");
        html.append("<tbody>\n");
        for (MSTResult result : results) {
            html.append(String.format("<tr><td>%s</td><td>%.2f</td><td>%d</td><td>%d</td><td>%d</td><td>%s</td></tr>\n",
                    result.getAlgorithmName(),
                    result.getTotalCost(),
                    result.getPerformanceMetrics().getExecutionTimeMs(),
                    result.getPerformanceMetrics().getOperationsCount(),
                    result.getPerformanceMetrics().getMemoryUsageBytes(),
                    result.isValidMST() ? "Yes" : "No"));
        }
        html.append("</tbody>\n");
        html.append("</table>\n");
        return html.toString();
    }

    // Markdown helper methods
    private String generateGraphInfoMarkdown(Graph graph) {
        Map<String, Object> stats = graph.getStatistics();
        return String.format(
                "- **Vertices:** %d\n- **Edges:** %d\n- **Density:** %.4f\n- **Type:** %s",
                stats.get("vertices"), stats.get("edges"), stats.get("density"), stats.get("graphType")
        );
    }

    private String generateMSTResultsMarkdown(MSTResult result) {
        return String.format(
                "- **Total Cost:** %.2f\n- **MST Edges:** %d\n- **Valid MST:** %s\n- **Execution Time:** %d ms",
                result.getTotalCost(), result.getMstEdges().size(),
                result.isValidMST() ? "Yes" : "No",
                result.getPerformanceMetrics().getExecutionTimeMs()
        );
    }

    private String generatePerformanceMarkdown(MSTResult result) {
        return String.format(
                "- **Operations:** %d\n- **Comparisons:** %d\n- **Memory Usage:** %d bytes\n- **Operations/ms:** %.2f",
                result.getPerformanceMetrics().getOperationsCount(),
                result.getPerformanceMetrics().getComparisonsCount(),
                result.getPerformanceMetrics().getMemoryUsageBytes(),
                result.getPerformanceMetrics().getOperationsPerMillisecond()
        );
    }

    private String generateEdgesTableMarkdown(List<Edge> edges) {
        StringBuilder md = new StringBuilder();
        md.append("| From | To | Weight |\n");
        md.append("|------|----|--------|\n");
        for (Edge edge : edges) {
            md.append(String.format("| %s | %s | %.2f |\n",
                    edge.getFrom(), edge.getTo(), edge.getWeight()));
        }
        return md.toString();
    }

    private String generateComparisonTableMarkdown(List<MSTResult> results) {
        StringBuilder md = new StringBuilder();
        md.append("| Algorithm | Cost | Time (ms) | Operations | Memory | Valid |\n");
        md.append("|-----------|------|-----------|------------|--------|-------|\n");
        for (MSTResult result : results) {
            md.append(String.format("| %s | %.2f | %d | %d | %d | %s |\n",
                    result.getAlgorithmName(),
                    result.getTotalCost(),
                    result.getPerformanceMetrics().getExecutionTimeMs(),
                    result.getPerformanceMetrics().getOperationsCount(),
                    result.getPerformanceMetrics().getMemoryUsageBytes(),
                    result.isValidMST() ? "Yes" : "No"));
        }
        return md.toString();
    }

    // JSON helper methods
    private Map<String, Object> edgeToMap(Edge edge) {
        Map<String, Object> edgeMap = new LinkedHashMap<>();
        edgeMap.put("from", edge.getFrom());
        edgeMap.put("to", edge.getTo());
        edgeMap.put("weight", edge.getWeight());
        edgeMap.put("inMST", edge.isInMST());
        return edgeMap;
    }

    private Map<String, Object> generateComparisonMetrics(List<MSTResult> results) {
        Map<String, Object> comparison = new LinkedHashMap<>();

        // Find best performing algorithm
        MSTResult fastest = results.stream()
                .min(Comparator.comparing(r -> r.getPerformanceMetrics().getExecutionTimeMs()))
                .orElse(results.get(0));

        MSTResult mostEfficient = results.stream()
                .min(Comparator.comparing(r -> r.getPerformanceMetrics().getOperationsCount()))
                .orElse(results.get(0));

        comparison.put("fastestAlgorithm", fastest.getAlgorithmName());
        comparison.put("mostEfficientAlgorithm", mostEfficient.getAlgorithmName());
        comparison.put("recommendedAlgorithm", fastest.getAlgorithmName()); // Simple recommendation

        return comparison;
    }

    private String formatJSON(Map<String, Object> data) {
        // Simple JSON formatting (in real implementation, use Jackson or Gson)
        return data.toString().replace("=", ": ").replace(", ", ",\n    ");
    }

    private String generateChartJS(List<MSTResult> results) {
        StringBuilder js = new StringBuilder();

        // Execution Time Chart
        js.append("const timeCtx = document.getElementById('executionTimeChart').getContext('2d');\n");
        js.append("new Chart(timeCtx, {\n");
        js.append("    type: 'bar',\n");
        js.append("    data: {\n");
        js.append("        labels: ").append(getAlgorithmNames(results)).append(",\n");
        js.append("        datasets: [{\n");
        js.append("            label: 'Execution Time (ms)',\n");
        js.append("            data: ").append(getExecutionTimes(results)).append(",\n");
        js.append("            backgroundColor: 'rgba(54, 162, 235, 0.5)'\n");
        js.append("        }]\n");
        js.append("    }\n");
        js.append("});\n");

        // Operations Chart
        js.append("const opsCtx = document.getElementById('operationsChart').getContext('2d');\n");
        js.append("new Chart(opsCtx, {\n");
        js.append("    type: 'bar',\n");
        js.append("    data: {\n");
        js.append("        labels: ").append(getAlgorithmNames(results)).append(",\n");
        js.append("        datasets: [{\n");
        js.append("            label: 'Operations Count',\n");
        js.append("            data: ").append(getOperationsCounts(results)).append(",\n");
        js.append("            backgroundColor: 'rgba(255, 99, 132, 0.5)'\n");
        js.append("        }]\n");
        js.append("    }\n");
        js.append("});\n");

        return js.toString();
    }

    // Utility methods
    private String getAlgorithmNames(List<MSTResult> results) {
        List<String> names = new ArrayList<>();
        for (MSTResult result : results) {
            names.add("\"" + result.getAlgorithmName() + "\"");
        }
        return "[" + String.join(", ", names) + "]";
    }

    private String getExecutionTimes(List<MSTResult> results) {
        List<String> times = new ArrayList<>();
        for (MSTResult result : results) {
            times.add(String.valueOf(result.getPerformanceMetrics().getExecutionTimeMs()));
        }
        return "[" + String.join(", ", times) + "]";
    }

    private String getOperationsCounts(List<MSTResult> results) {
        List<String> counts = new ArrayList<>();
        for (MSTResult result : results) {
            counts.add(String.valueOf(result.getPerformanceMetrics().getOperationsCount()));
        }
        return "[" + String.join(", ", counts) + "]";
    }

    private String getCSSStyles() {
        return """
            body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
            .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            header { border-bottom: 2px solid #333; margin-bottom: 30px; padding-bottom: 15px; }
            h1 { color: #333; margin: 0; }
            .timestamp { color: #666; font-style: italic; }
            .section { margin-bottom: 30px; }
            .subsection { margin-bottom: 20px; padding: 15px; background: #f9f9f9; border-radius: 5px; }
            h2 { color: #444; border-bottom: 1px solid #ddd; padding-bottom: 5px; }
            h3 { color: #555; margin-top: 0; }
            .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 15px 0; }
            .info-grid div { padding: 10px; background: white; border-radius: 4px; border-left: 4px solid #007cba; }
            .data-table { width: 100%; border-collapse: collapse; margin: 15px 0; }
            .data-table th, .data-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
            .data-table th { background-color: #f2f2f2; font-weight: bold; }
            .data-table tr:hover { background-color: #f5f5f5; }
            """;
    }

    private void createOutputDirectory() {
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Custom exception for report generation errors
     */
    public static class ReportGenerationException extends RuntimeException {
        public ReportGenerationException(String message) {
            super(message);
        }

        public ReportGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Static factory methods
    public static ReportGenerator createDefault() {
        return new Builder().build();
    }

    public static ReportGenerator createWithDirectory(String directory) {
        return new Builder().outputDirectory(directory).build();
    }
}