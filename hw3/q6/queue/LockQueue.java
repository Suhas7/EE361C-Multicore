package queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    ReentrantLock eLock=new ReentrantLock(), dLock=new ReentrantLock();
    Node s,t;
    AtomicInteger count=new AtomicInteger(0);
    
    public LockQueue() {
        s=t=new Node(null);
    }

    public boolean enq(Integer value) {
        eLock.lock();
        Node newN=new Node(value);
        t.next=newN;
        t=newN;
        count.incrementAndGet();
        eLock.unlock();
        return true;
    }

    public Integer deq() {
        dLock.lock();
        if(this.count.get()==0) {
        	return null;
        }
        int result = s.next.value;
        s = s.next;
        count.decrementAndGet();
        dLock.unlock();
        return result;
    }
    
    public String toStringTest() {
        String out = "";
        Node x = this.s.next;
        while (x != null) {
            out += ((Integer) x.value).toString() + ",";
            x = x.next;
        }
        return out;
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
