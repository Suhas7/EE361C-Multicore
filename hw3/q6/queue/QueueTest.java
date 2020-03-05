package queue;

import org.junit.Test;

public class QueueTest {

    @Test
    public void queueTest() {
        MyQueue list = new LockQueue();
        makeThread(list);
        checkNode((LockQueue) list);
        makeRemovingThread(list);
        checkNode((LockQueue) list);
    }

    private void makeThread(MyQueue list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread(401, 800, list));
        threads[1] = new Thread(new MyThread(501, 1000, list));
        threads[2] = new Thread(new MyThread(6, 7, list));
        threads[1].start(); threads[0].start(); threads[2].start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void makeRemovingThread(MyQueue list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread2(0, list));
        threads[1] = new Thread(new MyThread2(450, list));
        threads[2] = new Thread(new MyThread2(450, list));
        threads[1].start(); threads[0].start(); threads[2].start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkNode(LockQueue list) {
        System.out.println("Final List: " + list.toStringTest());
    }

    private class MyThread implements Runnable {
        int begin;
        int end;
        MyQueue list;
        MyThread(int begin, int end, MyQueue list) {
            this.begin = begin;
            this.end = end;
            this.list = list;
        }
        @Override
        public void run() {
            for (int i = begin; i <= end; i++) {
                list.enq(i);
            }
        }
    }
    private class MyThread2 implements Runnable {
        int begin;
        MyQueue list;
        MyThread2(int remove, MyQueue list) {
            this.begin = remove;
            this.list = list;
        }
        @Override
        public void run() {
            for (int i = 0; i < begin; ++i) {
                try {
                	System.out.println(list.deq());
                	//list.deq();
                } catch (Exception emptyStack) {
                    emptyStack.printStackTrace();
                }
            }
        }
    }
}
