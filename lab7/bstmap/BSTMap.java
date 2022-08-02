package bstmap;


import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode {
        private K label;
        private V value;
        private BSTNode left;
        private BSTNode right;

        public BSTNode(K label, V value){
            this.label = label;
            this.value = value;
            this.left = null;
            this.right = null;
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

        private boolean isLeaf(){
            return (this.left == null) && (this.right == null);
        }

    }

    BSTNode node;

    public BSTMap(K key, V value){
        node = new BSTNode(key, value);
    }



    /**
     * Removes all the mappings from this map.
     */
    @Override
    public void clear() {

    }

    @Override
    public boolean containsKey(K key) {
        while (!node.isLeaf()){
            if (node.label().equals(key)){
                return true;
            } else if (key.compareTo(node.label()) > 0) {
                node = node.right();
            } else {
                node = node.left();
            }
        }

        return false;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void put(K key, V value) {
        while (!node.isLeaf()) {
            if (node == null){
                node.setLabel(key);
                node.setValue(value);
            }
        }
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }


    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public void forEach(Consumer action) {
        throw new UnsupportedOperationException();
        //Map61B.super.forEach(action);
    }

    @Override
    public Spliterator spliterator() {
        throw new UnsupportedOperationException();
        //return Map61B.super.spliterator();
    }

    public static void main(String[] args) {
        BSTMap<String, String> m = new BSTMap<>("d", "root node");
    }

}
