package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> bug = new BuggyAList<>();
        AListNoResizing<Integer> noResizing = new AListNoResizing<>();
        bug.addLast(4);
        bug.addLast(5);
        bug.addLast(6);
        noResizing.addLast(4);
        noResizing.addLast(5);
        noResizing.addLast(6);
        bug.removeLast();
        noResizing.removeLast();
        assertEquals(bug.getLast(),noResizing.getLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int AListNoResizingSize = L.size();
                int BuggyAListSize = B.size();
                assertEquals(AListNoResizingSize, BuggyAListSize);
            }

            if (L.size() == 0) {
                continue;
            }

            if (operationNumber == 2) {
                //getLast
                Integer AListNoResizingGetLastItem = L.getLast();
                Integer BuggyAListGetLastItem = B.getLast();
                assertEquals(AListNoResizingGetLastItem, BuggyAListGetLastItem);
            } else if (operationNumber == 3) {
                //removeLast
                Integer AListNoResizingRemoveLastItem = L.removeLast();
                Integer BuggyAListRemoveLastItem = B.removeLast();
                assertEquals(AListNoResizingRemoveLastItem,BuggyAListRemoveLastItem);
            }
        }

    }
}
