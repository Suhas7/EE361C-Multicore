package queue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeQueue implements MyQueue {
    // you are free to add members
	AtomicStampedReference<Node> Head = new AtomicStampedReference<Node>(new Node(null), 0);
	AtomicStampedReference<Node> Tail = new AtomicStampedReference<Node>(new Node(null), 0);

    public boolean enq(Integer value) {
        // implement your enq method here
    	Node node = new Node(value);
    	AtomicStampedReference<Node> tail = this.Tail;
		AtomicStampedReference<Node> next = tail.getReference().next;
    	while(true) {
    		tail = this.Tail;
    		next = tail.getReference().next;
    		if(tail.getReference() == this.Tail.getReference()) {
    			if(next.getReference() == null) {
    				if(tail.compareAndSet(next.getReference(), node, tail.getStamp(), tail.getStamp()+1)) {
    					break;
    				}
    			}
    			else {
    				this.Tail.compareAndSet(tail.getReference(), next.getReference(), tail.getStamp(), tail.getStamp()+1);
    			}
    		}
    	}
    	this.Tail.compareAndSet(tail.getReference(), node, tail.getStamp(), tail.getStamp()+1);
        return true;
    }

    public Integer deq() {
        // implement your deq method here
    	Integer val = null;
    	while(true) {
    		AtomicStampedReference<Node> head = this.Head;
    		AtomicStampedReference<Node> tail = this.Tail;
    		AtomicStampedReference<Node> next = head.getReference().next;
    		if (head.getReference() == this.Head.getReference()) {
    			if(head.getReference() == tail.getReference()) {
    				if(next.getReference() == null) {
    					return null;
    				}
    			}
    			else {
    				this.Tail.compareAndSet(tail.getReference(), next.getReference(), tail.getStamp(), tail.getStamp()+1);
    			}
    		}
    		else {
    			val = next.getReference().value;
    			if (this.Head.compareAndSet(head.getReference(), next.getReference(), head.getStamp(), head.getStamp()+1)) {
    				break;
    			}
    		}
    	}
        return val;
    }

    protected class Node {
        public Integer value;
        public AtomicStampedReference<Node> next;

        public Node(Integer x) {
        	int count = 0;
            value = x;
            next = new AtomicStampedReference<Node>(null, count);
        }
    }
}
