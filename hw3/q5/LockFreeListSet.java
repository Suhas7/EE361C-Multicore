package q5;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeListSet implements ListSet {
	Node start = new Node(Integer.MIN_VALUE);
	Node end = new Node(Integer.MAX_VALUE);

    public LockFreeListSet() {
    	start.next.set(end, false);
    }

    public boolean add(int value) {
    	Node node = new Node(value);

        while(true) {
        	Node prev = start;
        	Node curr = start.next.getReference();

        	while(curr != end && curr.value < value) {
        		prev = curr;
        		curr = curr.next.getReference();
        	}
        	if(curr.value == value) {
        		return false;
        	}
        	node.next.set(curr, false);
        	if(prev.next.compareAndSet(curr, node, false, false)) {
        		return true;
        	}
        }
    }

    public boolean remove(int value) {
        while(true) {
        	Node curr = start.next.getReference();
        	Node prev = start;
        	
        	while(curr != end && curr.value < value) {
        		prev = curr;
        		curr = curr.next.getReference();
	        }
	        if(curr.value != value || curr.next.isMarked()){
	            return false;
	        }
	        else{
	        	if(curr.next.compareAndSet(curr.next.getReference(), curr.next.getReference(), false, true)) {
	        		if(prev.next.compareAndSet(prev.next.getReference(), curr.next.getReference(), false, false)) {
	        			return true;
	        		}
	        	}
	        }
        }
    }

    public boolean contains(int value) {
        // implement your contains method here
    	Node curr = start.next.getReference();
    	while(curr != end && curr.value < value) {
    		curr = curr.next.getReference();
        }
        if(curr.value != value || curr.next.isMarked()){
            return false;
        }
        else{
        	return true;
        }
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
    	Node curr = start.next.getReference(); 
        while(curr != end) {
            out += ((Integer)curr.value).toString()+",";
            curr = curr.next.getReference();
        }
        return out;
    }
}
