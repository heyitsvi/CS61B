package gitlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Paths {
    private boolean[] marked;
    private List<List<Integer>> allPaths;
    private int[] distTo;
    //private int[] edgeTo;
    private int s;

    public Paths(gitlet.GraphObj G, int s, int d) {
        this.s = s;
        marked = new boolean[G.getV()];
        distTo = new int[G.getV()];
        Arrays.fill(distTo, 10000);
        distTo[s] = 0;

        allPaths = new ArrayList<>();
        List<Integer> localPathList = new ArrayList<>();
        localPathList.add(s);

        allPaths(G, s, d, marked, localPathList);
    }

    public List<List<Integer>> allPaths() {
        return this.allPaths;
    }
    public int[] getDistances() {
        return this.distTo;
    }
    private void allPaths(gitlet.GraphObj G, int u, int w, boolean[] visited, List<Integer> list) {
        if (u == w) {
            List<Integer> res = new ArrayList<>(list);
            allPaths.add(res);
        } else {
            visited[u] = true;

            for (int i : G.adj(u)) {
                if (!visited[i]) {
                    list.add(i);
                    distTo[i] = distTo[u] + 1;
                    allPaths(G, i, w, visited, list);
                    list.remove(list.size() - 1);
                }
            }

            visited[u] = false;
        }
    }
}
