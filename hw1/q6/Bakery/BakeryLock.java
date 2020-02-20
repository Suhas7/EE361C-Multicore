package q6.Bakery;

public class BakeryLock implements Lock {
	int N;
    boolean[] choosing;
    int[] number;
    public BakeryLock(int numThreads){
        N = numThreads;
        choosing = new boolean[N];
        number = new int[N];
        for (int j = 0; j < N; j++) {
            choosing[j] = false;
            number[j] = 0;
        }
    }
    public void lock(int pid) {
        choosing[pid] = true;
        for (int j = 0; j < N; j++) {
            if(number[j] > number[pid])
                number[pid] = number[j];
        }
        number[pid]++;
        choosing[pid] = false;

        for (int j = 0; j < N; j++) {
            while(choosing[j]);
            while((number[j] != 0) && ((number[j] < number[pid]) || ((number[j] == number[pid]) && j < pid)));
        }
    }
    public void unlock(int pid) {
        number[pid] = 0;
    }
}
