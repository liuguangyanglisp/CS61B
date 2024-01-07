package bstmap;

import com.google.common.base.Strings;
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
        private boolean isred;
        private BSTNode(K k, V v) {
            this.key = k;
            this.value = v;
            this.size = 1;
            this.isred = true;
        }

    }

    public BSTMap() {
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
        node = put(node,key,value);
        if (node.isred) {
            node.isred = false;
        }
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
        if (cmp == 0) {
            n.value = value;
        }

        n.size = 1 + size(n.left) + size(n.right);
        if (isRed(n.right) && !isRed(n.left)) {
           n =  rotateLeft(n);
        }
        if (isRed(n.left) && isRed(n.left.left)) {
            n = rotateRight(n);
        }
        if (isRed(n.left) && isRed(n.right)) {
            flipColor(n);
        }
        return n;
    }


    @Override
    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
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

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        inorderPrint(this.node);
    }

    private void inorderPrint(BSTNode n) {
        if (n == null) return;
        inorderPrint(n.left);
        System.out.print(n.key + " ");
        inorderPrint(n.right);
    }

    private BSTNode getNode (BSTNode n, K k) {
        if (n == null) return null;
        if (k == null) {
            throw new IllegalArgumentException();
        }
        int cmp = k.compareTo(n.key);
        if (cmp > 0) {
            return getNode(n.right, k);
        }
        if (cmp < 0) {
            return getNode(n.left, k);
        }
        return n;
    }




    private BSTNode findParent (BSTNode upNode,BSTNode downNode) {
        if (upNode == null) return null;
        if (downNode == null) {
            throw new IllegalArgumentException();
        }
        if (upNode.right == downNode || upNode.left == downNode) return upNode;
        int cmp = downNode.key.compareTo(upNode.key);
        if (cmp > 0) {
            return findParent(upNode.right, downNode);
        }
        if (cmp < 0) {
            return findParent(upNode.left, downNode);
        }
        return null;

    }

    private BSTNode rotateLeft (BSTNode n) {
        if (n != null && n.right != null) return n;

            BSTNode parent = findParent(this.node, n);
            BSTNode newRoot = n.right;
            n.right = newRoot.left;
            newRoot.left = n;
            n.size = 1 + size(n.left) + size(n.right);
            newRoot.size = 1 + size(newRoot.left) + size(newRoot.right);

            if (parent == null) {
                node = newRoot;
            } else {
                int cmp = newRoot.key.compareTo(parent.key);
                if (cmp > 0) {
                    parent.right = newRoot;
                }
                if (cmp < 0) {
                    parent.left = newRoot;
                }
                if (cmp == 0) {
                    throw new RuntimeException("parent node and child node have same Key");
                }
                parent.size = 1 + size(parent.left) + size(parent.right);
            }
            return newRoot;
        }

    private BSTNode rotateRight (BSTNode n) {
        if (n == null || n.left == null) return n;

            BSTNode parent = findParent(this.node, n);
            BSTNode newRoot = n.left;
            n.left = newRoot.right;
            newRoot.right = n;
            n.size = 1 + size(n.left) + size(n.right);
            newRoot.size = 1 + size(newRoot.left) + size(newRoot.right);

            if (parent == null) {
                node = newRoot;
            } else {
                int cmp = newRoot.key.compareTo(parent.key);
                if (cmp > 0) {
                    parent.right = newRoot;
                }
                if (cmp < 0) {
                    parent.left = newRoot;
                }
                if (cmp == 0) {
                    throw new RuntimeException("parent node and child node have same Key");
                }
                parent.size = 1 + size(parent.left) + size(parent.right);
            }
        return newRoot;
        }

    private void flipColor (BSTNode n) {
        if (n != node) {
            node.isred = true;
        }
        n.left.isred = false;
        n.right.isred = false;
    }

    private boolean isRed (BSTNode n) {
        if (n == null) return false;
        return n.isred;
    }
    public static void main(String[] args) {
        BSTMap m = new BSTMap();
        m.put(1,1);
        m.put(2,2);
        m.put(3,3);
        m.put(4,4);
        m.put(5,5);
        m.put(6,6);
        m.rotateLeft(m.getNode(m.node,3));
        /*m.put("B","B");
        m.put("A","A");
        m.put("E","E");
        m.put("S","S");
        m.put("Z","Z");
        m.put("G","G");*/
        m.printInOrder();

    }
}
