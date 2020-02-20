package q3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monkey {
    int currDir;
    int monkeyCount;
    boolean kongOn=false;
    ReentrantLock rope = new ReentrantLock();
    Condition monkWait = rope.newCondition();
    Condition kongWait = rope.newCondition();
    public Monkey() {}
    public void ClimbRope(int direction) throws InterruptedException {
        rope.lock();
        if(direction==-1){
            kongOn=true;
            while(monkeyCount>0){kongWait.await();}
            currDir = -1;
            kongOn=false;
        }
        else {
            while(((monkeyCount!=0)&&(currDir !=direction)) || (monkeyCount==3) || (kongOn)) monkWait.await();
            currDir = direction;
        }
        monkeyCount++;
        rope.unlock();
    }
    public void LeaveRope() {
        rope.lock();
        if(currDir ==-1){
            currDir = 0;
            monkWait.signalAll();
        }
        if(kongOn) kongWait.signalAll();
        else monkWait.signalAll();
        monkeyCount--;
        rope.unlock();
    }

    /**
     * Returns the number of monkeys on the rope currently for test purpose.
     *
     * @return the number of monkeys on the rope
     *
     * Positive Test Cases:
     * case 1: when normal monkey (0 and 1) is on the rope, this value should <= 3, >= 0
     * case 2: when Kong is on the rope, this value should be 1
     */
    public int getNumMonkeysOnRope() {
        return monkeyCount;
    }

}
