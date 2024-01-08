package bstmap;

import com.google.common.base.Strings;
import org.w3c.dom.Node;

import java.security.Key;
import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode node;
    private static int colorSet;

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
        return getKey(node, key) == key;
    }

    private K getKey(BSTNode n, K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls getKey(n,key) with a null key");
        }
        if (n == null) {
            return null;
        }
        int cmp = key.compareTo(n.key);
        if (cmp > 0) {
            return getKey(n.right, key);
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
        return getValue(node, key);
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

    private int size(BSTNode n) {
        if (n == null) {
            return 0;
        }
        return n.size;
    }

    private int updateSize(BSTNode n) {
        return (1 + size(n.left) + size(n.right));
    }

    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        node = put(node, key, value);
        node.isred = false;
    }

    private BSTNode put(BSTNode n, K key, V value) {
        if (n == null) {
            return new BSTNode(key, value);
        }
        if (value == null) {
            throw new IllegalArgumentException();
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
        n.size = updateSize(n);

        // balance the tree
        // move red node to left. if right is red, and left is not red,rotate n to left
        if (isRed(n.right) && !isRed(n.left)) {
            n = rotateLeft(n);
        }

        // if both child are red, exchange parent and child's color
        if (isRed(n.left) && isRed(n.right)) {
            flipColor(n);
        }

        // we have already exclude the circumstance above (left red, or both red).
        // we accpet one left red child.
        // but if n has 2 conneted left red child, rotate n to right;
        if (isRed(n.left) && isRed(n.left.left)) {
            n = rotateRight(n);
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
        System.out.println();
    }

    private void inorderPrint(BSTNode n) {
        if (n == null) return;
        inorderPrint(n.left);
        System.out.print(n.key + " ");
        inorderPrint(n.right);
    }
    private BSTNode rotateLeft(BSTNode n) {
        if (n == null || n.right == null) return n;
        //rotate
        BSTNode newRoot = n.right;
        n.right = newRoot.left;
        newRoot.left = n;
        // update size and color
        n.size = updateSize(n);
        newRoot.size = updateSize(newRoot);
        flipColor(newRoot, n);
        //return
        return newRoot;
    }

    private BSTNode rotateRight(BSTNode n) {
        if (n == null || n.left == null) return n;
        //rotate
        BSTNode newRoot = n.left;
        n.left = newRoot.right;
        newRoot.right = n;
        // update size and color
        n.size = updateSize(n);
        newRoot.size = updateSize(newRoot);
        flipColor(newRoot, n);
        //return
        return newRoot;
    }

    /*Exchange color between parent and child.
     * when both child are red, only input parent
     * when rotate, input newparent and the child (who is parent befoe)*/
    private void flipColor(BSTNode parent, BSTNode... childNode) {
        // exchange color when both child are red
        if (childNode.length == 0) {
            parent.isred = true;
            parent.left.isred = false;
            parent.right.isred = false;
        }
        // exchange color when rotate
        if (childNode.length == 1) {
            Boolean parentColor = parent.isred;
            parent.isred = childNode[0].isred;
            childNode[0].isred = parentColor;
        }
    }

    private boolean isRed(BSTNode n) {
        if (n == null) return false;
        return n.isred;
    }

    public void printpreOrder() {
        preorderPrint(this.node);
        System.out.println();
    }

    private void preorderPrint(BSTNode n) {
        if (n == null) return;
        System.out.print(n.key + " ");
        preorderPrint(n.left);
        preorderPrint(n.right);
    }

    public void printPostorder() {
        postorderPrint(this.node);
        System.out.println();
    }

    private void postorderPrint(BSTNode n) {
        if (n == null) return;
        postorderPrint(n.left);
        postorderPrint(n.right);
        System.out.print(n.key + " ");
    }
}
