package q6.AtomicInteger;
import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable{
	static AtomicInteger itg = new AtomicInteger();
    int internalCounter;
    
    public PIncrement(int internalC){
        this.internalCounter=internalC;
    }
    
    @Override
	public void run() {
		for(int i=0; i<internalCounter; i++){
            boolean trying = true;
            while(trying) trying=!itg.compareAndSet(itg.get(),itg.get()+1);
        }		
	}
    
    public static int parallelIncrement(int c, int numThreads){
    	itg.set(0);
        for(int i = 0; i < numThreads; i++){
        	PIncrement currJob;
            if(i!=0) currJob=new PIncrement(c/numThreads);
            else currJob=new PIncrement((c/numThreads)+(c%numThreads));
            currJob.run();
        }
        return itg.get();
    }	
}
