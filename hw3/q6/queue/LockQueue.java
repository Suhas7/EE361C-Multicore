package queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    // you are free to add members
    ReentrantLock eLock=new ReentrantLock(), dLock=new ReentrantLock();
    Node s,t;
    AtomicInteger count=new AtomicInteger(0);
    //AtomicReference<Node> s= new AtomicReference<>(),t= new AtomicReference<>();
    public LockQueue() {
        // implement your constructor here
        s=t=new Node(null);
    }

    public boolean enq(Integer value) {
        // implement your enq method here
        if(value==null) throw new NullPointerException();
        eLock.lock();
        Node newN=new Node(value);
        t.next=newN;
        t=newN;
        count.incrementAndGet();
        eLock.unlock();
        return true; //todo why
    }

    public Integer deq() {
        // implement your deq method here
        dLock.lock();
        if(s.next==null) throw new EmptyException();
        int result = s.next.value;
        s=s.next;
        count.decrementAndGet();
        dLock.unlock();
        return result;
    }

    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }
}
