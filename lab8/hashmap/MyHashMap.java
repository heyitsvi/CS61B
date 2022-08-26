package hashmap;


import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Vivek Singh
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int initialSize = 16;
    private double loadFactor = 0.75;
    private int size;

    private Set<K> keys = new HashSet<>();

    private int numOfBuckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = new Collection[initialSize];

        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }

        numOfBuckets = initialSize;
    }

    public MyHashMap(int initialSize) {
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }

        numOfBuckets = initialSize;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }

        numOfBuckets = initialSize;
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    // Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!


    private class getIterator implements Iterator<K>  {
        int pos = 0;
        Object[] keysArray = keys.toArray();
        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public K next() {
            if (hasNext()) {
                K key = (K) keysArray[pos];
                pos += 1;
                return key;
            }

            return null;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new getIterator();
    }

    /** Removes all of the mappings from this map. */
    public void clear(){
        for (Collection<Node> bucket : buckets) {
            bucket.clear();
        }
        size = 0;
    };

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        for (Collection<Node> bucket : buckets) {
            if (searchKey(key, bucket)) {
                return true;
            }
        }

        return false;
    };

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */

    public V get(K key) {
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                if (n.key.equals(key)) {
                    return n.value;
                }
            }
        }
        return null;
    };

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    };

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */

    private double getLoadFactor() {
        return (double) size / numOfBuckets;
    }

    //Check if the key exists in a bucket
    private boolean searchKey(K key, Collection C) {
        for (Object o : C) {
            Node v = (Node) o;
            if (v.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    private void resizeBuckets(int val) {
        int newSize = val * numOfBuckets;
        Collection<Node>[] oldBuckets = buckets;
        Collection<Node>[] resizedBuckets = new Collection[newSize];
        for (int i = 0; i < newSize; i++) {
            resizedBuckets[i] = createBucket();
        }
        buckets = resizedBuckets;
        size = 0;
        numOfBuckets = newSize;
        for (Collection<Node> bucket : oldBuckets) {
            for (Node n : bucket) {
                put(n.key, n.value);
            }
        }
    }

    public void put(K key, V value) {
        int bucketIndex = Math.floorMod(key.hashCode(), numOfBuckets);

        if (searchKey(key, buckets[bucketIndex])) {
            for (Node n : buckets[bucketIndex]) {
                if (n.key.equals(key)) {
                    n.value = value;
                }
            }
        } else {
            Node newNode = createNode(key, value);
            buckets[bucketIndex].add(newNode);
            keys.add(key);
            size += 1;
        }

        if (getLoadFactor() >= 0.75) {
            resizeBuckets(2);
        }
    };

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        return keys;
    };

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    };

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    };


}
