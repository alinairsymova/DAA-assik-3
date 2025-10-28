package app;

import algorithms.KruskalAlgorithm;
import algorithms.PrimAlgorithm;
import model.Edge;
import model.Graph;
import model.MSTResult;

import java.util.*;

/**
 * Main application for Transportation Network Optimization using MST algorithms
 * Demonstrates Prim's and Kruskal's algorithms on various graph types
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("🚀 Transportation Network Optimizer - MST Algorithms");
        System.out.println("=" .repeat(50));

        // Create algorithm instances
        PrimAlgorithm prim = PrimAlgorithm.createDefault();
        KruskalAlgorithm kruskal = KruskalAlgorithm.createDefault();

        // Test with different graph types
        testSmallGraph(prim, kruskal);
        testMediumGraph(prim, kruskal);
        testDenseGraph(prim, kruskal);
        testSparseGraph(prim, kruskal);

        // Performance comparison
        compareAlgorithms(prim, kruskal);

        // Batch processing demonstration
        demonstrateBatchProcessing(prim, kruskal);
    }

    private static void testSmallGraph(PrimAlgorithm prim, KruskalAlgorithm kruskal) {
        System.out.println("\n📊 1. SMALL GRAPH (3 vertices, 3 edges)");
        System.out.println("-".repeat(40));

        Graph smallGraph = createSmallGraph();
        System.out.println("Graph: " + smallGraph);

        // Run Prim's algorithm
        MSTResult primResult = prim.computeMST(smallGraph);
        printResult("Prim", primResult);

        // Run Kruskal's algorithm
        MSTResult kruskalResult = kruskal.computeMST(smallGraph);
        printResult("Kruskal", kruskalResult);

        compareResults(primResult, kruskalResult);
    }

    private static void testMediumGraph(PrimAlgorithm prim, KruskalAlgorithm kruskal) {
        System.out.println("\n📊 2. MEDIUM GRAPH (6 vertices, 8 edges)");
        System.out.println("-".repeat(40));

        Graph mediumGraph = createMediumGraph();
        System.out.println("Graph: " + mediumGraph);

        MSTResult primResult = prim.computeMST(mediumGraph);
        printResult("Prim", primResult);

        MSTResult kruskalResult = kruskal.computeMST(mediumGraph);
        printResult("Kruskal", kruskalResult);

        compareResults(primResult, kruskalResult);
    }

    private static void testDenseGraph(PrimAlgorithm prim, KruskalAlgorithm kruskal) {
        System.out.println("\n📊 3. DENSE GRAPH (5 vertices, high density)");
        System.out.println("-".repeat(40));

        Graph denseGraph = createDenseGraph();
        System.out.println("Graph: " + denseGraph);

        // Use optimized versions for dense graphs
        PrimAlgorithm primDense = PrimAlgorithm.createOptimizedForDenseGraphs();
        KruskalAlgorithm kruskalDense = KruskalAlgorithm.createOptimizedForDenseGraphs();

        MSTResult primResult = primDense.computeMST(denseGraph);
        printResult("Prim (Dense Optimized)", primResult);

        MSTResult kruskalResult = kruskalDense.computeMST(denseGraph);
        printResult("Kruskal (Dense Optimized)", kruskalResult);

        compareResults(primResult, kruskalResult);
    }

    private static void testSparseGraph(PrimAlgorithm prim, KruskalAlgorithm kruskal) {
        System.out.println("\n📊 4. SPARSE GRAPH (7 vertices, low density)");
        System.out.println("-".repeat(40));

        Graph sparseGraph = createSparseGraph();
        System.out.println("Graph: " + sparseGraph);

        // Use optimized versions for sparse graphs
        PrimAlgorithm primSparse = PrimAlgorithm.createOptimizedForSparseGraphs();
        KruskalAlgorithm kruskalSparse = KruskalAlgorithm.createOptimizedForSparseGraphs();

        MSTResult primResult = primSparse.computeMST(sparseGraph);
        printResult("Prim (Sparse Optimized)", primResult);

        MSTResult kruskalResult = kruskalSparse.computeMST(sparseGraph);
        printResult("Kruskal (Sparse Optimized)", kruskalResult);

        compareResults(primResult, kruskalResult);
    }

    private static void compareAlgorithms(PrimAlgorithm prim, KruskalAlgorithm kruskal) {
        System.out.println("\n⚡ ALGORITHM COMPARISON ANALYSIS");
        System.out.println("=".repeat(50));

        Graph testGraph = createMediumGraph();

        System.out.println("Algorithm Characteristics:");
        System.out.printf("Prim: %s, Time: %s, Space: %s%n",
                prim.getAlgorithmName(), prim.getTimeComplexity(), prim.getSpaceComplexity());
        System.out.printf("Kruskal: %s, Time: %s, Space: %s%n",
                kruskal.getAlgorithmName(), kruskal.getTimeComplexity(), kruskal.getSpaceComplexity());

        // Analyze suitability
        System.out.println("\nGraph Suitability Analysis:");
        Map<String, Object> primAnalysis = prim.analyzeSuitability(testGraph);
        Map<String, Object> kruskalAnalysis = kruskal.analyzeSuitability(testGraph);

        System.out.printf("Prim suitable: %s, Reason: %s%n",
                primAnalysis.get("suitableForPrim"), primAnalysis.get("recommendedOptimization"));
        System.out.printf("Kruskal suitable: %s, Reason: %s%n",
                kruskalAnalysis.get("suitableForKruskal"), kruskalAnalysis.get("recommendedSortingStrategy"));
    }

    private static void demonstrateBatchProcessing(PrimAlgorithm prim, KruskalAlgorithm kruskal) {
        System.out.println("\n🔄 BATCH PROCESSING DEMONSTRATION");
        System.out.println("=".repeat(50));

        List<Graph> graphs = Arrays.asList(
                createSmallGraph(),
                createMediumGraph(),
                createDenseGraph(),
                createSparseGraph()
        );

        System.out.println("Processing " + graphs.size() + " graphs with Prim...");
        List<MSTResult> primResults = prim.computeMSTBatch(graphs);

        System.out.println("Processing " + graphs.size() + " graphs with Kruskal...");
        List<MSTResult> kruskalResults = kruskal.computeMSTBatch(graphs);

        System.out.println("\nBatch Results Summary:");
        for (int i = 0; i < graphs.size(); i++) {
            System.out.printf("Graph %d: Prim=%dms, Kruskal=%dms, Cost=%.2f%n",
                    i + 1,
                    primResults.get(i).getPerformanceMetrics().getExecutionTimeMs(),
                    kruskalResults.get(i).getPerformanceMetrics().getExecutionTimeMs(),
                    primResults.get(i).getTotalCost());
        }
    }

    private static void printResult(String algorithm, MSTResult result) {
        System.out.printf("%s Algorithm:%n", algorithm);
        System.out.printf("  • Total Cost: $%.2f%n", result.getTotalCost());
        System.out.printf("  • MST Edges: %d%n", result.getMstEdges().size());
        System.out.printf("  • Execution Time: %d ms%n", result.getPerformanceMetrics().getExecutionTimeMs());
        System.out.printf("  • Operations: %d%n", result.getPerformanceMetrics().getOperationsCount());
        System.out.printf("  • Valid MST: %s%n", result.isValidMST());

        // Print MST edges
        System.out.println("  • MST Edges:");
        for (Edge edge : result.getMstEdges()) {
            System.out.printf("      %s-%s (%.1f)%n",
                    edge.getFrom(), edge.getTo(), edge.getWeight());
        }
    }

    private static void compareResults(MSTResult primResult, MSTResult kruskalResult) {
        Map<String, Object> comparison = MSTResult.createComparisonReport(primResult, kruskalResult);

        System.out.println("Comparison:");
        System.out.printf("  • Cost Equivalent: %s%n", comparison.get("costEquivalent"));
        System.out.printf("  • Cost Difference: $%.2f%n", comparison.get("costDifference"));
        System.out.printf("  • Performance Improvement: %.1f%%%n", comparison.get("performanceImprovement"));
        System.out.printf("  • Faster Algorithm: %s%n", comparison.get("fasterAlgorithm"));
        System.out.printf("  • More Efficient: %s%n", comparison.get("moreEfficientAlgorithm"));
    }

    // Graph creation methods

    private static Graph createSmallGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 4.0)
                .addEdge("B", "C", 1.0)
                .addEdge("A", "C", 2.0)
                .build();
    }

    private static Graph createMediumGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 4.0)
                .addEdge("A", "C", 2.0)
                .addEdge("B", "C", 1.0)
                .addEdge("B", "D", 5.0)
                .addEdge("C", "D", 8.0)
                .addEdge("C", "E", 10.0)
                .addEdge("D", "E", 2.0)
                .addEdge("D", "F", 6.0)
                .addEdge("E", "F", 3.0)
                .build();
    }

    private static Graph createDenseGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 2.0)
                .addEdge("A", "C", 4.0)
                .addEdge("A", "D", 1.0)
                .addEdge("A", "E", 5.0)
                .addEdge("B", "C", 3.0)
                .addEdge("B", "D", 2.0)
                .addEdge("B", "E", 4.0)
                .addEdge("C", "D", 1.0)
                .addEdge("C", "E", 3.0)
                .addEdge("D", "E", 2.0)
                .build();
    }

    private static Graph createSparseGraph() {
        return new Graph.Builder()
                .addEdge("A", "B", 3.0)
                .addEdge("B", "C", 2.0)
                .addEdge("C", "D", 4.0)
                .addEdge("D", "E", 1.0)
                .addEdge("E", "F", 3.0)
                .addEdge("F", "G", 2.0)
                .build();
    }
}