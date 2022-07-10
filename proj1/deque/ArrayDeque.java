package deque;
import java.util.Iterator;

public class ArrayDeque<Type> implements Iterable<Type>, Deque<Type>{
    private Type[] items;
    private int size;

    private int nextFirst;

    private int nextLast;

    public ArrayDeque(){
        items = (Type[]) new Object[8];
        nextFirst = items.length - 1;
        size = 0;
        nextLast = 0;
    }

    public Iterator<Type> iterator() {
        return new arrayDequeIterator();
    }

    private class arrayDequeIterator implements Iterator<Type>{
        private int pos;

        arrayDequeIterator(){
            pos = 0;
        }

        public boolean hasNext(){
            return pos < size;
        }

        public Type next(){
            Type item = items[pos];
            pos += 1;
            return item;
        }
    }
    public void resize(int capacity){
        int frontNum = calcNumOfFrontElements();
        Type[] newArr = (Type[]) new Object[capacity];

        if(nextLast > 0 && nextFirst < items.length - 1){
            System.arraycopy(items, 0, newArr, 0, nextLast);
            System.arraycopy(items, nextFirst + 1, newArr, nextLast + size, frontNum);
        } else if(nextLast == 0 && nextFirst < items.length){
            System.arraycopy(items, nextFirst + 1, newArr, nextLast + size, frontNum);
        } else{
            System.arraycopy(items, 0, newArr, 0, nextLast);
        }

        nextFirst = nextLast + size - 1;
        items = newArr;
    }

    public int getArrayLength(){
        return items.length;
    }
    public int calcNumOfFrontElements(){
        return size - nextLast;
    }

    public int calcNumOfBackElements(){
        return nextLast;
    }

    public int getNewFirstIndex(int capacity){
        return nextFirst - capacity;
    }

    public void downsizeArray(int capacity){
        int frontNum = calcNumOfFrontElements();
        int newFirstIndex = getNewFirstIndex(capacity);
        Type[] newArr = (Type[]) new Object[capacity];

        if (nextFirst == items.length - 1 && nextLast > 0){
            System.arraycopy(items, 0, newArr, 0, nextLast);
        } else if(nextLast == 0 && nextFirst < items.length){
            System.arraycopy(items, nextFirst + 1, newArr, newFirstIndex + 1, frontNum);
        }else if (nextLast > nextFirst){
            System.arraycopy(items, nextFirst + 1, newArr, newFirstIndex + 1, size);
            nextLast = nextLast - capacity;
        }else{
            System.arraycopy(items, 0, newArr, 0, nextLast);
            System.arraycopy(items, nextFirst + 1, newArr, newFirstIndex + 1, frontNum);
        }


        nextFirst = newFirstIndex;
        items = newArr;
    }
    @Override
    public void addFirst(Type item){
        if (size == items.length){
            resize(size * 2);
        }

        if(nextFirst < 0){
            nextFirst = items.length - 1;
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        size += 1;
    };

    @Override
    public void addLast(Type item){
        if (size == items.length){
            resize(size * 2);
        }

        if (nextLast > items.length - 1){
            nextLast = 0;
        }
        items[nextLast] = item;
        nextLast += 1;
        size += 1;
    };

    public double getUsageRatio(){
        return ((size * 1.0) / items.length);
    }

    @Override
    public Type removeFirst(){
        //System.out.println(getUsageRatio());

        if(isEmpty()){
            return null;
        }
        if (getUsageRatio() <= 0.25 && items.length > 16){
            downsizeArray(items.length / 2);
        }

        Type item;

        if (nextFirst < items.length - 1){
            item = items[nextFirst + 1];
            items[nextFirst + 1] = null;
            nextFirst += 1;
        }else{
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
    public Type removeLast(){
        if(isEmpty()){
            return null;
        }
        //System.out.println(getUsageRatio());
        if (getUsageRatio() <= 0.25 && items.length > 16){
            downsizeArray(items.length / 2);
        }

        Type item;
        if(nextLast - 1 < 0){
            item = items[items.length - 1];
            items[items.length - 1] = null;
            nextLast = items.length - 1;
        } else{
            item = items[nextLast - 1];
            items[nextLast - 1] = null;
            nextLast -= 1;
        }

        //System.out.println("Last item removed: " + item);
        size -= 1;
        return item;
    };

    @Override
    public Type get(int i){
        return items[i];
    };


    @Override
    public int size(){
        return size;
    }

    @Override
    public boolean isEmpty(){
        return size == 0;
    }

    @Override
    public void printDeque(){
        for(int i = 0; i < size; i++){
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    public boolean equals(Object o){
        if (this == o){
            return true;
        }

        if (o == null || (this.getClass() != o.getClass())){
            return false;
        }

        ArrayDeque<Type> other = (ArrayDeque<Type>) o;

        if(other.size() != this.size()){
            return false;
        }

        for(int i = 0; i < size; i++){
            if(this.get(i) != other.get(i)){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        ArrayDeque<Integer> M = new ArrayDeque<>();

        /*for(int i = 0; i < 32; i ++){
            L.addLast(1);
        }*/

        for(int i = 0; i <= 6; i ++){
            L.addFirst(1);
            M.addFirst(1);
        }
        M.addFirst(1);


        System.out.println(L.equals(M));

        /*for(int i = 0; i < 50; i++){
            L.removeFirst();
        }*/


        /*L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(4);
        L.addFirst(4);
        L.addLast(8);
        L.addLast(9);
        L.removeFirst();
        L.removeFirst();
        L.removeFirst();
        L.removeFirst();
        L.removeFirst();
        L.removeFirst();
        L.removeFirst();
        L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(4);*/
    }
}
