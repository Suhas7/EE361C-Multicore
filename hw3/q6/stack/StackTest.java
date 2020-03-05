package stack;

import org.junit.Assert;
import org.junit.Test;

public class StackTest {

    @Test
    public void stackTest() {
        LockFreeStack list = new LockFreeStack();
        makeThread(list);
        checkNode(list);
        makeRemovingThread(list);
        checkNode(list);
    }

    private void makeThread(LockFreeStack list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread(0, 50, list));
        threads[1] = new Thread(new MyThread(0, 50, list));
        threads[2] = new Thread(new MyThread(0, 50, list));
        threads[1].start(); threads[0].start(); threads[2].start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void makeRemovingThread(LockFreeStack list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread2(55, list));
        threads[1] = new Thread(new MyThread2(50, list));
        threads[2] = new Thread(new MyThread2(50, list));
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
        System.out.println("Final string: " + list.toString());
        //System.out.println("Strlen" + list.toString().length());
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
            for (int i = begin; i <= end; i++) {
                list.push(i);
            }
        }
    }
    private class MyThread2 implements Runnable {

        int begin;
        LockFreeStack list;

        MyThread2(int remove, LockFreeStack list) {
            this.begin = remove;
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < begin; i++) {
                try {
                    //System.out.println(list.pop() + ",");
                    list.pop();
                } catch (EmptyStack emptyStack) {
                    emptyStack.printStackTrace();
                }
            }
        }
    }
}
