package q5;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedListSet implements ListSet {
    Node s;
    Node t;
    
    public FineGrainedListSet() {
        s = new Node(Integer.MIN_VALUE);
        t = new Node(Integer.MAX_VALUE);
        s.next=t;
    }

    public boolean add(int value) {
    	Node curr = s.next;
        Node prev = s;
        
        prev.lock.lock();
        curr.lock.lock();
        while(true) {
	        while(curr != t && curr.value < value){
	        	prev.lock.unlock();
	            prev = curr;
	            curr = curr.next;
	            curr.lock.lock();
	        }
	        if(curr.value == value) {
	        	prev.lock.unlock();
	        	curr.lock.unlock();
	        	return false;
	        }
	        if(!(curr.isDeleted || prev.isDeleted || prev.next != curr)) {
		        Node node = new Node(value);
		        node.next = curr;
		        prev.next = node;
		        prev.lock.unlock();
		        curr.lock.unlock();
		        return true;
	        }
	        else {
	        	prev.lock.unlock();
	        	curr.lock.unlock();
	        }
        }
    }

    public boolean remove(int value) {
    	Node curr = s.next;
        Node prev = s;
        
        prev.lock.lock();
        curr.lock.lock();
        while(true) {
	        while(curr != t && curr.value < value){
	        	prev.lock.unlock();
	            prev = curr;
	            curr = curr.next;
	            curr.lock.lock();
	        }
	        if(curr.value == value ) {
	        	if(!(curr.isDeleted || prev.isDeleted || prev.next != curr)) {
		        	curr.isDeleted = true;
		        	prev.next = curr.next;
		        	prev.lock.unlock();
		        	curr.lock.unlock();
		        	return true;
	        	}
	        	else {
	        		prev.lock.unlock();
		        	curr.lock.unlock();
	        	}
	        }
	        else {
	        	prev.lock.unlock();
	        	curr.lock.unlock();
	        	return false;
	        }
        }
    }

    public boolean contains(int value) {
    	Node curr = s.next;
        Node prev = s;
        
        prev.lock.lock();
        curr.lock.lock();
        while(curr != t && curr.value < value){
        	prev.lock.unlock();
            prev = curr;
            curr = curr.next;
            curr.lock.lock();
        }
        if(curr.value == value && !(curr.isDeleted || prev.isDeleted || prev.next != curr)) {
        	prev.lock.unlock();
        	curr.lock.unlock();
        	return true;
        }
        else {
        	prev.lock.unlock();
        	curr.lock.unlock();
        	return false;
        }
        
    }

    protected class Node {
        public Integer value;
        public Node next;
        public ReentrantLock lock;
        Boolean isDeleted;
        
        public Node(Integer x) {
            value = x;
            next = null;
            lock = new ReentrantLock();
            isDeleted = false;
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
