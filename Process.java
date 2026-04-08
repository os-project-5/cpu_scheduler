import java.util.Random;

public class Process {
    private static int idCounter = 1;

    public String processId;
    public int arrivalTime;
    public int burstTime;
    public int priority;

    public int remainingTime;
    public int completionTime;
    public int turnaroundTime;
    public int waitingTime;

    public String color;

    public Process(int burstTime, int priority, Integer arrivalTime) {
        this.processId = "P" + idCounter++;

        Random rand = new Random();
        this.arrivalTime = (arrivalTime == null) ? rand.nextInt(11) : arrivalTime;

        this.burstTime = burstTime;
        this.priority = priority;

        this.remainingTime = burstTime;
        this.completionTime = 0;
        this.turnaroundTime = 0;
        this.waitingTime = 0;

        // Generate random color for Gantt Chart
        this.color = String.format("#%06x", rand.nextInt(0xFFFFFF + 1));
    }

    @Override
    public String toString() {
        return String.format("[%s] Arr: %d, Burst: %d, Rem: %d, Pri: %d",
                processId, arrivalTime, burstTime, remainingTime, priority);
    }
}