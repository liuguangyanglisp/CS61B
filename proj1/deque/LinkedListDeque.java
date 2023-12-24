package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private Node sentinel;
    private int size;

    private class Node {
        private Node prev;
        private T item;
        private Node next;

        /*Construct an empty Node.*/
        private Node() {
        }

        /*Input: prevNode, aitem, and nextNode.
         * Create a new Node,which points to prev and next Node.*/
        private Node(Node prevNode, T aitem, Node nextNode) {
            prev = prevNode;
            item = aitem;
            next = nextNode;
        }

        /*Return the index T item from the current Node recursively.*/
        public T getRecursiveNode(int index) {
            Node curNode = this;
            if (curNode == sentinel) {
                return null;
            }

            if (index == 0) {
                return curNode.item;
            }

            curNode = curNode.next;
            return curNode.getRecursiveNode(index - 1);
        }

    }

    /*Create an empty LinkedListDeque.*/
    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    /*Adds an item of type T to the front of the deque.*/
    public void addFirst(T item) {
        sentinel.next = new Node(sentinel, item, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    @Override
    /*Adds an item of type T to the back of the deque.*/
    public void addLast(T item) {
        sentinel.prev = new Node(sentinel.prev, item, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }

    @Override
    //*Returns the number of items in the deque.*/
    public int size() {
        return size;
    }

    @Override
    /*Print item of deque from first to last, separated by space.
    when it's done, print out a new line.*/
    public void printDeque() {
        Node theDeque = sentinel.next;
        while (theDeque != null) {
            System.out.print(theDeque.item + " ");
            theDeque = theDeque.next;
            if (theDeque == sentinel) {
                break;
            }
        }
        System.out.println();
    }

    @Override
    /*Removes and returns the item at the front of the deque.
    If no such item exists, returns null.*/
    public T removeFirst() {
        if (sentinel.next == sentinel) {
            return null;
        }
        T tempFirstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return tempFirstItem;
    }

    @Override
    /** Deletes item from back of the list and returns deleted item.
     * If no such item exists, returns null.*/
    public T removeLast() {
        if (sentinel.next == sentinel) {
            return null;
        } else {
            T tempLastItem = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size -= 1;
            return tempLastItem;
        }
    }

    @Override
    /*Gets item at given index, where 0 is front, 1 is next item, and so forth.
    If no such item exists, returns null. Don't alter the deque!*/
    public T get(int index) {
        Node curNode = sentinel.next;
        if (curNode == sentinel) {
            return null;
        }

        int position = 0;
        while (curNode.next != sentinel && position < index) {
            position += 1;
            curNode = curNode.next;
        }

        if (position != index) {
            return null;
        }

        return curNode.item;
    }

    /*Gets the item at the given index in a recursive way.*/
    public T getRecursive(int index) {
        return sentinel.next.getRecursiveNode(index);
    }

    /*Return an Iterator.
     * It is an instance of LLDIterator class*/
    public Iterator<T> iterator() {
        return new LLDIterator();
    }

    /*LLDIterator has 2 methods.
     * hasNext return true if the position has an item.
     * next return the item of the position*/
    private class LLDIterator implements Iterator<T> {
        private Node position;

        public LLDIterator() {
            position = sentinel.next;
        }

        public boolean hasNext() {
            return position != sentinel;
        }

        public T next() {
            T returnItem = position.item;
            position = position.next;
            return returnItem;
        }
    }

    /*Returns whether the parameter o is equal to the Deque.*/
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        /*Test whether the object is an instance of the specified type.
        If o is null, return false.*/
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque other = (Deque) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i <= size - 1; i += 1) {
            if (this.get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }
}
