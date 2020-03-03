package queue;

import org.junit.Test;

public class QueueTest {

    @Test
    public void queueTest() {
        MyQueue list = new LockFreeQueue();
        makeThread(list);
        checkNode(list);
        makeRemovingThread(list);
        checkNode(list);
        //makeThread(list);
        //checkNode(list);
    }

    private void makeThread(MyQueue list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread(0, 5, list));
        threads[1] = new Thread(new MyThread(0, 7, list));
        threads[2] = new Thread(new MyThread(4, 7, list));
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
        threads[0] = new Thread(new MyThread2(1, list));
        threads[1] = new Thread(new MyThread2(2, list));
        threads[2] = new Thread(new MyThread2(3, list));
        threads[1].start(); threads[0].start(); threads[2].start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkNode(MyQueue list) {
        System.out.println(list.toString());
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
            for (int i = begin; i <= end; ++i) {
                list.enq(i);
            }
        }
    }
    private class MyThread2 implements Runnable {

        int begin;
        int end;
        MyQueue list;

        MyThread2(int remove, MyQueue list) {
            this.begin = remove;
            this.end = end;
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < begin; ++i) {
                try {
                    System.out.println(list.deq());
                } catch (Exception emptyStack) {
                    emptyStack.printStackTrace();
                }
            }
        }
    }
}
