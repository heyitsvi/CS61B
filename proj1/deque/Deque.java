package deque;

public interface Deque<Type> {
    public void addFirst(Type item);
    public void addLast(Type item);

    //Returns true if deque is empty, false otherwise.
    default public boolean isEmpty(){
        return this.size() == 0;
    };
    public int size();
    public void printDeque();
    public Type removeFirst();
    public Type removeLast();
    public Type get(int index);
}
