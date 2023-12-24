package deque;
import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /*Create an empty ArrayDeque.*/
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    /*Help function to count Index to make Array circular.
     *the front of front is the end*/
    private int minusOne(int index) {
        if (index > 0) {
            index -= 1;
        } else {
            index = items.length - 1;
        }
        return index;
    }

    /*Help function to count Index to make Array circular.
     *the next positon of end is the front*/
    private int plusOne(int index) {
        if (index < items.length - 1) {
            index += 1;
        } else {
            index = 0;
        }
        return index;
    }

    /*Adds an item of type T to the front of the deque.*/
    @Override
    public void addFirst(T x) {
        if (size == items.length) {
            reSize(size * 2);
        }
        items[nextFirst] = x;
        size += 1;
        nextFirst = minusOne(nextFirst);

    }

    /*Adds an item of type T to the back of the deque.*/
    @Override
    /** Inserts X into the back of the list. */
    public void addLast(T x) {
        if (size == items.length) {
            reSize(size * 2);
        }
        items[nextLast] = x;
        size += 1;
        nextLast = plusOne(nextLast);
    }

    @Override
    /*Return the size of ArrayDeque.*/
    public int size() {
        return size;
    }

    @Override
    /*Print item of deque from first to last, separated by space.
    when it's done, print out a new line.*/
    public void printDeque() {
        int first = plusOne(nextFirst);
        while (first != nextLast) {
            System.out.print(items[first] + " ");
            first = plusOne(first);
        }
        System.out.println();
    }

    @Override
    /*Removes and returns the item at the front of the deque.
    If no such item exists, returns null.*/
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        double usageRate = (size - 1) / (items.length * 1.0);
        if (items.length >= 16 && usageRate < 0.25) {
            reSize(items.length / 2);
        }
        T tempFirstItem = items[plusOne(nextFirst)];
        items[plusOne(nextFirst)] = null;
        nextFirst = plusOne(nextFirst);
        size -= 1;
        return tempFirstItem;
    }

    @Override
    /** Deletes item from back of the list and
     * returns deleted item. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        double usageRate = (size - 1) / (items.length * 1.0);
        if (items.length >= 16 && usageRate < 0.25) {
            reSize(items.length / 2);
        }
        T tempLastItem = items[minusOne(nextLast)];
        items[minusOne(nextLast)] = null;
        nextLast = minusOne(nextLast);
        size -= 1;
        return tempLastItem;
    }

    @Override
    /*Gets item at given index, where 0 is front, 1 is next item, and so forth.
    If no such item exists, returns null. Don't alter the deque!*/
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int arrayIndex = indexTranslate(index);
        return items[arrayIndex];
    }

    /*Given item index of the deque.
     * return the index of array to get the item directly.*/
    private int indexTranslate(int index) {
        int newIndex = plusOne(nextFirst) + index;
        if (newIndex > items.length - 1) {
            newIndex = newIndex - items.length;
        }
        return newIndex;
    }

    /*Return an Iterator.
     * It is an instance of ADIterator class*/
    public Iterator<T> iterator() {
        return new ADIterator();
    }

    /*ADIterator has 2 methods.
     * hasNext return true if the position has an item.
     * next return the item of the position*/
    private class ADIterator implements Iterator<T> {
        private int position;
        private ADIterator() {
            position = plusOne(nextFirst);
        }

        public boolean hasNext() {
            return position != nextLast;
        }

        public T next() {
            T returnItem = items[position];
            position = plusOne(position);
            return returnItem;
        }
    }

    /*Returns whether the parameter o is equal to the Deque*/
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        /*Test whether Object o is an instance of ArrayDeque.
        If o is not or null,  return false.*/
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

    /*Resize Deque's size to n.
     *reorder items,start from index 0.
     *reset nextFirst and nextLast*/
    private void reSize(int n) {
        T[] tempItems = (T[]) new Object[n];
        for (int i = 0; i <= size - 1; i += 1) {
            tempItems[i] = get(i);
        }
        items = tempItems;
        nextFirst = items.length - 1;
        nextLast = size;
        tempItems = null;
    }
}
