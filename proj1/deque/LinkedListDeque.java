package deque;

public class LinkedListDeque<Type> {
    private class Node{
        public Node prev;
        Type data;
        public Node next;
        public Node(Node p, Type i, Node n){
            this.prev = p;
            this.data = i;
            this.next = n;
        }

        public Type getValue(){
            return this.data;
        }

        public void setNext(Node p){
            this.next = p;
        }

        public void setPrev(Node p){
            this.prev = p;
        }

        public void setValue(Type value){
            this.data = value;
        }

        public Node getPrev(){
            return this.prev;
        }

        public Node getNext(){
            return this.next;
        }
    }

    private Node sentinel;

    int size;

    public LinkedListDeque(){
        sentinel = new Node(null, null,null);
        sentinel.setPrev(sentinel);
        sentinel.setNext(sentinel);
        size = 0;
    }

    public void addBetween(Node firstNode, Type item, Node secondNode){
        Node newNode = new Node(firstNode, item, secondNode);
        firstNode.setNext(newNode);
        secondNode.setPrev(newNode);

    }

    //Adds an item of type T to the front of the deque. You can assume that item is never null.
    public void addFirst(Type item){
        addBetween(sentinel, item, sentinel.next);
        size += 1;
    };

    //Adds an item of type T to the back of the deque. You can assume that item is never null.
    public void addLast(Type item){
        addBetween(sentinel.getPrev(), item, sentinel);
        size += 1;
    };

    //Returns true if deque is empty, false otherwise.
    public boolean isEmpty(){
        return size == 0;
    };

    //Returns the number of items in the deque.
    public int size(){
        return size;
    };

    //Prints the items in the deque from first to last, separated by a space. Once all the items have been printed, print out a new line.
    public void printDeque(){
        Node n = sentinel.next;

        while(n != sentinel){
            System.out.print(n.getValue() + " ");

            n = n.getNext();
        }
        System.out.println();
    };

    public Type removeNode(Node n){
        Type originalValue = n.getValue();
        Node prevNode = n.getPrev();
        Node nextNode = n.getNext();

        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);


        n.setPrev(null);
        n.setNext(null);
        n.setValue(null);

        return originalValue;
    }
    //Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public Type removeFirst(){
        if (isEmpty()){
            return null;
        }

        size -= 1;
        return removeNode(sentinel.getNext());
    };

    //Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public Type removeLast(){
        if(isEmpty()){
            return null;
        }

        size -= 1;
        return removeNode(sentinel.getPrev());
    };

    //Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. Must not alter the deque!
    public Type get(int index){
        Node p = sentinel.next;

        while(index != 0){
            p = p.getNext();
            index -= 1;
        }

        return p.getValue();
    };

    private Type getRecursive(int index, Node n){
        if (index == 0){
            return n.getValue();
        }

        return getRecursive(index - 1, n.getNext());
    }
    public Type getRecursive(int index){
        return getRecursive(index, sentinel.getNext());
    }

    public static void main(String[] args) {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();

        L.addFirst(3);
        L.addFirst(2);
        L.addFirst(1);
        L.addFirst(0);
        L.addLast(4);
        L.addLast(5);
        L.printDeque();
        System.out.println(L.size());
        System.out.println(L.get(5));
        System.out.println(L.getRecursive(5));
    }
}