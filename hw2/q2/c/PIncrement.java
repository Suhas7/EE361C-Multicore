package q2.c;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable{
	static AndersonLock anderson;
	static int itg;
	int internalCounter;
	
	public PIncrement(int internalC) {
        this.internalCounter = internalC;
    }
	
    public static int parallelIncrement(int c, int numThreads) {
    	anderson = new AndersonLock(numThreads);
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
    		anderson.lock();
            itg++;
            anderson.unlock();
        }
	}
    
    public static class AndersonLock {
        AtomicInteger tailSlot = new AtomicInteger (0);
        boolean [] Available ;
        ThreadLocal<Integer> mySlot;
        
        public AndersonLock ( int n ) { // constructor
            Available= new boolean[n];
            mySlot = new ThreadLocal<Integer>();
            mySlot.set(0);
            Available[0] = true;
        }
        public void lock () {
            mySlot.set( tailSlot.getAndIncrement( ) % Available.length ) ;
            while(!Available[mySlot.get()]);
        }
        public void unlock () {
            Available[mySlot.get()] = false ;
            Available[(mySlot.get()+1) % Available.length] = true ;
        }
    }
}
