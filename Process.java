
public class Process {

    public int arrivalTime;
    public int burstTime;
    public int priority;

    public int remainingTime;
    public int completionTime;

    public Process(int burstTime, int priority, Integer arrivalTime) {

        this.arrivalTime = arrivalTime;

        this.burstTime = burstTime;
        this.priority = priority;

        this.remainingTime = burstTime;
        this.completionTime = 0;

    }

    public void update_remaining(int currentTime) {
        remainingTime--;
        if (remainingTime == 0) {
            completionTime = currentTime;
        }
    }

    @Override
    public String toString() {
        return String.format("Arr: %d, Burst: %d, Comp: %d",
                arrivalTime, burstTime, completionTime);
    }

}