package deque;

public class LinkedListDeque<T> {
    private Node sentinel;
    private int size;

    private class Node {
        public Node prev;
        public T item;
        public Node next;

        public Node() {
            Node prev;
            T item;
            Node next;
        }
        public Node(Node prevNode, T aitem, Node nextNode) {
            prev = prevNode;
            item = aitem;
            next = nextNode;
        }

        public T getRecursive(int index) {
            Node curNode = this;
            if (curNode == sentinel)
                return null;

            if (index == 0)
                return curNode.item;

            curNode = curNode.next;
            return curNode.getRecursive(index - 1);
        }

    }

    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        int size = 0;
    }

    public void addFirst(T item) {
        sentinel.next = new Node(sentinel, item, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    public void addLast(T item) {
        sentinel.prev = new Node(sentinel.prev, item, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }

    public boolean isEmpty() {
        if (sentinel.next == sentinel) {
            return true;
        }else {
            return false;
        }
    }

    //*Returns the number of items in the deque.*/
    public int size() {
        return size;
    }

    /*Prints the items in the deque from first to last, separated by a space.
    Once all the items have been printed, print out a new line.*/
    public void printDeque() {
        Node Deque = sentinel.next;
        while (Deque != null) {
            System.out.print(Deque.item + " ");
            Deque = Deque.next;
            if (Deque == sentinel)
                break;
        }
        System.out.println();
    }

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

    /*Removes and returns the item at the back of the deque.
     If no such item exists, returns null.*/
    public T removeLast() {
        if (sentinel.next == sentinel) {
            return null;
        }
        {
            T tempLastItem = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size -= 1;
            return tempLastItem;
        }
    }

    /*Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
    If no such item exists, returns null. Must not alter the deque!*/
    public T get(int index) {
        Node curNode = sentinel.next;
        if (curNode == sentinel)
            return null;

        int position = 0;
        while (curNode.next != sentinel && position < index) {
            position += 1;
            curNode = curNode.next;
        }

        if (position != index)
            return null;

        return curNode.item;
    }

    public T getRecursive(int index) {
        return sentinel.next.getRecursive(index);
    }

}