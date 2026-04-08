package cpu_scheduler;

import java.util.*;
import java.util.concurrent.*;

public class MainWithBufferedInput {
    static int currentTime = 0;

    static int globalpid=0;

    static int currpid=-1;
    static int lastpid=-1;
    private static final Queue<Process> buffer = new ConcurrentLinkedQueue<>();
    Scheduler sch ;
    
    public static void main(String[] args) {

        // Scheduler: process every 1 second
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            processBuffer();
        }, 1, 1, TimeUnit.SECONDS);

        // Thread to read input from terminal
        Thread inputThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    int input = scanner.nextInt();
                     // blocks until user types
                    Process prc=new Process (globalpid++,input);// pid, burst time
                     addInput(prc);
                }
            }
        });
        
        inputThread.setDaemon(true);
        inputThread.start();
        
        // Keep main thread alive
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
        }));
    }

    private static void addInput(Process input) {
        buffer.add(input);
        System.out.println("Received: " + input.pid);
    }

    private static void processBuffer() {
        // print current time
        System.out.println("Processing buffer at: " + currentTime);

 
        while (!buffer.isEmpty()) {
            sch.add(buffer.poll(),currentTime);
            
        }

        currpid=sch.getnext();

        System.out.println("Current PID: " + currpid);
        //wait()
        sch.update(currpid);
        // premitive-> rem--,resch; //nonprem-> if(rem==1)ressch

        currentTime += 1; // increment time by 1 seconds
    }
}
