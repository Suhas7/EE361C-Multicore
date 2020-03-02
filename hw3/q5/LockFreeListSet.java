package q5;

import java.util.concurrent.atomic.AtomicReference;

import LockFreeStack.Node;

public class LockFreeListSet implements ListSet {
	AtomicReference<Node> start = new Node(null);
	AtomicReference<Node> end = new Node(null);

    public LockFreeListSet() {
    	start.next = end;
    }

    public boolean add(int value) {
        Node curr=start;
        Node node = new Node(value);
        
        while (true) {
	        while(!curr.next != end && curr.next.value<value && cur.next != null) {
	        	curr=curr.next;
	        }
	        if(curr.next.value == value || cur.next == null) {
	        	return false;
	        }
	        next = curr.next;
	        if(next == cur.next && next != null) {
	        	if(curr.next.compareAndSet(cur.next, node)) {
	        		node.next = cur.next;
	        		break;
	        	}
	        }
        }
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
        public AtomicReference<Node> next;
        public Node(Integer x) {
            value = x;
            next = null;
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
