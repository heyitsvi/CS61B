package gitlet;
import java.util.ArrayList;
import java.util.List;

public class GraphObj {
    /** Number of vertices in the graph **/
    private int V;
    /** Adjacency List containing all the neighbours of a node **/
    private List<Integer>[] adj;
    /** Graph object constructor **/
    public GraphObj(int V) {
        this.V = V;
        adj = new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    /** Add an edge from v to w. **/
    public void addEdge(int v, int w) {
        adj[v].add(w);
    }
    /** Get the Iterator to iter over all the neighbours of a node. **/
    public Iterable<Integer> adj(int v) {
        return adj[v];
    }
    /** Get the total number of vertices in the Graph. **/
    public int getV() {
        return this.V;
    }
}
