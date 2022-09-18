package gitlet;
import java.io.Serializable;
import java.util.TreeMap;

public class Tree implements Serializable {
    public TreeMap<String, String> map;

    Tree() {
        map = new TreeMap<>();
    }

    public static Tree createTree() {
        return new Tree();
    }
}
