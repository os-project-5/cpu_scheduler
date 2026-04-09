import java.util.*;

record Pair<PID, NUM>(PID pid, NUM num) {
}

class SJF extends Scheduler {
    PriorityQueue<Pair<Integer, Integer>> readyQueue;

    public SJF(boolean isPreemptive) {
        super(isPreemptive);
        readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(p -> p.num()));
    }

    @Override
    void readyProcess(int currentTime) {
        while (!processQueue.isEmpty() && currentTime >= processQueue.peek().num()) {
            int pid = processQueue.poll().pid();
            readyQueue.add(new Pair<>(pid, processes.get(pid).remainingTime));
        }

    }

    @Override
    int getNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.poll().pid();
    }

    @Override
    int peekNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.peek().pid();
    }

    void reReady(int pid) {
        if (processes.get(pid).remainingTime > 0)
            readyQueue.add(new Pair<>(pid, processes.get(pid).remainingTime));
    }

}
