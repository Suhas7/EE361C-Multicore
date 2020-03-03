package stack;

import org.junit.Assert;
import org.junit.Test;
import q5.CoarseGrainedListSet;
import q5.FineGrainedListSet;
import q5.ListSet;
import q5.LockFreeListSet;

public class StackTest {

    @Test
    public void stackTest() {
        LockFreeStack list = new LockFreeStack();
        makeThread(list);
        checkNode(list);

    }

    private void makeThread(LockFreeStack list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread(0, 5, list));
        threads[1] = new Thread(new MyThread(0, 10, list));
        threads[2] = new Thread(new MyThread(10, 30, list));
        threads[1].start(); threads[0].start(); threads[2].start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkNode(LockFreeStack list) {
        System.out.println(list.toString());
    }

    private class MyThread implements Runnable {

        int begin;
        int end;
        LockFreeStack list;

        MyThread(int begin, int end, LockFreeStack list) {
            this.begin = begin;
            this.end = end;
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = begin; i <= end; ++i) {
                list.push(i);
            }
        }
    }
}
