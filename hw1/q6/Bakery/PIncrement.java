package q6.Bakery;

public class PIncrement implements Runnable{
	static Integer itg;
    static BakeryLock bakery;
    int internalCounter;
    int pid;
    
	public PIncrement(int internalC, int pid) {
        this.internalCounter = internalC;
    }

	@Override
	public void run() {
		for(int i = 0; i<internalCounter;i++) {
			bakery.lock(pid);
            itg++;
            bakery.unlock(pid);
        }
	}
	
    public static int parallelIncrement(int c, int numThreads){
    	bakery = new BakeryLock(numThreads);
    	itg = 0;
    	
    	for(int i = 0; i < numThreads; i++){
    		PIncrement currJob;
            if(i!=0) currJob=new PIncrement(c/numThreads, i);
            else currJob=new PIncrement((c/numThreads)+(c%numThreads), i);
            currJob.run();
        }
    	return itg;
    }
}
