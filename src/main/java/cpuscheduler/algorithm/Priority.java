package cpuscheduler.algorithm;

import java.util.*;

public class Priority extends Scheduler {

    private PriorityQueue<int[]> readyQueue; // {pid, priority}

    public Priority(boolean isPreemptive) {
        super(isPreemptive);
        readyQueue = new PriorityQueue<>((a, b) -> {
            if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
            return Integer.compare(a[0], b[0]);
        });
    }

    @Override
    protected void readyProcess(int currentTime) {
        while (!processQueue.isEmpty() && currentTime >= processQueue.peek()[1]) {
            int pid = processQueue.poll()[0];
            readyQueue.add(new int[]{pid, processes.get(pid).getPriority()});
        }
    }

    @Override
    protected int getNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.poll()[0];
    }

    @Override
    protected int peekNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.peek()[0];
    }

    @Override
    protected void reReady(int pid) {
        if (processes.get(pid).getRemainingTime() > 0)
            readyQueue.add(new int[]{pid, processes.get(pid).getPriority()});
    }

    @Override
    protected void addToReadyQueue(int pid) {
        readyQueue.add(new int[]{pid, processes.get(pid).getPriority()});
    }

    @Override
    protected boolean hasReadyProcesses() {
        return !readyQueue.isEmpty();
    }
}
