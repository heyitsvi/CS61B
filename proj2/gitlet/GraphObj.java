package gitlet;
import java.util.ArrayList;
import java.util.List;

public class GraphObj {
    private int V;
    private List<Integer>[] adj;

    public GraphObj(int V) {
        this.V = V;
        adj = new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    // Add edge from v to w
    public void addEdge(int v, int w) {
        adj[v].add(w);
    }

    public Iterable<Integer> adj(int v) {
        return adj[v];
    }

    public int getV() {
        return this.V;
    }
}
