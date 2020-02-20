package q5;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Frequency implements Callable<Integer> {
	int elem;
    int freq;
    int i,j;
    int[] arr;
    public Frequency(int i, int j, int[]arr, int x){
        this.i=i;
        this.j=j;
        this.arr=arr;
        this.freq=0;
        this.elem=x;
    }	
	
    public static int parallelFreq(int x, int[] A, int numThreads){
        int count=0;
        int stepSize=1+A.length/numThreads;
        int start=0;
        FutureTask[] subTasks = new FutureTask[numThreads];
        for(int i = 0; i < numThreads; i++){
            Frequency currJob=new Frequency(start,start+stepSize,A,x);
            subTasks[i] = new FutureTask(currJob);
            (new Thread(subTasks[i])).start();
            start+=stepSize;
        }
        for(int i =0; i<numThreads; i++){
            try {
				count+=(Integer) subTasks[i].get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
        }
        return count;
    }
	@Override
	public Integer call() throws Exception {
		for(int idx = i; idx<j && idx<arr.length; idx++){
            if(arr[idx]==this.elem) this.freq++;
        }
        return this.freq;
    }
}
