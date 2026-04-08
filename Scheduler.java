import java.util.ArrayList;
import java.util.Queue;

abstract class Scheduler {

    ArrayList<Process> processes;

    public Scheduler() {
        processes = new ArrayList<Process>();
    }

    abstract void addProcess(Process p);

    abstract Process getNextProcess();

}

class FCFS extends Scheduler {
    Queue<Process> processQueue;

    public FCFS() {
        super();

    }

    @Override
    void addProcess(Process p) {
        processes.add(p);
        processQueue.add(p);

    }

    @Override
    Process getNextProcess() {
        return processQueue.poll();
    }

}