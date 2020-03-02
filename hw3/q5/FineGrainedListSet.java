package q5;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedListSet implements ListSet {
    Node s,t;
    public FineGrainedListSet() {
        s = new Node(0);
        s.isTip=true;
        t = new Node(0);
        t.isTip=true;
        s.next=t;
    }

    public boolean add(int value) {
        s.lock.lock();
        Node curr=s;
        while(!curr.next.isTip && curr.next.value<value){
            curr.next.lock.lock();
            curr.lock.unlock();
            curr=curr.next;
        }
        if(curr.next.value==value) {curr.lock.unlock();return false;}
        Node newN = new Node(value);
        newN.next=curr.next;
        curr.next=newN;
        curr.lock.unlock();
        return true;
    }

    public boolean remove(int value) {
        s.lock.lock();
        Node curr=s;
        while(!curr.next.isTip && curr.next.value<value){
            curr.next.lock.lock();
            curr.lock.unlock();
            curr=curr.next;
        }
        if(curr.next.value==value) {
            curr.next=curr.next.next;
            curr.lock.unlock();
            return true;
        }else {
            curr.lock.unlock();
            return false;
        }
    }

    public boolean contains(int value) {
        Node curr=s.next;
        curr.lock.lock();
        while(s!=t){
            if(!curr.isTip && curr.value==value){
                curr.lock.unlock();
                return true;
            }
            curr.next.lock.lock();
            curr.lock.unlock();
            curr=curr.next;
        }
        if(t.lock.getHoldCount()>0) t.lock.unlock();
        return false;
    }

    protected class Node {
        public Integer value;
        public Node next;
        public ReentrantLock lock;
        public boolean isTip;
        public Node(Integer x) {
            value = x;
            next = null;
            lock = new ReentrantLock();
            isTip = false;
        }
    }

    /*
      return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
      check simpleTest for more info
    */
    public String toString() {
        String out="";
        Node curr=s.next;
        while(curr!=t) {
            out+= ((Integer)curr.value).toString()+",";
            curr=curr.next;
        }
        return out;
    }
}
