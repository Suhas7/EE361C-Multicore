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
        s.lock.lock();
        Node curr=s;
        
        while(curr != t && curr.next.value < value){
            curr.next.lock.lock();
            curr.lock.unlock();
            curr=curr.next;
        }
        if(curr.next.value==value) {
        	curr.lock.unlock();
        	return false;
        }
        
        Node newN = new Node(value);
        newN.next=curr.next;
        curr.next=newN;
        curr.lock.unlock();
        return true;
    }

    public boolean remove(int value) {
        Node curr=s;
        curr.lock.lock();
        curr.next.lock.lock();
        Node last;
        while(curr.next != null && curr.next.value < value){
            curr.next.next.lock.lock();
            last=curr;
            curr=curr.next;
            last.lock.unlock(); //this line occasionally causes illegal state exception
        }
        if(curr.next.value==value && curr != t) {
            curr.next.lock.unlock();
            curr.next=curr.next.next;
            curr.lock.unlock();
            return true;
        } 
        else {
            curr.next.lock.unlock();
            curr.lock.unlock();
            return false;
        }
    }

    public boolean contains(int value) {
        Node curr=s.next;
        curr.lock.lock();
        
        while(s!=t){
            if(curr != t && curr.value==value){
                curr.lock.unlock();
                return true;
            }
            curr.next.lock.lock();
            curr.lock.unlock();
            curr=curr.next;
        }
        
        if(t.lock.getHoldCount() > 0) {
        	t.lock.unlock();
        }
        
        return false;
    }

    protected class Node {
        public Integer value;
        public Node next;
        public ReentrantLock lock;
        
        public Node(Integer x) {
            value = x;
            next = null;
            lock = new ReentrantLock();
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
