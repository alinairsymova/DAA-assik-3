import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Graph 1
        List<String> nodes1 = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges1 = Arrays.asList(
                new Edge("A", "B", 4),
                new Edge("A", "C", 3),
                new Edge("B", "C", 2),
                new Edge("B", "D", 5),
                new Edge("C", "D", 7),
                new Edge("C", "E", 8),
                new Edge("D", "E", 6)
        );
        Graph graph1 = new Graph(nodes1, edges1);

        MSTResult primResult = PrimAlgorithm.prim(graph1);
        MSTResult kruskalResult = KruskalAlgorithm.kruskal(graph1);

        System.out.println("===== Graph 1 =====");
        System.out.println("Prim’s Algorithm:\n" + primResult);
        System.out.println("Kruskal’s Algorithm:\n" + kruskalResult);
    }
}
