package q5;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet implements ListSet {
    // you are free to add members
    ReentrantLock RL = new ReentrantLock();
    Node s,t;
    AtomicInteger x= new AtomicInteger(0);
    public CoarseGrainedListSet() {
        s = new Node(0);
        s.isTip=true;
        t = new Node(0);
        t.isTip=true;
        s.next=t;
    }

    public boolean add(int value) {
        RL.lock();
        System.out.println(x.incrementAndGet());
        Node curr=s;
        while(curr.next.isTip==false && curr.next.value<value){curr=curr.next;}
        if(curr.next.value==value) {RL.unlock();return false;}
        Node newN = new Node(value);
        newN.next=curr.next;
        curr.next=newN;
        RL.unlock();
        return true;
    }

    public boolean remove(int value) {
        RL.lock();
        Node curr=s;
        while(!curr.next.isTip && curr.next.value<value)curr=curr.next;
        if(curr.next.isTip || curr.next.value!=value){
            RL.unlock();
            return false;
        }
        else{
            curr.next = curr.next.next;
            RL.unlock();
            return true;
        }
    }

    public boolean contains(int value) {
        // implement your contains method here
        RL.lock(); //todo no need to lock bc read?
        Node curr=s;
        while(s!=t){
            if(!curr.isTip && curr.value==value){RL.unlock();return true;}
            curr=curr.next;
        }
        RL.unlock();
        return false;
    }

    protected class Node {
        public Integer value;
        public Node next;
        public boolean isTip;
        public Node(Integer x) {
            isTip=false;
            value = x;
            next = null;
        }
    }

    /*
      return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
      check simpleTest for more info
    */
    public String toString() {
        String out="";
        Node curr=s.next;
        while(curr!=t) out+= ((Integer)curr.value).toString()+",";
        return "";
    }
}
