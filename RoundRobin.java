import java.util.*;

record Pair<PID, NUM>(PID pid, NUM num) {
}

class RoundRobin extends Scheduler {
    public Queue<Integer> readyQueue;

    public RoundRobin(int timeQuantum) {
        super(timeQuantum);
        readyQueue = new LinkedList<>();

    }

    @Override
    void readyProcess(int currentTime) {
        while (!processQueue.isEmpty() && currentTime >= processQueue.peek().num())
            readyQueue.add(processQueue.poll().pid());

    }

    @Override
    int getNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.poll();
    }

    @Override
    int peekNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.peek();
    }

    @Override
    void reReady(int pid) {
        if (processes.get(pid).remainingTime > 0)
            readyQueue.add(pid);
    }

}