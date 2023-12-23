package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c){
        comparator = c;
    }
    public T max(){
        return max(comparator);
    }
    public T max(Comparator<T> c){
        if (isEmpty()){
            return null;
        }

        if (size() == 1){
            return get(0);
        }

        int maxIndex = 0;
        for (int i = 0; i <= size() - 1; i += 1){
            int cmp = c.compare(get(i), get(maxIndex));
            if (cmp > 0)
                maxIndex = i;
        }
        return get(maxIndex);
    }

    public static Comparator<Integer> getNumberComparator(){
        return new numberComparator();
    }

    public static class numberComparator implements Comparator<Integer>{
        public int compare(Integer a, Integer b){
            return a - b;
        }
    }

    public static Comparator<String> getStringComparator(){
        return new StringComparator();
    }

    public static class StringComparator implements Comparator<String>{
        public int compare(String a, String b){
            return a.compareTo(b);
        }
    }

    public static Comparator<String> getStringLengthComparator(){
        return new StringLengthComparator();
    }

    public static class StringLengthComparator implements Comparator<String>{
        public int compare(String a, String b){
            return a.length() - b.length();
        }
    }

    public static void main(String[]args){
        /*MaxArrayDeque<Integer> A = new MaxArrayDeque<>(getNumberComparator());
        A.addFirst(100);
        A.addLast(2);
        A.addLast(3);
        A.addFirst(4);
        System.out.println(A.max());

        MaxArrayDeque<String> B = new MaxArrayDeque<>(getStringComparator());
        B.addFirst("liuguangyang");
        B.addLast("dawang");
        B.addLast("z");
        B.addFirst("faker");
        B.addFirst("adc");
        B.addFirst("cs61b");
        System.out.println(B.max());
        System.out.println(B.max(getStringLengthComparator()));*/

        MaxArrayDeque<String> C = new MaxArrayDeque<>(getStringComparator());
        /*C.addFirst("1");
        C.addLast("2222");
        C.addLast("3");
        C.addFirst("a");*/
        C.addFirst("9.1");
        C.addFirst("9.2");
        /*C.addLast("c");
        C.addLast("cc");*/
        C.addFirst("2");
        System.out.println(C.max());
        /*System.out.println(C.max(getStringLengthComparator()));*/
    }

}
