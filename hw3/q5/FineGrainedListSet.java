package q5;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedListSet implements ListSet {
    // you are free to add members

    public FineGrainedListSet() {
        // implement your constructor here
    }

    public boolean add(int value) {
        // implement your add method here
        return false;
    }

    public boolean remove(int value) {
        // implement your remove method here
        return false;
    }

    public boolean contains(int value) {
        // implement your contains method here
        return false;
    }

    protected class Node {
        public Integer value;
        public Node next;
        public ReentrantLock lock;
        public Node(Integer x) {
            value = x;
            next = null;
            lock= new ReentrantLock();
        }
    }

    /*
      return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
      check simpleTest for more info
    */
    public String toString() {
        return "";
    }
}
