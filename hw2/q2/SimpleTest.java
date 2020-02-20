package q2;
import org.junit.Assert;
import org.junit.Test;

import static java.lang.System.nanoTime;

public class SimpleTest {

    private static final int OPERATIONS = 120000;

    @Test
    public void testCLH() {
        for(int i =1; i<9; i++) {
            long t = nanoTime();
            int result = q2.a.PIncrement.parallelIncrement(0, i);
            System.out.println("CLH:  " + ((Long) (nanoTime() - t)).toString());
        }
    }

    @Test
    public void testLamport() {
        for(int i =1; i<9; i++) {
            long t = nanoTime();

            int result = q2.b.PIncrement.parallelIncrement(0, i);
            System.out.println("Lamport " + ((Long) (nanoTime() - t)).toString());
        }
    }

    @Test
    public void testAnderson() {
        for(int i =1; i<9; i++) {
            long t = nanoTime();
            int result = q2.c.PIncrement.parallelIncrement(0, i);
            System.out.println("Anderson " + ((Long) (nanoTime() - t)).toString());
        }
    }

}
