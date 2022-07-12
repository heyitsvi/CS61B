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

    private static class intComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    private static class strComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    private static Comparator<String> getStrComparator() {
        return new strComparator();
    }

    private static Comparator<Integer> getIntComparator() {
        return new intComparator();
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

        if(other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < this.size(); i++) {
            if (this.get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Comparator<Integer> intCmp = MaxArrayDeque.getIntComparator();
        Comparator<String> strCmp = MaxArrayDeque.getStrComparator();
        MaxArrayDeque<Integer> L = new MaxArrayDeque<>(intCmp);
        MaxArrayDeque<String> L2 = new MaxArrayDeque<>(strCmp);
        MaxArrayDeque<Integer> L3 = new MaxArrayDeque<>(intCmp);



        L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(9);
        L.addFirst(4);
        L.addLast(2);
        L.addLast(12);

        System.out.println(L.max());

        /*L3.addFirst(1);
        L3.addFirst(2);
        L3.addFirst(3);
        L3.addFirst(9);
        L3.addFirst(4);
        L3.addLast(2);
        L3.addLast(12);
        L3.addLast(10);*/
        L2.addFirst("a");
        L2.addFirst("b");
        L2.addFirst("e");
        L2.addFirst("f");
        L2.addLast("y");
        L2.addLast("r");

        System.out.println(L2.max());

    }

}
