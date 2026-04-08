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
        processQueue = new LinkedList<Process>();
        
    }

    @Override
    void addProcess(Process p) {
        processes.add(p);


    }

    @Override
    Process getNextProcess() {
        if (processes.isEmpty()) {
            return null;
        }
        return processes.remove(0);
    }

}