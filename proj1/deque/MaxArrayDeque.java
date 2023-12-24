package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }
    public T max() {
        return max(comparator);
    }
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        if (size() == 1) {
            return get(0);
        }

        int maxIndex = 0;
        for (int i = 0; i <= size() - 1; i += 1) {
            int cmp = c.compare(get(i), get(maxIndex));
            if (cmp > 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }

    private static Comparator<Integer> getNumberComparator() {
        return new NumberComparator();
    }

    private static class NumberComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    private static Comparator<String> getStringComparator() {
        return new StringComparator();
    }

    private static class StringComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    private static Comparator<String> getStringLengthComparator() {
        return new StringLengthComparator();
    }

    private static class StringLengthComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }
}
