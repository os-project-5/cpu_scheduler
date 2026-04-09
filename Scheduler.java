import java.util.*;

record Pair<PID, NUM>(PID pid, NUM num) {
}

public abstract class Scheduler {

    PriorityQueue<Pair<Integer, Integer>> processQueue;
    List<Process> processes;
    boolean isPreemptive;

    public Scheduler(boolean isPreemptive) {
        processQueue = new PriorityQueue<>(
                Comparator.comparingInt(p -> p.num()));
        processes = new ArrayList<>();
        this.isPreemptive = isPreemptive;
    }

    public static void main(String[] args) {
        int currentTime = 0;
        Scheduler sch = new SJF(true);
        sch.addProcess(new Process(5, 5, 0));
        sch.addProcess(new Process(3, 5, 1));
        sch.addProcess(new Process(4, 5, 2));
        sch.readyProcess(currentTime);
        while (currentTime < 20) {
            int pid = sch.getNextProcess();
            int top_pid = sch.peekNextProcess();

            if (pid == -1) {
                currentTime++;
                sch.readyProcess(currentTime);
                continue;
            }
            int t = sch.processes.get(pid).remainingTime;
            for (int i = 0; i < t; i++) {
                currentTime++;
                sch.readyProcess(currentTime);
                sch.updateProcesses(pid, currentTime);
                if (sch.isPreemptive && sch.peekNextProcess() != top_pid) {
                    if ((sch instanceof SJF) && sch.processes.get(sch.peekNextProcess()).remainingTime < sch.processes
                            .get(pid).remainingTime) {
                        sch.processQueue.add(new Pair<>(pid, sch.processes.get(pid).arrivalTime));
                        sch.readyProcess(currentTime);
                        break;
                    } else if ((sch instanceof Priority)
                            && sch.processes.get(sch.peekNextProcess()).priority < sch.processes.get(pid).priority) {
                        sch.processQueue.add(new Pair<>(pid, sch.processes.get(pid).arrivalTime));
                        sch.readyProcess(currentTime);
                        break;
                    }

                    else {
                        top_pid = sch.peekNextProcess();
                    }
                }
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

    void updateProcesses(int pid, int currentTime) {
        processes.get(pid).update_remaining(currentTime);

    }

    abstract void readyProcess(int currentTime);

    abstract int getNextProcess();

    abstract int peekNextProcess();

}

class FCFS extends Scheduler {
    public Queue<Integer> readyQueue;

    public FCFS(boolean isPreemptive) {
        super(isPreemptive);
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

}

class Priority extends Scheduler {
    PriorityQueue<Pair<Integer, Integer>> readyQueue;

    public Priority(boolean isPreemptive) {
        super(isPreemptive);
        readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(p -> p.num()));
    }

    @Override
    void readyProcess(int currentTime) {
        while (!processQueue.isEmpty() && currentTime >= processQueue.peek().num()) {
            int pid = processQueue.poll().pid();
            readyQueue.add(new Pair<>(pid, processes.get(pid).priority));
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

}
