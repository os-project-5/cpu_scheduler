import java.util.*;

record Pair<PID, ArrivalTime>(PID pid, ArrivalTime arrivalTime) {
}

public abstract class Scheduler {

    PriorityQueue<Pair<Integer, Integer>> processQueue;
    List<Process> processes;

    public Scheduler() {
        processQueue = new PriorityQueue<>(
                Comparator.comparingInt(p -> p.arrivalTime()));
        processes = new ArrayList<>();
    }

    public static void main(String[] args) {
        int currentTime = 0;
        Scheduler sch = new FCFS();
        sch.addProcess(new Process(5, 1, 0));
        sch.addProcess(new Process(3, 1, 1));
        sch.addProcess(new Process(8, 1, 2));
        sch.readyProcess(currentTime);
        while (currentTime < 20) {
            int pid = sch.getNextProcess();
            if (pid == -1) {
                currentTime++;
                sch.readyProcess(currentTime);
                continue;
            }
            int t = sch.processes.get(pid).remainingTime;
            for (int i = 0; i < t; i++) {
                currentTime++;
                sch.updateProcesses(pid, currentTime);
                sch.readyProcess(currentTime);
            }

        }
        System.out.println();
        for (int i = 0; i < sch.processes.size(); i++) {
            System.out.println(sch.processes.get(i).toString());
        }

    }

    void addProcess(Process p) {
        processes.add(p);
        processQueue.add(new Pair<>(processes.size() - 1, p.arrivalTime));

    }

    abstract void readyProcess(int currentTime);

    abstract int getNextProcess();

    abstract void updateProcesses(int pid, int currentTime);

}

class FCFS extends Scheduler {
    public Queue<Integer> readyQueue;

    public FCFS() {
        super();
        readyQueue = new LinkedList<>();

    }

    @Override
    void readyProcess(int currentTime) {
        while (!processQueue.isEmpty() && currentTime == processQueue.peek().arrivalTime())
            readyQueue.add(processQueue.poll().pid());

    }

    @Override
    int getNextProcess() {
        return readyQueue.isEmpty() ? -1 : readyQueue.poll();
    }

    @Override
    void updateProcesses(int pid, int currentTime) {
        processes.get(pid).update_remaining(currentTime);

    }

}
/*
 * class SJF extends Scheduler {
 * PriorityQueue<Pair<Integer, Integer>> processQueue = new PriorityQueue<>(
 * Comparator.comparingInt(p -> p.remainingTime()));
 * boolean isPreemptive;
 * 
 * public SJF(boolean isPreemptive) {
 * super();
 * this.isPreemptive = isPreemptive;
 * }
 * 
 * @Override
 * void addProcess(Process p) {
 * processes.add(p);
 * processQueue.add(new Pair<>(processes.size() - 1, p.remainingTime));
 * 
 * }
 * 
 * @Override
 * Process getNextProcess() {
 * return processes.get(processQueue.poll().pid());
 * }
 * 
 * }
 */