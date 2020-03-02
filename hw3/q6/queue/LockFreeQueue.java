package queue;

import java.util.concurrent.atomic.AtomicReference;

import LockFreeStack.Node;

public class LockFreeQueue implements MyQueue {
    // you are free to add members
	AtomicReference<Node> Head;
	AtomicReference<Node> Tail;

    public LockFreeQueue() {
        // implement your constructor here
    }

    public boolean enq(Integer value) {
        // implement your enq method here
    	Node node = new Node(value);
    	node.next = null;
    	while(true) {
    		tail = this.Tail;
    		next = tail.next;
    		if(tail == this.Tail) {
    			if(next == null) {
    				if(tail.next.compareAndSet(next, node)) {
    					break;
    				}
    			}
    			else {
    				this.Tail.compareAndSet(this.tail, next);
    			}
    		}
    	}
    	this.Tail.compareAndSet(tail, node);
        return true;
    }

    public Integer deq() {
        // implement your deq method here
    	Integer val = null;
    	while(true) {
    		head = this.Head;
    		tail = this.Tail;
    		next = head.next;
    		if (head == this.Head) {
    			if(head == tail) {
    				if(next == null) {
    					return null;
    				}
    			}
    			else {
    				this.Tail.compareAndSet(tail, next);
    			}
    		}
    		else {
    			val = next.value;
    			if (this.Head.compareAndSet(head, next)) {
    				break;
    			}
    		}
    	}
        return val;
    }

    protected class Node {
        public Integer value;
        public AtomicReference<Node> next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }
}
