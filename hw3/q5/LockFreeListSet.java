package q5;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

import LockFreeStack.Node;

public class LockFreeListSet implements ListSet {
	AtomicMarkableReference<Node> start = new AtomicMarkableReference<Node>(new Node(null), false);
	AtomicMarkableReference<Node> end = new AtomicMarkableReference<Node>(new Node(null), false);

    public LockFreeListSet() {
    	start.getReference().next = end.getReference();
    }

    public boolean add(int value) {
    	AtomicMarkableReference<Node> node = new AtomicMarkableReference<Node>(new Node(value), false);
    	AtomicMarkableReference<Node> curr = start;
        while(true) {
        	while(!curr.getReference().next.getReference() != end.getReference() && curr.getReference().next.getReference().value < value && !curr.getReference().next.isMarked()) {
        		curr = curr.getReference().next;
        	}
        	if(curr.getReference().value == value || curr.getReference() == end.getReference()) {
        		return false;
        	}
        	AtomicMarkableReference<Node> next = curr.getReference().next
        	node.getReference().next = next;
        	if(curr.getReference().next.compareAndSet(next, node, false, false)) {
        		break;
        	}
        }
        return true;
    }

    public boolean remove(int value) {
    	// when you delete a node, make its next equal to true
    	//if cur.next is right, then we delete it
    	AtomicMarkableReference<Node> curr = start;
        while(true) {
        	while(!curr.getReference().next.getReference() != end.getReference() && curr.getReference().next.getReference().value < value) {
        		curr = curr.getReference().next;
	        }
	        if(curr.getReference().next.getReference().value != value){
	            return false;
	        }
	        else{
	        	AtomicMarkableReference<Node> next = curr.getReference().next;
	        	if(next.getReference().next.compareAndSet(next.getReference().next.getReference(), next.getReference().next.getReference(), false, true)) {
	        		if(curr.getReference().next.compareAndSet(next.getReference().next.getReference(), next.getReference().next.getReference(), false, false)) {
	        			return true;
	        		}
	        	}
	        }
        }
    }

    public boolean contains(int value) {
        // implement your contains method here
    	AtomicMarkableReference<Node> cur = start.getReference().next;
    	while(cur.getReference() != end.getReference()) {
    		if(cur.getReference().value == value && !cur.getReference().next.isMarked()) {
    			return true;
    		}
    		cur = cur.getReference().next;
    	}
        return false;
    }

    protected class Node {
        public Integer value;
        public AtomicMarkableReference<Node> next;
        public Node(Integer x) {
            value = x;
            next = new AtomicMarkableReference(null, false);
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
