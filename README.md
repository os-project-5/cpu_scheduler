# ⚙ CPU Scheduler Simulator

A **JavaFX desktop application** that simulates CPU scheduling algorithms with a live animated Gantt chart, real-time remaining burst time updates, and dynamic process addition.

---

## 📋 Features

- **4 Scheduling Algorithms**
  - **FCFS** — First Come First Served
  - **SJF** — Shortest Job First (Preemptive & Non-Preemptive)
  - **Priority** — Lower number = higher priority (Preemptive & Non-Preemptive)
  - **Round Robin** — Configurable time quantum

- **Live Scheduling Mode**
  - 1 time unit mapped to 1 real second
  - Gantt chart drawn live as the scheduler runs
  - Remaining burst time table updated in real-time
  - Add new processes dynamically while the scheduler is running

- **Instant Run Mode**
  - Run all processes to completion without live scheduling
  - Results displayed immediately

- **Smart Input Handling**
  - Priority field shown only when Priority algorithm is selected
  - Time quantum shown only for Round Robin
  - Preemptive checkbox shown only for SJF and Priority

- **Output**
  - Color-coded animated Gantt chart
  - Average Waiting Time
  - Average Turnaround Time
  - Per-process details: Arrival, Burst, Remaining, Completion, Turnaround, Waiting

---

## 🛠 Prerequisites

- **Java 17** or higher (tested with Java 24)

> **Note:** Maven is **not** required — the project includes a Maven Wrapper (`mvnw.cmd`) that automatically downloads Maven on first run.

---

## 🚀 How to Run

### Option 1: Development Mode

```bash
.\mvnw.cmd javafx:run
```

### Option 2: Build & Run Executable JAR

```bash
# Build the fat JAR (includes all dependencies)
.\mvnw.cmd package

# Run the executable
java -jar target\cpu-scheduler-1.0.jar
```

---

## 📖 Usage Guide

### 1. Select Algorithm
Choose a scheduling algorithm from the dropdown. The UI automatically shows/hides relevant options:
| Algorithm    | Extra Options Shown         |
|--------------|-----------------------------|
| FCFS         | —                           |
| SJF          | Preemptive checkbox         |
| Priority     | Preemptive checkbox, Priority input field |
| Round Robin  | Time Quantum spinner        |

### 2. Add Processes
Enter the **Arrival Time** and **Burst Time** (and **Priority** if applicable), then click **+ Add**. Repeat for each process.

### 3. Run the Simulation

| Button         | Description                                                  |
|----------------|--------------------------------------------------------------|
| **▶ Live Run** | Runs the scheduler in real-time (1 second per time unit). You can add processes dynamically during the simulation. |
| **⚡ Instant Run** | Runs all processes to completion instantly and displays the final results. |
| **⏹ Stop**     | Stops the live simulation.                                   |

### 4. View Results
- **Gantt Chart** — Color-coded timeline showing process execution order
- **Process Details Table** — Updated live with remaining burst time, completion time, turnaround time, and waiting time
- **Averages** — Average Waiting Time and Average Turnaround Time displayed at the bottom

### 5. Dynamic Process Addition
During a **Live Run**, you can add new processes at any time. The scheduler will pick them up based on their arrival time.

---

## 📁 Project Structure

```
cpu_scheduler/
├── pom.xml                              # Maven build configuration
├── mvnw.cmd / mvnw                      # Maven Wrapper (no install needed)
├── src/main/java/cpuscheduler/
│   ├── model/
│   │   └── Process.java                 # Process data model
│   ├── algorithm/
│   │   ├── Scheduler.java               # Abstract scheduler base class
│   │   ├── FCFS.java                    # First Come First Served
│   │   ├── SJF.java                     # Shortest Job First
│   │   ├── Priority.java               # Priority Scheduling
│   │   └── RoundRobin.java             # Round Robin
│   └── gui/
│       ├── Main.java                    # JavaFX application & UI
│       ├── GanttChartPane.java          # Custom Gantt chart component
│       └── Launcher.java               # JAR entry point
├── src/main/resources/cpuscheduler/
│   └── style.css                        # Dark theme stylesheet
└── target/
    └── cpu-scheduler-1.0.jar            # Executable JAR (after build)
```

---

## 🏗 Architecture

```
┌──────────────┐     ┌───────────────────┐     ┌──────────────────┐
│  GUI Layer   │────▶│  Algorithm Layer   │────▶│   Model Layer    │
│  (JavaFX)    │     │  (Schedulers)      │     │   (Process)      │
├──────────────┤     ├───────────────────┤     ├──────────────────┤
│ Main.java    │     │ Scheduler (base)   │     │ Process.java     │
│ GanttChart   │     │ FCFS / SJF         │     │  - arrivalTime   │
│ Launcher     │     │ Priority / RR      │     │  - burstTime     │
│ style.css    │     │                    │     │  - remainingTime  │
└──────────────┘     └───────────────────┘     │  - completionTime│
                                                └──────────────────┘
```

- **Model Layer** — `Process` class holding per-process data and computed metrics (turnaround, waiting time)
- **Algorithm Layer** — Abstract `Scheduler` with a `step()` method for tick-by-tick simulation; each algorithm implements its own ready queue and scheduling logic
- **GUI Layer** — JavaFX application with dark theme, live Gantt chart, and editable process table

---

## 🧪 Example

**Input:**
| Process | Arrival | Burst |
|---------|---------|-------|
| P1      | 0       | 5     |
| P2      | 1       | 3     |
| P3      | 2       | 4     |

**Algorithm:** FCFS

**Gantt Chart Output:**
```
|███ P1 ███|██ P2 ██|███ P3 ███|
0          5        8         12
```

**Results:**
| Process | Arrival | Burst | Completion | Turnaround | Waiting |
|---------|---------|-------|------------|------------|---------|
| P1      | 0       | 5     | 5          | 5          | 0       |
| P2      | 1       | 3     | 8          | 7          | 4       |
| P3      | 2       | 4     | 12         | 10         | 6       |

- **Avg Turnaround Time:** 7.33
- **Avg Waiting Time:** 3.33

---

## 📝 Technologies

- **Java 17+**
- **JavaFX 21** — UI framework
- **Maven** — Build system (via Maven Wrapper)
- **Maven Shade Plugin** — Fat JAR generation
