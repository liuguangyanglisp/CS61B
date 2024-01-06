package bstmap;

import org.w3c.dom.Node;

import java.security.Key;
import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode node;
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;
        private int size;
        private BSTNode(K k, V v) {
            this.key = k;
            this.value = v;
            this.size = 1;
        }

    }

    public BSTMap() {

    }
    public BSTMap(K k, V v) {
        node = new BSTNode(k,v);
    }


    @Override
    /** Removes all of the mappings from this map. */
    public void clear() {
        node = null;
    }

    @Override
    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return getKey(node,key) == key;
    }

    private K getKey(BSTNode n, K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        if (n == null) {
            return null;
        }
        int cmp = key.compareTo(n.key);
        if (cmp > 0) {
            return getKey(n.right,key);
        }
        if (cmp < 0) {
            return getKey(n.left, key);
        }
        return key;
    }

    @Override
    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return getValue(node,key);
    }

    private V getValue(BSTNode n, K key) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (n == null) return null;
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            return getValue(n.left, key);
        }
        if (cmp > 0) {
            return getValue(n.right, key);
        }
        return n.value;
    }


    @Override
    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size(node);
    }

    private int size (BSTNode n) {
        if (n == null) {
            return 0;
        }
        return n.size;
    }

    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        /*if (value == null) {
            return;
        }*/
        node = put(node,key,value);
    }

    private BSTNode put(BSTNode n,K key, V value) {
        if (n == null) {
            return new BSTNode(key,value);
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            n.left = put(n.left, key, value);
        }
        if (cmp > 0) {
            n.right = put(n.right, key, value);
        }
        n.value = value;
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }


    @Override
    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
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
    public Iterator<K> iterator() {
        return null;
    }
}
