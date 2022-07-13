package deque;
import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;

    private int nextFirst;

    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        nextFirst = items.length - 1;
        size = 0;
        nextLast = 0;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;

        ArrayDequeIterator() {
            pos = 0;
        }

        public boolean hasNext() {
            return pos < size;
        }

        public T next() {
            T item = items[pos];
            pos += 1;
            return item;
        }
    }
    private void resize(int capacity) {
        int frontNum = calcNumOfFrontElements();
        T[] newArr = (T[]) new Object[capacity];

        if (nextLast > 0 && nextFirst < items.length - 1) {
            System.arraycopy(items, 0, newArr, 0, nextLast);
            System.arraycopy(items, nextFirst + 1, newArr, nextLast + size, frontNum);
        } else if (nextLast == 0 && nextFirst < items.length) {
            System.arraycopy(items, nextFirst + 1, newArr, nextLast + size, frontNum);
        } else {
            System.arraycopy(items, 0, newArr, 0, nextLast);
        }

        nextFirst = nextLast + size - 1;
        items = newArr;
    }

    /*private int getArrayLength() {
        return items.length;
    }*/

    private int calcNumOfFrontElements() {
        return size - nextLast;
    }

    private int calcNumOfBackElements() {
        return nextLast;
    }

    private int getNewFirstIndex(int capacity) {
        return nextFirst - capacity;
    }

    private void downsizeArray(int capacity) {
        int frontNum = calcNumOfFrontElements();
        int newFirstIndex = getNewFirstIndex(capacity);
        T[] newArr = (T[]) new Object[capacity];

        if (nextFirst == items.length - 1 && nextLast > 0) {
            System.arraycopy(items, 0, newArr, 0, nextLast);
        } else if (nextLast == 0 && nextFirst < items.length) {
            System.arraycopy(items, nextFirst + 1, newArr, newFirstIndex + 1, frontNum);
        } else if (nextLast > nextFirst) {
            System.arraycopy(items, nextFirst + 1, newArr, newFirstIndex + 1, size);
            nextLast = nextLast - capacity;
        } else {
            System.arraycopy(items, 0, newArr, 0, nextLast);
            System.arraycopy(items, nextFirst + 1, newArr, newFirstIndex + 1, frontNum);
        }
        nextFirst = newFirstIndex;
        items = newArr;
    }
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        if (nextFirst < 0) {
            nextFirst = items.length - 1;
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        size += 1;
    };

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        if (nextLast > items.length - 1) {
            nextLast = 0;
        }
        items[nextLast] = item;
        nextLast += 1;
        size += 1;
    };

    private double getUsageRatio() {
        return ((size * 1.0) / items.length);
    }

    @Override
    public T removeFirst() {
        //System.out.println(getUsageRatio());

        if (isEmpty()) {
            return null;
        }
        if (getUsageRatio() <= 0.25 && items.length > 16) {
            downsizeArray(items.length / 2);
        }

        T item;

        if (nextFirst < items.length - 1) {
            /*if(nextFirst < nextLast){
                nextLast -= 1;
            }*/
            item = items[nextFirst + 1];
            items[nextFirst + 1] = null;
            nextFirst += 1;
        } else {
            item = items[0];
            items[0] = null;
            nextFirst = 0;
            //reverse = true;
        }
//

        size -= 1;

        //System.out.println("First item removed: " + item);
        return item;
    };

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        //System.out.println(getUsageRatio());
        if (getUsageRatio() <= 0.25 && items.length > 16) {
            downsizeArray(items.length / 2);
        }

        T item;
        if (nextLast - 1 < 0) {
            item = items[items.length - 1];
            items[items.length - 1] = null;
            nextLast = items.length - 1;
        } else {
            item = items[nextLast - 1];
            items[nextLast - 1] = null;
            nextLast -= 1;
        }

        //System.out.println("Last item removed: " + item);
        size -= 1;
        return item;
    };

    @Override
    public T get(int i) {

        if (i < 0 || i > size) {
            return null;
        }

        if (nextLast == 0) {
            return items[items.length - 1 - i];
        }

        if (nextLast < nextFirst) {
            return items[i];
        } else if (nextFirst < nextLast && nextFirst >= 0) {
            return items[nextFirst + 1];
        } else {
            return items[i];
        }
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (this.getClass() != o.getClass()) {
            if (!(o instanceof Deque)) {
                return false;
            }
        }

        Deque<T> other = (Deque<T>) o;

        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!java.util.Objects.deepEquals(this.get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(0);
        L.get(0);
        L.removeFirst();
        L.addFirst(3);
        L.addLast(4);
        L.get(1) ;
        L.get(0);
        L.removeLast();
        L.removeLast();
        L.addLast(9);
        L.removeFirst();
        L.addFirst(11);
        L.addLast(12);
        L.addLast(13);
        System.out.println(L.get(1));
        L.removeFirst();
        L.removeLast();
        System.out.println(L.get(0));
    }
}
