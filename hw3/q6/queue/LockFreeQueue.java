package queue;

import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeQueue implements MyQueue {
	AtomicStampedReference<Node> Head;
	AtomicStampedReference<Node> Tail;

	public LockFreeQueue() {
		Node node = new Node(0);
		Head = new AtomicStampedReference<Node>(node, 0);
		Tail = new AtomicStampedReference<Node>(node, 0);
	}
    public boolean enq(Integer value) {
        // implement your enq method here
    	Node node = new Node(value);
    	AtomicStampedReference<Node> tail = this.Tail;
		AtomicStampedReference<Node> next = tail.getReference().next;
    	while(true) {
    		tail = this.Tail;
    		next = tail.getReference().next;
    		if(tail == this.Tail) {
    			if(next.getReference() == null) {
    				if(tail.getReference().next.compareAndSet(null, node, next.getStamp(), next.getStamp()+1)) {
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
    				this.Tail.compareAndSet(tail.getReference(), next.getReference(), tail.getStamp(), tail.getStamp()+1);
    			}
	    		else {
	    			val = next.getReference().value;
	    			if (this.Head.compareAndSet(head.getReference(), next.getReference(), head.getStamp(), head.getStamp()+1)) {
	    				return val;
	    			}
	    		}
	    	}
    	}
    }
	public String toStringTest() {
		String out = "";
		if(Head.getReference().next.getReference() == null) {
			return out;
		}
		AtomicStampedReference<Node> x = this.Head.getReference().next;
		while (x.getReference() != Tail.getReference()) {
			out += ((Integer) x.getReference().value).toString() + ",";
			x = x.getReference().next;
		}
		out += ((Integer) x.getReference().value).toString()+ ",";
		return out;
	}
    protected class Node {
        public Integer value;
        public AtomicStampedReference<Node> next;

        public Node(Integer x) {
            value = x;
            next = new AtomicStampedReference<Node>(null, 0);
        }
    }
}
