package cpuscheduler.algorithm;

import cpuscheduler.model.Process;
import java.util.*;

public abstract class Scheduler {

    protected PriorityQueue<int[]> processQueue; // {pid, arrivalTime}
    protected List<Process> processes;
    protected boolean isPreemptive;
    protected int timeQuantum;

    // Step-by-step execution state
    private int currentPid = -1;
    private int ticksInBurst = 0;
    private int stepTime = 0;

    public Scheduler() {
        processQueue = new PriorityQueue<>((a, b) -> {
            if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
            return Integer.compare(a[0], b[0]);
        });
        processes = new ArrayList<>();
        isPreemptive = false;
        timeQuantum = 0;
    }

    public Scheduler(boolean isPreemptive) {
        this();
        this.isPreemptive = isPreemptive;
    }

    public Scheduler(int timeQuantum) {
        this();
        this.timeQuantum = timeQuantum;
    }

    public void addProcess(Process p) {
        processes.add(p);
        processQueue.add(new int[]{processes.size() - 1, p.getArrivalTime()});
    }

    public void addProcessDynamic(Process p) {
        processes.add(p);
        int pid = processes.size() - 1;
        if (p.getArrivalTime() <= stepTime) {
            addToReadyQueue(pid);
        } else {
            processQueue.add(new int[]{pid, p.getArrivalTime()});
        }
    }

    /**
     * Execute one time unit. Returns the PID that ran, or -1 if idle.
     */
    public int step() {
        readyProcess(stepTime);

        if (currentPid == -1) {
            currentPid = getNextProcess();
            ticksInBurst = 0;
        }

        if (currentPid == -1) {
            stepTime++;
            return -1;
        }

        int ranPid = currentPid;
        stepTime++;
        readyProcess(stepTime);
        processes.get(currentPid).updateRemaining(stepTime);
        ticksInBurst++;

        // Process completed
        if (processes.get(currentPid).getRemainingTime() == 0) {
            currentPid = -1;
            return ranPid;
        }

        // Round Robin quantum expired
        if (timeQuantum > 0 && ticksInBurst >= timeQuantum) {
            reReady(currentPid);
            currentPid = -1;
            return ranPid;
        }

        // Preemption check
        if (isPreemptive) {
            int topPid = peekNextProcess();
            if (topPid != -1) {
                boolean shouldPreempt = false;
                if (this instanceof SJF) {
                    shouldPreempt = processes.get(topPid).getRemainingTime()
                            < processes.get(currentPid).getRemainingTime();
                } else if (this instanceof Priority) {
                    shouldPreempt = processes.get(topPid).getPriority()
                            < processes.get(currentPid).getPriority();
                }
                if (shouldPreempt) {
                    reReady(currentPid);
                    currentPid = -1;
                }
            }
        }

        return ranPid;
    }

    public boolean isAllDone() {
        if (!processQueue.isEmpty()) return false;
        if (currentPid != -1) return false;
        if (hasReadyProcesses()) return false;
        for (Process p : processes) {
            if (p.getRemainingTime() > 0) return false;
        }
        return !processes.isEmpty();
    }

    public int getStepTime() { return stepTime; }
    public List<Process> getProcesses() { return processes; }

    protected void updateProcesses(int pid, int currentTime) {
        processes.get(pid).updateRemaining(currentTime);
    }

    protected abstract void readyProcess(int currentTime);
    protected abstract int getNextProcess();
    protected abstract int peekNextProcess();
    protected abstract void reReady(int pid);
    protected abstract void addToReadyQueue(int pid);
    protected abstract boolean hasReadyProcesses();
}
