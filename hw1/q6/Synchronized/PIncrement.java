package q6.Synchronized;

public class PIncrement implements Runnable{
	static Integer itg;
	int internalCounter;
	private static final Object lock = new Object();
	
    public PIncrement(int internalC) {
    	this.internalCounter = internalC;
	}

	@Override
	public void run() {
		for(int i = 0; i<internalCounter;i++) {
            synchronized (lock) {
                itg++;
            }
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
