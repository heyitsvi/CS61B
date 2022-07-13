package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by hug.
 */
public class ArrayDequeTest {
    @Test
    public void testThreeAddThreeRemove(){
        ArrayDeque<Integer> L1 = new ArrayDeque<>();
        L1.addLast(5);
        L1.addLast(4);
        L1.addLast(3);
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        L2.addLast(5);
        L2.addLast(4);
        L2.addLast(3);

        L1.removeLast();
        L2.removeLast();

        assertEquals(L1.size(), L2.size());

        L1.removeLast();
        L2.removeLast();

        assertEquals(L1.size(), L2.size());

        L1.removeLast();
        L2.removeLast();

        assertEquals(L1.size(), L2.size());

    }

    @Test
    public void testRemoveLast(){
        ArrayDeque<Integer> L1 = new ArrayDeque<>();
        L1.addLast(5);
        L1.addLast(4);
        L1.addFirst(5);
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        L2.addLast(5);
        L2.addLast(4);
        L2.addFirst(5);

        for(int i = 0; i < 3; i++){
            assertEquals(L1.removeLast(), L2.removeLast());
        }
    }

    @Test
    public void testRemoveFirst(){
        ArrayDeque<Integer> L1 = new ArrayDeque<>();
        L1.addFirst(1);
        L1.addFirst(2);
        L1.addLast(3);
        L1.addLast(4);

        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        L2.addFirst(1);
        L2.addFirst(2);
        L2.addLast(3);
        L2.addLast(4);

        for(int i = 0; i < 3; i++){
            assertEquals(L1.removeFirst(), L2.removeFirst());
        }
    }

    /** Tests for deep equality */
    @Test
    public void deepEqualsTest(){
        int[] a = new int[]{1, 2, 3, 4, 5};
        int[] b = new int[]{1, 2, 3, 4, 6};

        ArrayDeque<int[]> L1 = new ArrayDeque<>();
        ArrayDeque<int[]> L2 = new ArrayDeque<>();

        L1.addFirst(a);
        L1.addFirst(b);
        L2.addFirst(a);
        L2.addFirst(b);

        assertTrue("should be equal", L1.equals(L2));

    }

    /*@Test
    public void checkArrayUpsize(){
        ArrayDeque<Integer> L1 = new ArrayDeque<>();

        for(int i = 0; i <= 8; i++){
            L1.addFirst(1);
        }

        assertEquals(16, L1.size());
    }*/

    /*@Test
    public void checkArrayDownsize(){
        ArrayDeque<Integer> L1 = new ArrayDeque<>();

        for(int i = 0; i <= 32; i++){
            L1.addFirst(1);
        }

        for(int i = 0; i <= 18; i++){
            L1.removeFirst();
        }

        assertEquals(32, L1.getArrayLength());

        for(int i = 0; i <= 6; i++){
            L1.removeLast();
        }

        assertEquals(16, L1.getArrayLength());
    }*/

    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();

        int N = 100000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2 = L2.size();
                assertEquals(size,size2);
            } else if (operationNumber == 2){
                if(L.size() > 0){
                    assertEquals(L.size(), L2.size());
                }
            } else{
                if(L.size() > 0){
                    int lastElementProperList = L.removeLast();
                    int lastElementBuggyList = L2.removeLast();
                    assertEquals(lastElementProperList,lastElementBuggyList);
                }
            }
        }
    }

}
