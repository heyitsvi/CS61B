package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  @Test
  public void testThreeAddThreeRemove(){
    AListNoResizing<Integer> L1 = new AListNoResizing<>();
    L1.addLast(5);
    L1.addLast(4);
    L1.addLast(3);
    BuggyAList<Integer> L2 = new BuggyAList<>();
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
  public void randomizedTest(){
    AListNoResizing<Integer> L = new AListNoResizing<>();
    BuggyAList<Integer> L2 = new BuggyAList<>();

    int N = 5000;
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
