package queue;

import stack.LockFreeStack;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements MyQueue {
    AtomicReference<Node> s=new AtomicReference<>(),t=new AtomicReference<>();
    public LockFreeQueue() {

    }

    public boolean enq(Integer value) {
        Node n = new Node(value);
        boolean success=false;
        while(!success){
            Node currPoint = s.get();
            n.next=currPoint;
            if()
            currPoint.last=n;
            success=s.compareAndSet(currPoint,n);
        }
    }

    public Integer deq() {
        boolean success=false;
        Node currPoint;
        while(!success){
            currPoint = t.get();
            success=t.compareAndSet(currPoint,currPoint.last);
        }
        return currPoint;
    }

    protected class Node {
        public Integer value;
        public Node next;
        public boolean isTip;
        public Node last;

        public Node(Integer x) {
            value = x;
            next = null;
            isTip=false;
        }
    }
}
