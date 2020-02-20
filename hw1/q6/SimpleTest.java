package q6;

import org.junit.Test;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.*;

public class SimpleTest {
	int x=1;

	@Test
	public void TestBakery() {
	    long t=currentTimeMillis();
		for(int i = 1; i < 9; i++) {
			int res = q6.Bakery.PIncrement.parallelIncrement(1200000, x);
			assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
		}
        System.out.println("Bakery "+((Long) (currentTimeMillis()-t)).toString());
	}

	@Test
	public void TestAtomicInteger() {
        long t=currentTimeMillis();
		for(int i = 1; i < 9; i++) {
			int res = q6.AtomicInteger.PIncrement.parallelIncrement(1200000, x);
			assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
		}
        System.out.println("Atomic "+((Long) (currentTimeMillis()-t)).toString());
	}

	@Test
	public void TestSynchronized() {
        long t=currentTimeMillis();
		for(int i = 1; i < 9; i++) {
			int res = q6.Synchronized.PIncrement.parallelIncrement(1200000, x);
			assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
		}
        System.out.println("Synchronized "+((Long) (currentTimeMillis()-t)).toString());
	}

	@Test
	public void TestReentrantLock() {
        long t=currentTimeMillis();
		for(int i = 1; i < 9; i++) {
			int res = q6.ReentrantLock.PIncrement.parallelIncrement(1200000, x);
			assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
		}
        System.out.println("Reentrant "+((Long) (currentTimeMillis()-t)).toString());
	}
}
