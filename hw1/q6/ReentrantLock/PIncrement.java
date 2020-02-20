package q6.ReentrantLock;

import java.util.concurrent.locks.ReentrantLock;

public class PIncrement implements Runnable{
	int internalCounter;
	static Integer itg;
    static ReentrantLock re = new ReentrantLock();
    
	public PIncrement(int internalC) {
        this.internalCounter = internalC;
    }

	@Override
	public void run() {
		for(int i = 0; i<internalCounter;i++) {
            re.lock();
            itg++;
            re.unlock();
        }
	}
	
    public static int parallelIncrement(int c, int numThreads){
    	itg = 0;
    	for(int i = 0; i < numThreads; i++){
    		PIncrement currJob;
            if(i!=0) currJob=new PIncrement(c/numThreads);
            else currJob=new PIncrement((c/numThreads)+(c%numThreads));
            currJob.run();
        }
    	return itg;
    }
}
