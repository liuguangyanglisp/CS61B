package deque;

import com.sun.xml.internal.xsom.impl.scd.Iterators;

public class ArrayDeque<T> {
    /** Creates an empty list. */
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public int minusOne(int index){
        if (index > 0){
            index -= 1;
        }else {
            index = items.length - 1;
        }
        return index;
    }

    public int plusOne(int index){
        if (index < items.length - 1) {
            index += 1;
        }else {
            index = 0;
        }
        return index;
    }

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    public void addFirst(T x) {
        items[nextFirst] = x;
        size += 1;
        nextFirst = minusOne(nextFirst);

    }

    /** Inserts X into the back of the list. */
    public void addLast(T x) {
        items[nextLast] = x;
        size += 1;
        nextLast = plusOne(nextLast);
    }

    public boolean isEmpty(){
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int first = plusOne(nextFirst);
        while (first != nextLast){
            System.out.print(items[first] + " ");
            first = plusOne(first);
        }
        System.out.println();
    }

    public T removeFirst(){
        T tempFirstItem = items[plusOne(nextFirst)];
        items[plusOne(nextFirst)] = null;
        nextFirst = plusOne(nextFirst);
        size -= 1;
        return tempFirstItem;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public T removeLast() {
        T tempLastItem = items[minusOne(nextLast)];
        items[minusOne(nextLast)] = null;
        nextLast = minusOne(nextLast);
        size -= 1;
        return tempLastItem;
    }

    public T get(int index){
        if (index >= size || index < 0){
            return null;
        }
        int actualIndex = IndexTranslate(index);
        return items[actualIndex];
    }

    public int IndexTranslate(int index){
        int newIndex = plusOne(nextFirst) + index;
        if (newIndex > items.length - 1)
            newIndex = newIndex - items.length;
        return newIndex;
    }



    public static void main(String[]args){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addFirst(1);
        L.addLast(2);
        /*L.addLast(3);
        L.addFirst(4);
        L.isEmpty();
        System.out.println(L.get(0));
        System.out.println(L.get(1));
        System.out.println(L.get(2));
        System.out.println(L.get(3));
        System.out.println(L.get(10));
        L.printDeque();
        L.printDeque();*/
        System.out.println(L.removeFirst());
        System.out.println(L.removeLast());
        System.out.println(L.isEmpty());
        System.out.println(L.size);

    }
}