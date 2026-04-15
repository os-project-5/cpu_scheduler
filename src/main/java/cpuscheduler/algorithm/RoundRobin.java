package cpuscheduler.algorithm;

import java.util.*;

public class RoundRobin extends Scheduler {

    private Queue<Integer> readyQueue;

    public RoundRobin(int timeQuantum) {
        super(timeQuantum);
        readyQueue = new LinkedList<>();
    }

    @Override
    protected void readyProcess(int currentTime) {
        while (!processQueue.isEmpty() && currentTime >= processQueue.peek()[1])
            readyQueue.add(processQueue.poll()[0]);
    }

    @Override
    protected int getNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.poll();
    }

    @Override
    protected int peekNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.peek();
    }

    @Override
    protected void reReady(int pid) {
        if (processes.get(pid).getRemainingTime() > 0)
            readyQueue.add(pid);
    }

    @Override
    protected void addToReadyQueue(int pid) {
        readyQueue.add(pid);
    }

    @Override
    protected boolean hasReadyProcesses() {
        return !readyQueue.isEmpty();
    }
}
