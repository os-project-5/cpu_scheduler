import java.util.ArrayList;
import java.util.Queue;
import java.util.*;

record Pair<PID, RemainingTime>(PID pid, RemainingTime remainingTime) {
}

abstract class Scheduler {

    ArrayList<Process> processes;

    public Scheduler() {
        processes = new ArrayList<Process>();
    }

    abstract void addProcess(Process p);

    abstract Process getNextProcess();

}

class FCFS extends Scheduler {
    Queue<Integer> processQueue;

    public FCFS() {
        super();

    }

    @Override
    void addProcess(Process p) {
        processes.add(p);
        processQueue.add(processes.size() - 1);

    }

    @Override
    Process getNextProcess() {
        return processes.get(processQueue.poll());
    }

}

class SJF extends Scheduler {
    PriorityQueue<Pair<Integer, Integer>> processQueue = new PriorityQueue<>(
            Comparator.comparingInt(p -> p.remainingTime()));
    boolean isPreemptive;

    public SJF(boolean isPreemptive) {
        super();
        this.isPreemptive = isPreemptive;
    }

    @Override
    void addProcess(Process p) {
        processes.add(p);
        processQueue.add(new Pair<>(processes.size() - 1, p.remainingTime));

    }

    @Override
    Process getNextProcess() {
        return processes.get(processQueue.poll().pid());
    }

}