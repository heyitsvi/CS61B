package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    //creates a MaxArrayDeque with the given Comparator.
    private final Comparator<T> comp;
    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    };

    /**returns the maximum element in the deque as governed by the previously given Comparator.
     If the MaxArrayDeque is empty, simply return null. */
    public T max() {
        return max(comp);
    }

    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }

        int maxElementIndex = 0;

        for (int i = 0; i < this.size(); i++) {
            if (this.get(maxElementIndex) == null) {
                maxElementIndex += 1;
                continue;
            }

            if (this.get(i) == null) {
                continue;
            }


            if (c.compare(this.get(i), this.get(maxElementIndex)) > 0) {
                maxElementIndex = i;
            }
        }

        return this.get(maxElementIndex);
    }

    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    private static class StrComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    private static Comparator<String> getStrComparator() {
        return new StrComparator();
    }

    private static Comparator<Integer> getIntComparator() {
        return new IntComparator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || (this.getClass() != o.getClass())) {
            return false;
        }

        MaxArrayDeque<T> other = (MaxArrayDeque<T>) o;

        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < this.size(); i++) {
            if (this.get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }
}

