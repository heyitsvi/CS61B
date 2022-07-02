package deque;

public class ArrayDeque<Type> {
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

    /*public void resize(int capacity){
        Type[] newArr = (Type[]) new Object[capacity];
        System.arraycopy(items, 0, newArr,0, lastIndex);
        System.arraycopy(items, firstIndex, newArr,(lastIndex + (capacity - items.length)), items.length - lastIndex);
        firstIndex = lastIndex + (capacity - items.length) - 1;
        items = newArr;
    }*/

    public void addFirst(Type item){
        /*if (size == items.length){
            resize(size * 2);
        }*/

        if(nextFirst < 0){
            nextFirst = items.length - 1;
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        size += 1;
    };

    public void addLast(Type item){
        /*if (size == items.length){
            resize(size * 2);
        }*/

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

    private boolean reverse = false;
    public Type removeFirst(){
        //System.out.println(getUsageRatio());

        if(isEmpty()){
            return null;
        }
        /*if (getUsageRatio() < 0.25 && items.length > 16){
            resize(items.length / 2);
        }*/

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

        System.out.println("First item removed: " + item);
        return item;
    };

    public Type removeLast(){
        if(isEmpty()){
            return null;
        }
        //System.out.println(getUsageRatio());
        /*if (getUsageRatio() < 0.25 && items.length > 16){
            resize(items.length / 2);
        }*/

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

        System.out.println("Last item removed: " + item);
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
        L.addLast(6);
        L.addLast(7);
        L.addFirst(1);
        L.addFirst(2);
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.removeLast();
        L.addLast(6);
        L.addLast(7);
        L.addLast(8);
        L.addLast(9);

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
