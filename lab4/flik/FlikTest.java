package flik;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
public class FlikTest {
    @Test
    public void testIsSameNumber127() {
        boolean returnValue = Flik.isSameNumber(127, 127);
        assertTrue(returnValue);
    }

    @Test
    public void testIsSameNumber128() {
        boolean returnValue = Flik.isSameNumber(128, 128);
        assertTrue(returnValue);
    }

    @Test
    public void testIsSameNumber() throws Exception {
        Integer i = 0;
        for (Integer j = 0; i < 10000000; ++i, ++j) {
            assertTrue(Flik.isSameNumber(i, j));
        }
    }


}
