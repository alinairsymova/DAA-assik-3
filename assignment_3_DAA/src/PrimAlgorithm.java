import java.util.*;

public class PrimAlgorithm {

    public static MSTResult prim(Graph graph) {
        long start = System.nanoTime();

        List<Edge> mst = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        int operations = 0;
        int totalCost = 0;

        String startNode = graph.getNodes().get(0);
        visited.add(startNode);

        while (visited.size() < graph.getNodes().size()) {
            Edge minEdge = null;
            for (Edge e : graph.getEdges()) {
                operations++;
                if (visited.contains(e.from) && !visited.contains(e.to) ||
                    visited.contains(e.to) && !visited.contains(e.from)) {
                    if (minEdge == null || e.weight < minEdge.weight) {
                        minEdge = e;
                    }
                }
            }
            if (minEdge != null) {
                mst.add(minEdge);
                totalCost += minEdge.weight;
                visited.add(minEdge.from);
                visited.add(minEdge.to);
            } else break;
        }

        long end = System.nanoTime();
        double execTime = (end - start) / 1_000_000.0;

        return new MSTResult(mst, totalCost, operations, execTime);
    }
}
