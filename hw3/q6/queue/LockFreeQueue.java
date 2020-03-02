package queue;

<<<<<<< HEAD
import java.util.concurrent.atomic.AtomicReference;

import LockFreeStack.Node;

public class LockFreeQueue implements MyQueue {
    // you are free to add members
	AtomicReference<Node> Head;
	AtomicReference<Node> Tail;
=======
import stack.LockFreeStack;

import java.util.concurrent.atomic.AtomicReference;
>>>>>>> c60f1c59a5fda9eb77401d1d7be9296715d619de

public class LockFreeQueue implements MyQueue {
    AtomicReference<Node> s=new AtomicReference<>(),t=new AtomicReference<>();
    public LockFreeQueue() {

    }

    public boolean enq(Integer value) {
<<<<<<< HEAD
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
=======
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
>>>>>>> c60f1c59a5fda9eb77401d1d7be9296715d619de
    }

    protected class Node {
        public Integer value;
<<<<<<< HEAD
        public AtomicReference<Node> next;
=======
        public Node next;
        public boolean isTip;
        public Node last;
>>>>>>> c60f1c59a5fda9eb77401d1d7be9296715d619de

        public Node(Integer x) {
            value = x;
            next = null;
            isTip=false;
        }
    }
}
