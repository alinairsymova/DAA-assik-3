import java.util.*;

public class KruskalAlgorithm {

    static class UnionFind {
        private final Map<String, String> parent = new HashMap<>();

        public String find(String node) {
            if (!parent.containsKey(node)) parent.put(node, node);
            if (!parent.get(node).equals(node))
                parent.put(node, find(parent.get(node)));
            return parent.get(node);
        }

        public void union(String a, String b) {
            parent.put(find(a), find(b));
        }
    }

    public static MSTResult kruskal(Graph graph) {
        long start = System.nanoTime();

        List<Edge> mst = new ArrayList<>();
        int totalCost = 0;
        int operations = 0;

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);

        UnionFind uf = new UnionFind();

        for (Edge e : edges) {
            operations++;
            String rootA = uf.find(e.from);
            String rootB = uf.find(e.to);

            if (!rootA.equals(rootB)) {
                uf.union(rootA, rootB);
                mst.add(e);
                totalCost += e.weight;
            }
        }

        long end = System.nanoTime();
        double execTime = (end - start) / 1_000_000.0;

        return new MSTResult(mst, totalCost, operations, execTime);
    }
}
