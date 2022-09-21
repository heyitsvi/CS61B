package gitlet;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Tree implements Serializable {
    public TreeMap<String, String> map;
    public Set<String> removeSet;

    Tree() {
        map = new TreeMap<>();
        removeSet = new TreeSet<>();
    }

    public static Tree createTree() {
        return new Tree();
    }
}
