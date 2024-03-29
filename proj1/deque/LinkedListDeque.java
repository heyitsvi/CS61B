package deque;
import java.lang.reflect.Type;
import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private class Node {
        private Node prev;
        T data;
        private Node next;
        Node(Node p, T i, Node n) {
            this.prev = p;
            this.data = i;
            this.next = n;
        }

        public T getValue() {
            return this.data;
        }

        public void setNext(Node p) {
            this.next = p;
        }

        public void setPrev(Node p) {
            this.prev = p;
        }

        public void setValue(T value) {
            this.data = value;
        }

        public Node getPrev() {
            return this.prev;
        }

        public Node getNext() {
            return this.next;
        }
    }

    public Iterator<T> iterator() {
        return new LLIterator();
    }


    private Node sentinel;

    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.setPrev(sentinel);
        sentinel.setNext(sentinel);
        size = 0;
    }

    private class LLIterator implements Iterator<T> {
        int pos = 0;
        Node p = sentinel.next;

        public boolean hasNext() {
            return pos < size;
        }

        public T next() {
            T item = p.getValue();
            p = p.getNext();
            pos += 1;
            return item;
        }
    }

    private void addBetween(Node firstNode, T item, Node secondNode) {
        Node newNode = new Node(firstNode, item, secondNode);
        firstNode.setNext(newNode);
        secondNode.setPrev(newNode);

    }

    @Override
    //Adds an item of t T to the front of the deque. You can assume that item is never null.
    public void addFirst(T item) {
        addBetween(sentinel, item, sentinel.next);
        size += 1;
    };

    @Override
    //Adds an item of t T to the back of the deque. You can assume that item is never null.
    public void addLast(T item) {
        addBetween(sentinel.getPrev(), item, sentinel);
        size += 1;
    };

    @Override
    //Returns the number of items in the deque.
    public int size() {
        return size;
    };

    @Override
    //Prints the items in the deque from first to last, separated by a space.
    //Once all the items have been printed, print out a new line.
    public void printDeque() {
        Node n = sentinel.next;

        while (n != sentinel) {
            System.out.print(n.getValue() + " ");

            n = n.getNext();
        }
        System.out.println();
    };

    private T removeNode(Node n) {
        T originalValue = n.getValue();
        Node prevNode = n.getPrev();
        Node nextNode = n.getNext();

        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);


        n.setPrev(null);
        n.setNext(null);
        n.setValue(null);

        return originalValue;
    }

    @Override
    //Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        size -= 1;
        return removeNode(sentinel.getNext());
    };

    @Override
    //Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        size -= 1;
        return removeNode(sentinel.getPrev());
    };

    private Node getFirstElementPointer() {
        return sentinel.next;
    }

    @Override
    //Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
    //If no such item exists, returns null. Must not alter the deque!
    public T get(int index) {
        Node p = sentinel.next;

        while (index != 0) {
            p = p.getNext();
            index -= 1;
        }

        return p.getValue();
    };

    private T getRecursive(int index, Node n) {
        if (index == 0) {
            return n.getValue();
        }

        return getRecursive(index - 1, n.getNext());
    }
    public T getRecursive(int index) {
        return getRecursive(index, sentinel.getNext());
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

        Deque<Type> other = (Deque<Type>) o;


        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < this.size(); i++) {
            if (!java.util.Objects.deepEquals(this.get(i), other.get(i))) {
                return false;
            }
        }

        return true;

        /*LinkedListDeque<T> other = (LinkedListDeque<T>) o;

        if (other.size() != this.size()) {
            return false;
        }

        Node thisP = this.getFirstElementPointer();
        Node otherP = other.getFirstElementPointer();
        int iter = size;
        while (iter > 0) {
            if (otherP.getValue() != thisP.getValue()) {
                return false;
            }
            iter -= 1;
            thisP = thisP.getNext();
            otherP = otherP.getNext();
        }
        return true;*/
    }
}
