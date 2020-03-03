package q5;

import java.util.concurrent.atomic.AtomicMarkableReference;


public class LockFreeListSet implements ListSet {
	AtomicMarkableReference<Node> start = new AtomicMarkableReference<Node>(new Node(-1), false);
	AtomicMarkableReference<Node> end = new AtomicMarkableReference<Node>(new Node(-1), false);

    public LockFreeListSet() {
    	start.getReference().next.set(end.getReference(), false);
    }

    public boolean add(int value) {
    	AtomicMarkableReference<Node> node = new AtomicMarkableReference<Node>(new Node(value), false);
    	AtomicMarkableReference<Node> curr = start;
        while(true) {
        	while(curr.getReference().next.getReference() != end.getReference() && curr.getReference().next.getReference().value < value) {
        		curr = curr.getReference().next;
        	}
        	if(curr.getReference().next.getReference().value == value || curr.getReference() == end.getReference()) {
        		return false;
        	}
        	node.getReference().next.set(curr.getReference().next.getReference(), false);
        	if(curr.getReference().next.compareAndSet(curr.getReference().next.getReference(), node.getReference(), false, false)) {
        		return true;
        	}
        }
    }

    public boolean remove(int value) {
    	// when you delete a node, make its next equal to true
    	//if cur.next is right, then we delete it
    	AtomicMarkableReference<Node> curr = start;
        while(true) {
        	while(curr.getReference().next.getReference() != end.getReference() && curr.getReference().next.getReference().value < value) {
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
            next = new AtomicMarkableReference<Node>(null, false);
        }
    }

    /*
      return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
      check simpleTest for more info
    */
    public String toString() {
    	String out = "";
    	AtomicMarkableReference<Node> curr = start.getReference().next;
        while(curr.getReference() != end.getReference()) {
            out += ((Integer)curr.getReference().value).toString()+",";
            curr = curr.getReference().next;
        }
        return out;
    }
}
