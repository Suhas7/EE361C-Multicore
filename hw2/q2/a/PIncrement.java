package q2.a;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class PIncrement implements Runnable{
	static CLHLock clh;
	static int itg;
	int internalCounter;
	
	public PIncrement(int internalC) {
        this.internalCounter = internalC;
    }
	
    public static int parallelIncrement(int c, int numThreads) {
    	clh = new CLHLock();
    	itg = c;
    	int max = 120000;
    	ArrayList<Thread> threads = new ArrayList<Thread>();
    	for(int i = 0; i < numThreads; i++){
    		Thread currJob;
            if(i!=0) currJob=new Thread(new PIncrement(max/numThreads));
            else currJob=new Thread(new PIncrement((max/numThreads)+(max%numThreads)));
            currJob.run();
            threads.add(currJob);
        }
    	for(int i = 0; i < numThreads; i++) {
    		try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return itg;
    }
    
    @Override
	public void run() {
    	for(int i = 0; i<internalCounter;i++) {
            clh.lock();
            itg++;
            clh.unlock();
        }
		
	}
    
    public static class CLHLock {
        class Node {
            boolean locked ;
        }
        AtomicReference<Node> tailNode ;
        ThreadLocal<Node> myNode ;
        ThreadLocal<Node> pred ;

        public CLHLock ( ) {
            tailNode = new AtomicReference<Node>(new Node ( ) ) ;
            tailNode.get( ).locked = false ;
            myNode = new ThreadLocal<Node> ( ) {
                protected Node initialValue ( ) {
                    return new Node ( ) ;
                }
            } ;
            pred = new ThreadLocal<Node> ();
        }
        public void lock() {
            myNode.get().locked = true ;
            pred.set(tailNode.getAndSet(myNode.get()));
            while(pred.get().locked){Thread.yield();}
        }
        public void unlock () {
            myNode.get().locked = false ;
            myNode.set( pred.get( ) ) ;
        }
    }
}
