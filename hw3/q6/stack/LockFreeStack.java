package stack;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack implements MyStack {
    AtomicReference<Node> top = new AtomicReference<>();

    public LockFreeStack() {
        // implement your constructor here
    }

    public boolean push(Integer value) {
        // implement your push method here
        Node newNode=new Node(value);
        boolean success = false;
        while(!success){
            Node last=top.get();
            newNode.next=last;
            success=top.compareAndSet(last, newNode);
        }
        return true;
    }

    public Integer pop() throws EmptyStack {
        // implement your pop method here
        boolean success=false;
        Node last=null;
        while(!success){
            last = top.get();
            if(last==null) throw new EmptyStack();
            success=top.compareAndSet(last, last.next);
        }
        return last.value;
    }

    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }

    public String toStringTest() {
        String out = "";
        Node x = this.top.get();
        while(x != null) {
        	out+=((Integer) x.value).toString() + ",";
        	x=x.next;
        }
        return out;
    }
}
