package gitlet;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Tree implements Serializable {
    private TreeMap<String, String> map;
    private Set<String> removeSet;

    Tree() {
        map = new TreeMap<>();
        removeSet = new TreeSet<>();
    }

    public static Tree createTree() {
        return new Tree();
    }

    public TreeMap<String, String> getMap() {
        return this.map;
    }

    public Set<String> getRemoveSet() {
        return this.removeSet;
    }

}
