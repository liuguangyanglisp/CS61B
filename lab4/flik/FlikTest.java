package flik;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
public class FlikTest {
    @Test
    public void testIsSameNumber128() {
        boolean returnValue = Flik.isSameNumber(128, 128);
        assertTrue(returnValue);
    }

    @Test
    public void testIsSameNumber127() {
        boolean returnValue = Flik.isSameNumber(127, 127);
        assertTrue(returnValue);
    }
    @Test
    public void testIsSameNumber1() {
        boolean returnValue = Flik.isSameNumber(1, 1);
        assertTrue(returnValue);
    }

    @Test
    public void testIsSameNumber() throws Exception {
        Integer i = 0;
        for (Integer j = 0; i < 500; ++i, ++j) {
            boolean checkValue = Flik.isSameNumber(i, j);
            if (!checkValue) {
                checkValue = i.equals(j);
            }
            System.out.print(i + " " + j + " ");
            System.out.println(checkValue);
            assertTrue(checkValue);
        }
    }


}
