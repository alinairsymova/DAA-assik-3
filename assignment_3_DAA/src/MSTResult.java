import java.util.List;

public class MSTResult {
    List<Edge> edges;
    int totalCost;
    int operationsCount;
    double executionTimeMs;

    public MSTResult(List<Edge> edges, int totalCost, int operationsCount, double executionTimeMs) {
        this.edges = edges;
        this.totalCost = totalCost;
        this.operationsCount = operationsCount;
        this.executionTimeMs = executionTimeMs;
    }

    @Override
    public String toString() {
        return "MST edges: " + edges + "\nTotal cost: " + totalCost +
                "\nOperations: " + operationsCount + "\nExecution time: " + executionTimeMs + " ms";
    }
}
