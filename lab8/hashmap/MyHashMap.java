package hashmap;


import java.lang.reflect.Array;
import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V>, Iterable<K>{

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
    // You should probably define some more!
    private int size;
    private double loadFactor;
    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);
        size = 0;
        loadFactor = 0.75;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        size = 0;
        loadFactor = 0.75;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        size = 0;
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
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        buckets = null;
        size = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return get(buckets, key) != null;
    }

    private Node get(Collection<Node>[] buckets, K key) {
        if (buckets == null) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(),buckets.length);
        if (buckets[index] == null) {
            return null;
        }
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                return n;
                }
            }
        return null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        Node keyNode = get(buckets, key);
        if (keyNode == null) {
            return null;
        }
        return keyNode.value;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key can't be null");
        }
        buckets = put(buckets,key,value);
        buckets = loadCheck();
    }

    private Collection<Node> [] put(Collection<Node> [] buckets, K key, V value) {
        Collection<Node> [] thebucktes = buckets;
        if (thebucktes == null) {
            thebucktes = createTable(16);
        }

        //what if hashCode <= 0?
        int index = Math.floorMod(key.hashCode(),buckets.length);
        if (thebucktes[index] == null) {
            thebucktes[index] = createBucket();
        }

        for (Node n : thebucktes[index]) {
            if (n.key.equals(key)) {
                n.value = value;
                return thebucktes;
            }
        }
        thebucktes[index].add(createNode(key,value));
        size += 1;
        return thebucktes;
    }
    /*if actualLoadFactior is bigger than default loadFactor.
    * Creat a newbucktes whose length is twice than before.
    * put every key-value of origin buckets into newbuckets.
    * return new buckets.
    * if else, do nothing.*/
    private Collection<Node>[] loadCheck() {
        double actualLoadFactor = size / buckets.length;
        if (actualLoadFactor > loadFactor) {
            Collection<Node>[] newBuckets = createTable(buckets.length * 2);
            size = 0;
            for (Collection<Node> bucket : buckets) {
                if (bucket == null) {
                    continue;
                }
                for (Node n : bucket) {
                    put(newBuckets, n.key, n.value);
                }
            }
            return newBuckets;
        }
        return buckets;
    }

    /** Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet(){
        Set<K> keySet = new HashSet<>();
        for (K key : this) {
            keySet.add(key);
        }
        return keySet;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new keyIterator();
    }

    private class keyIterator implements Iterator<K> {
        private int index;
        private Iterator<Node> nodeIterator;
        keyIterator() {
            index = 0;
            while (index < buckets.length && buckets[index] == null) {
                index += 1;
            }
            if (index < buckets.length) {
                nodeIterator = buckets[index].iterator();
            }
        }
        @Override
        public boolean hasNext() {
            return index < buckets.length;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new RuntimeException();
            }
            //return item
            K returnItem = nodeIterator.next().key;
            boolean has = nodeIterator.hasNext();
            //prepare for next call
            if (!nodeIterator.hasNext()) {
                index ++;
                while (index < buckets.length && buckets[index] == null) {
                    index ++;
                }
                if (index < buckets.length) {
                    nodeIterator = buckets[index].iterator();
                }
            }
            //return
            return returnItem;
        }
    }
}
