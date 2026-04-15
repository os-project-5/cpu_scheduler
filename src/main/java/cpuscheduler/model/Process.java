package cpuscheduler.model;

public class Process {

    private String name;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int remainingTime;
    private int completionTime;

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.completionTime = 0;
    }

    public void updateRemaining(int currentTime) {
        remainingTime--;
        if (remainingTime == 0) {
            completionTime = currentTime;
        }
    }

    // Computed metrics
    public int getTurnaroundTime() {
        return completionTime > 0 ? completionTime - arrivalTime : 0;
    }

    public int getWaitingTime() {
        return completionTime > 0 ? getTurnaroundTime() - burstTime : 0;
    }

    // Getters
    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public int getRemainingTime() { return remainingTime; }
    public int getCompletionTime() { return completionTime; }

    @Override
    public String toString() {
        return String.format("%s: Arr=%d, Burst=%d, Rem=%d, Comp=%d",
                name, arrivalTime, burstTime, remainingTime, completionTime);
    }
}
