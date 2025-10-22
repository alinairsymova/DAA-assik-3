public class Edge implements Comparable<Edge> {
    String from, to;
    int weight;

    public Edge(String from, String to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return from + " - " + to + " (" + weight + ")";
    }
}
