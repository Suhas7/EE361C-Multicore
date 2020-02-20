package q2.b;

import java.util.ArrayList;

public class PIncrement implements Runnable{
	
	static LamportMutex mux;
	static int itg;
	int internalCounter;
	int pid;
	
	public PIncrement(int internalC, int i){
        this.internalCounter = internalC;
        this.pid = i;
    }
	
    public static int parallelIncrement(int c, int numThreads){
    	mux = new LamportMutex(numThreads);
    	itg = c;
    	int max = 120000;
    	ArrayList<Thread> threads = new ArrayList<Thread>();
    	for(int i = 0; i < numThreads; i++){
    		Thread currJob;
            if(i!=0) currJob=new Thread(new PIncrement(max/numThreads, i));
            else currJob=new Thread(new PIncrement((max/numThreads)+(max%numThreads), i));
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
            mux.lock(pid);
            itg++;
            mux.unlock(pid);
        }
		
	}
    
    public static class LamportMutex{
        int X,Y;
        boolean[] flag;
        LamportMutex(int n){
            flag=new boolean[n];
            X = -1;
            Y = -1;
        }
        public void lock(int i){
            while(true){
                flag[i]=true;
                X=i;
                if(Y!=-1){
                    flag[i]=false;
                    while(Y!=-1);
                }else{
                    Y=i;
                    if(X==i) {return;}
                    else{
                        flag[i]=false;
                        for(int j=0; j<flag.length;j++){while(flag[j]==true);}
                        if(Y==i) return;
                        else{while(Y!=-1);continue;}
                    }
                }
            }
        }
        public void unlock(int i)
        {
            Y = -1;
            flag[i] = false;
        }
    }
    
}
