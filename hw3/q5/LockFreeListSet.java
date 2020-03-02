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
        
        return true;
    }

    public boolean remove(int value) {
    	// when you delete a node, make its next equal to null
        Node curr=start;
        while(true) {
	        while(!curr.next != end && curr.next.value < value && cur.next != null) {
	        	curr = curr.next;
	        }
	        if(curr.next.isTip || curr.next.value!=value || cur.next == null){
	            return false;
	        }
	        else{
	            curr.next = curr.next.next;
	            return true;
	        }
        }
    }

    public boolean contains(int value) {
        // implement your contains method here
    	Node cur = start.next;
    	while(cur != end) {
    		if(cur.value == value) {
    			return true;
    		}
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
