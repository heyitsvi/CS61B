package bstmap;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private class BSTNode {
        private K label;
        private V value;

        private int size;
        private BSTNode left;
        private BSTNode right;

        public BSTNode(){
            this.label = null;
            this.value = null;
            this.left = null;
            this.right = null;
            this.size = 0;
        }

        public BSTNode(K label, V value){
            this.label = label;
            this.value = value;
            this.left = null;
            this.right = null;
            this.size += 1;
        }

        private K label(){
            return this.label;
        }

        private void setLabel(K label) {
            this.label = label;
        }

        private V value(){
            return this.value;
        }

        private void setValue(V value){
            this.value = value;
        }

        private BSTNode left(){
            return this.left;
        }

        private BSTNode right(){
            return this.right;
        }

        private boolean isNull() {
            return (this.label == null) && (this.value == null);
        }
        private boolean isLeaf(){
            return (this.left == null) && (this.right == null);
        }

    }

    BSTNode node;

    public BSTMap() {
        node = new BSTNode();
    }

    /*public BSTMap(K key, V value){
        node = new BSTNode(key, value);
    }*/



    /**
     * Removes all the mappings from this map.
     */
    @Override
    public void clear() {
        node = null;
    }

    private boolean containsKey(BSTNode N, K key) {
        if (N == null) {
            return false;
        }
        if (N.label().equals(key)) {
            return true;
        } else if (key.compareTo(N.label()) > 0) {
            return containsKey(N.right(), key);
        } else {
            return containsKey(N.left(), key);
        }
    }

    @Override
    public boolean containsKey(K key) {

        if (node == null || node.size == 0) {
            return false;
        }

        return containsKey(node, key);
    }

    private V get(BSTNode N, K key) {
        if (N.size == 0) {
            return null;
        }
        if (key.equals(N.label())) {
            return N.value();
        } else if (key.compareTo(N.label()) > 0) {
            return get(N.right(), key);
        } else {
            return get(N.left(), key);
        }

    }
    @Override
    public V get(K key) {

        if (node == null) {
            return null;
        }
        return get(node, key);
    }

    @Override
    public int size() {
        if (node == null) {
            return 0;
        }

        return node.size;
    }

    private BSTNode put(BSTNode N, K key, V value){
        if (N == null) {
            return new BSTNode(key, value);
        }
        if (key.compareTo(N.label()) > 0) {
            N.right = put(N.right, key, value);
        } else if (key.compareTo(N.label()) < 0) {
            N.left = put(N.left, key, value);
        }

        N.size += 1;
        return N;
    }

    @Override
    public void put(K key, V value) {
        if (node.isNull()) {
            node.setLabel(key);
            node.setValue(value);
        }

        put(node, key, value);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

}
