package deque;

public class ArrayDeque<Type> {
    private Type[] items;
    private int size;

    private int firstIndex;

    private int lastIndex;

    public ArrayDeque(){
        items = (Type[]) new Object[8];
        firstIndex = 0;
        size = 0;
        lastIndex = size;
    }

    public void resize(int capacity){
        Type[] newArr = (Type[]) new Object[capacity];
        System.arraycopy(items, 0, newArr,0, lastIndex);
        System.arraycopy(items, firstIndex, newArr,(lastIndex + (capacity - items.length)), items.length - lastIndex);
        firstIndex = lastIndex + (capacity - items.length) - 1;
        items = newArr;
    }

    public void addFirst(Type item){
        if (size == items.length){
            resize(size * 2);
        }
        if(firstIndex == 0 && size < items.length){
            items[items.length - 1] = item;
            firstIndex = items.length - 2;
        }else{
            items[firstIndex] = item;
            firstIndex -= 1;
        }

        size += 1;
    };

    public void addLast(Type item){
        if (size == items.length){
            resize(size * 2);
        }

        if (lastIndex >= items.length && size < items.length){
            items[0] = item;
            lastIndex = 0;
        } else{
            items[lastIndex] = item;
            lastIndex += 1;
        }

        size += 1;
    };

    public double getUsageRatio(){
        return ((size * 1.0) / items.length);
    }

    public Type removeFirst(){
        //System.out.println(getUsageRatio());

        if(isEmpty()){
            return null;
        }
        if (getUsageRatio() < 0.25 && items.length > 16){
            resize(items.length / 2);
        }

        Type item;
        if (firstIndex == items.length - 1){
            item = items[0];
            items[0] = null;
            firstIndex = 0;
        }else{
            item = items[firstIndex + 1];
            items[firstIndex + 1] = null;
            firstIndex += 1;
        }

        size -= 1;

        return item;
    };

    public Type removeLast(){
        if(isEmpty()){
            return null;
        }
        //System.out.println(getUsageRatio());
        if (getUsageRatio() < 0.25 && items.length > 16){
            resize(items.length / 2);
        }

        Type item;
        if(lastIndex - 1 < 0){
            item = items[items.length - 1];
            items[items.length - 1] = null;
            lastIndex = items.length - 1;
        } else{
            item = items[lastIndex - 1];
            items[lastIndex - 1] = null;
            lastIndex -= 1;
        }

        size -= 1;
        return item;
    };

    public Type get(int i){
        return items[i];
    };

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> L = new ArrayDeque<>();

        L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(4);
        L.addFirst(5);
        L.addLast(6);
        L.addLast(7);
        L.addLast(8);
        L.addLast(9);
        L.addFirst(6);
        L.addLast(9);
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.removeLast();

    }
}
