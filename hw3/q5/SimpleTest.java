package q5;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    @Test
    public void testCoarseGrainedListSet() {
        ListSet list = new CoarseGrainedListSet();
        makeThread(list);
        checkNode(0, 4000, list);
    }

    @Test
    public void testCoarseGrainedListSetRemoval() {
        ListSet list = new CoarseGrainedListSet();
        makeThread(list);
        makeThread_B(list);
        checkRemoval(list);
    }

    @Test
    public void testFineGrainedListSet() {
        ListSet list = new FineGrainedListSet();
        makeThread(list);
        checkNode(0, 4000, list);
    }

    @Test
    public void testFineGrainedListSetRemoval() {
        ListSet list = new FineGrainedListSet();
        makeThread(list);
        makeThread_B(list);
        checkRemoval(list);
    }

    @Test
    public void testLockFreeListSet() {
        ListSet list = new LockFreeListSet();
        makeThread(list);
        checkNode(0, 4000, list);
    }

    @Test
    public void testLockFreeListSetRemoval() {
        ListSet list = new LockFreeListSet();
        makeThread(list);
        makeThread_B(list);
        checkRemoval(list);
    }

    private void makeThread_B(ListSet list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new Thread_B(0, 4000, list));
        threads[1] = new Thread(new Thread_B(1001, 2000, list));
        threads[2] = new Thread(new Thread_B(2001, 3000, list));
        threads[1].start(); threads[0].start(); threads[2].start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkRemoval(ListSet list) {
        Assert.assertEquals(new StringBuilder().toString(), list.toString());
    }

    private void makeThread(ListSet list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread(0, 2000, list));
        threads[1] = new Thread(new MyThread(0, 3000, list));
        threads[2] = new Thread(new MyThread(3000, 4000, list));
        threads[1].start(); threads[0].start(); threads[2].start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkNode(int start, int end, ListSet list) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= end; ++i) {
            sb.append(i).append(",");
        }
        Assert.assertEquals(list.toString(), sb.toString());
    }

    private class MyThread implements Runnable {

        int begin;
        int end;
        ListSet list;

        MyThread(int begin, int end, ListSet list) {
            this.begin = begin;
            this.end = end;
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = begin; i <= end; ++i) {
                list.add(i);
                Assert.assertTrue(list.contains(i));
            }
        }
    }

    private class Thread_B implements Runnable {
        int begin;
        int end;
        ListSet list;
        Thread_B(int begin, int end, ListSet list) {
            this.begin = begin;
            this.end = end;
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = end; i >= begin; --i) {
                list.remove(i);
                Assert.assertFalse(list.contains(i));
            }
        }
        
    }

}
