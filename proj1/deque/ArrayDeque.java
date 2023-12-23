package deque;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T> {
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
        if (size == items.length){
            reSize(size * 2);
        }
        items[nextFirst] = x;
        size += 1;
        nextFirst = minusOne(nextFirst);

    }

    /** Inserts X into the back of the list. */
    public void addLast(T x) {
        if (size == items.length){
            reSize(size * 2);
        }
        items[nextLast] = x;
        size += 1;
        nextLast = plusOne(nextLast);
    }

    public boolean isEmpty(){
        return size == 0;
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
        double usageRate = (size - 1) / (items.length * 1.0);
        if ( items.length >= 16 && usageRate < 0.25){
            reSize(items.length / 2);
        }
        T tempFirstItem = items[plusOne(nextFirst)];
        items[plusOne(nextFirst)] = null;
        nextFirst = plusOne(nextFirst);
        size -= 1;
        return tempFirstItem;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public T removeLast() {
        double usageRate = (size - 1) / (items.length * 1.0);
        if ( items.length >= 16 && usageRate < 0.25){
            reSize(items.length / 2);
        }
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
        int actualIndex = indexTranslate(index);
        return items[actualIndex];
    }
    public int indexTranslate(int index){
        int newIndex = plusOne(nextFirst) + index;
        if (newIndex > items.length - 1)
            newIndex = newIndex - items.length;
        return newIndex;
    }
    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int position;
        public ArrayDequeIterator(){
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
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }

        /* If we apply the instanceof operator with any variable that has null value, it returns false.*/
        if (o instanceof ArrayDeque) {
        }else {
            return false;
        }

        ArrayDeque other = (ArrayDeque) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i <= size - 1; i += 1){
            if (this.get(i) != other.get(i))
                return false;
        }
        return true;
    }

    public void reSize(int n){
            T[] tempItems = (T[]) new Object[n];
            for (int i = 0; i <= size - 1; i += 1){
                tempItems [i] = get(i);
            }
            items = tempItems;
            nextFirst = items.length - 1;
            nextLast = size;
            tempItems = null;
    }

    public static void main(String[]args){
/*        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addFirst(1);
        L.addLast(2);
        L.addLast(3);
        L.addFirst(4);
        System.out.println(L.isEmpty());

        Iterator<Integer> seer = L.iterator();

        while (seer.hasNext()){
            System.out.println(seer.next());
        }

        for (Integer i : L){
            System.out.println(i);
        }

        System.out.println(L.get(10));
        L.printDeque();
        System.out.println(L.removeFirst());
        System.out.println(L.removeLast());
        System.out.println(L.isEmpty());
        System.out.println(L.size);*/

        ArrayDeque<Integer> A = new ArrayDeque<>();
        A.addFirst(1);
        A.addLast(2);
        A.addLast(3);
        A.addFirst(4);
        /*ArrayDeque<String> B = new ArrayDeque<>();
        B.addFirst("1");
        B.addLast("2");
        B.addLast("3");
        B.addFirst("4");
        System.out.println(A.equals(B));*/
        /*A.removeLast();
        A.removeLast();
        A.removeLast();
        A.removeLast();*/
        A.addFirst(1);
        A.addLast(2);
        A.addLast(3);
        A.addFirst(4);
        A.addFirst(10);
        A.removeLast();
        A.removeLast();
        A.removeLast();
        A.removeLast();
        A.removeLast();
        A.removeLast();
        A.removeLast();
        A.removeLast();
    }
}
