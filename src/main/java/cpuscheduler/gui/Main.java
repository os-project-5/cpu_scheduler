package cpuscheduler.gui;

import cpuscheduler.algorithm.FCFS;
import cpuscheduler.algorithm.RoundRobin;
import cpuscheduler.algorithm.SJF;
import cpuscheduler.algorithm.Scheduler;
import cpuscheduler.model.Process;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    // Algorithm
    private ComboBox<String> algorithmCombo;
    private CheckBox preemptiveCheck;
    private Spinner<Integer> quantumSpinner;
    private VBox quantumBox;

    // Process input
    private TextField arrivalField, burstField, priorityField;
    private VBox priorityBox;
    private TableView<Process> inputTable;
    private ObservableList<Process> inputProcesses;
    private int processCounter = 0;

    // Controls
    private Button liveBtn, instantBtn, pauseBtn, resumeBtn, addDynamicBtn;

    // Output
    private GanttChartPane ganttChart;
    private TableView<Process> resultsTable;
    private ObservableList<Process> resultProcesses;
    private Label avgWaitLabel, avgTurnaroundLabel, timeLabel;

    // Simulation state
    private Scheduler scheduler;
    private Timeline timeline;
    private boolean isRunning = false;
    private boolean isPaused = false;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0f0f1a;");

        root.getChildren().addAll(
                createHeader(),
                createConfigSection(),
                createInputSection(),
                createControlButtons(),
                createGanttSection(),
                createResultsSection(),
                createAveragesSection());

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0f0f1a; -fx-background-color: #0f0f1a;");

        Scene scene = new Scene(scrollPane, 1000, 780);
        scene.getStylesheets().add(getClass().getResource("/cpuscheduler/style.css").toExternalForm());

        stage.setTitle("CPU Scheduler Simulator");
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(650);
        stage.show();
    }

    // ==================== HEADER ====================

    private HBox createHeader() {
        Label title = new Label("⚙  CPU Scheduler Simulator");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: linear-gradient(to right, #00d2ff, #7b2ff7);");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 8, 0));
        return header;
    }

    // ==================== CONFIG ====================

    private HBox createConfigSection() {
        // Algorithm selector
        Label algoLabel = new Label("Algorithm");
        algoLabel.setStyle("-fx-text-fill: #aaaacc; -fx-font-size: 12;");
        algorithmCombo = new ComboBox<>(FXCollections.observableArrayList(
                "FCFS", "SJF", "Priority", "Round Robin"));
        algorithmCombo.setValue("FCFS");
        algorithmCombo.setPrefWidth(160);
        algorithmCombo.setOnAction(e -> onAlgorithmChanged());
        VBox algoBox = new VBox(4, algoLabel, algorithmCombo);

        // Preemptive checkbox
        preemptiveCheck = new CheckBox("Preemptive");
        preemptiveCheck.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13;");
        preemptiveCheck.setVisible(false);
        preemptiveCheck.setManaged(false);

        // Time quantum
        Label qLabel = new Label("Time Quantum");
        qLabel.setStyle("-fx-text-fill: #aaaacc; -fx-font-size: 12;");
        quantumSpinner = new Spinner<>(1, 100, 2);
        quantumSpinner.setPrefWidth(90);
        quantumSpinner.setEditable(true);
        quantumBox = new VBox(4, qLabel, quantumSpinner);
        quantumBox.setVisible(false);
        quantumBox.setManaged(false);

        HBox config = new HBox(20, algoBox, preemptiveCheck, quantumBox);
        config.setAlignment(Pos.CENTER_LEFT);
        config.setPadding(new Insets(14, 18, 14, 18));
        config.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;"
                + "-fx-border-color: #2a2a5a; -fx-border-radius: 10;");
        return config;
    }

    private void onAlgorithmChanged() {
        String algo = algorithmCombo.getValue();
        boolean showPreemptive = algo.equals("SJF") || algo.equals("Priority");
        boolean showQuantum = algo.equals("Round Robin");
        boolean showPriority = algo.equals("Priority");

        preemptiveCheck.setVisible(showPreemptive);
        preemptiveCheck.setManaged(showPreemptive);
        if (!showPreemptive)
            preemptiveCheck.setSelected(false);

        quantumBox.setVisible(showQuantum);
        quantumBox.setManaged(showQuantum);

        priorityBox.setVisible(showPriority);
        priorityBox.setManaged(showPriority);

        // Update input table priority column
        inputTable.getColumns().get(3).setVisible(showPriority);
    }

    // ==================== INPUT ====================

    @SuppressWarnings("unchecked")
    private VBox createInputSection() {
        Label sectionLabel = new Label("Process Input");
        sectionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        sectionLabel.setStyle("-fx-text-fill: #e0e0e0;");

        // Input fields
        Label arrLabel = new Label("Arrival Time");
        arrLabel.setStyle("-fx-text-fill: #aaaacc; -fx-font-size: 12;");
        arrivalField = new TextField("0");
        arrivalField.setPrefWidth(90);
        arrivalField.setPromptText("0");
        VBox arrBox = new VBox(4, arrLabel, arrivalField);

        Label burstLabel = new Label("Burst Time");
        burstLabel.setStyle("-fx-text-fill: #aaaacc; -fx-font-size: 12;");
        burstField = new TextField();
        burstField.setPrefWidth(90);
        burstField.setPromptText("e.g. 5");
        VBox burstBox = new VBox(4, burstLabel, burstField);

        Label prioLabel = new Label("Priority");
        prioLabel.setStyle("-fx-text-fill: #aaaacc; -fx-font-size: 12;");
        priorityField = new TextField("0");
        priorityField.setPrefWidth(90);
        priorityField.setPromptText("0");
        priorityBox = new VBox(4, prioLabel, priorityField);
        priorityBox.setVisible(false);
        priorityBox.setManaged(false);

        Button addBtn = new Button("+ Add");
        addBtn.getStyleClass().add("btn-accent");
        addBtn.setOnAction(e -> addProcess());

        HBox inputRow = new HBox(12, arrBox, burstBox, priorityBox, addBtn);
        inputRow.setAlignment(Pos.BOTTOM_LEFT);

        // Input table
        inputProcesses = FXCollections.observableArrayList();
        inputTable = new TableView<>(inputProcesses);
        inputTable.setPrefHeight(150);
        inputTable.setPlaceholder(new Label("No processes added yet"));

        TableColumn<Process, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(80);

        TableColumn<Process, Integer> arrCol = new TableColumn<>("Arrival");
        arrCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        arrCol.setPrefWidth(80);

        TableColumn<Process, Integer> burstCol = new TableColumn<>("Burst");
        burstCol.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        burstCol.setPrefWidth(80);

        TableColumn<Process, Integer> prioCol = new TableColumn<>("Priority");
        prioCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        prioCol.setPrefWidth(80);
        prioCol.setVisible(false);

        inputTable.getColumns().addAll(nameCol, arrCol, burstCol, prioCol);
        inputTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Remove button
        Button removeBtn = new Button("− Remove Selected");
        removeBtn.getStyleClass().add("btn-danger");
        removeBtn.setOnAction(e -> {
            Process sel = inputTable.getSelectionModel().getSelectedItem();
            if (sel != null)
                inputProcesses.remove(sel);
        });

        Button clearBtn = new Button("Clear All");
        clearBtn.getStyleClass().add("btn-secondary");
        clearBtn.setOnAction(e -> {
            inputProcesses.clear();
            processCounter = 0;
        });

        HBox btnRow = new HBox(10, removeBtn, clearBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        VBox section = new VBox(10, sectionLabel, inputRow, inputTable, btnRow);
        section.setPadding(new Insets(14, 18, 14, 18));
        section.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;"
                + "-fx-border-color: #2a2a5a; -fx-border-radius: 10;");
        return section;
    }

    private void addProcess() {
        try {
            int burst = Integer.parseInt(burstField.getText().trim());
            if (burst <= 0) {
                showError("Burst time must be > 0");
                return;
            }

            // Auto-fill arrival time: if running and field is empty/default, use current sim time
            int arrival;
            String arrText = arrivalField.getText().trim();
            if ((isRunning || isPaused) && scheduler != null && arrText.isEmpty()) {
                arrival = scheduler.getStepTime();
            } else {
                arrival = Integer.parseInt(arrText.isEmpty() ? "0" : arrText);
            }
            if (arrival < 0) {
                showError("Arrival time must be >= 0");
                return;
            }

            // During simulation, arrival time must be >= current time
            if ((isRunning || isPaused) && scheduler != null && arrival < scheduler.getStepTime()) {
                showError("Arrival time must be >= current time (" + scheduler.getStepTime() + ")");
                return;
            }

            int priority = 0;
            if (priorityBox.isVisible()) {
                priority = Integer.parseInt(priorityField.getText().trim());
            }

            processCounter++;
            Process p = new Process("P" + processCounter, arrival, burst, priority);

            if ((isRunning || isPaused) && scheduler != null) {
                scheduler.addProcessDynamic(p);
                resultProcesses.add(p);
                resultsTable.refresh();
                // Also show in input table
                inputProcesses.add(p);
            } else {
                inputProcesses.add(p);
            }

            // Clear fields — set arrival prompt to current time if running
            if ((isRunning || isPaused) && scheduler != null) {
                arrivalField.clear();
                arrivalField.setPromptText("now (" + scheduler.getStepTime() + ")");
            } else {
                arrivalField.setText("0");
            }
            burstField.clear();
            priorityField.setText("0");
            burstField.requestFocus();
        } catch (NumberFormatException ex) {
            showError("Please enter valid integer values.");
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // ==================== CONTROLS ====================

    private HBox createControlButtons() {
        liveBtn = new Button("▶  Live Run");
        liveBtn.getStyleClass().add("btn-live");
        liveBtn.setOnAction(e -> startLiveRun());

        instantBtn = new Button("⚡  Instant Run");
        instantBtn.getStyleClass().add("btn-instant");
        instantBtn.setOnAction(e -> runInstant());

        pauseBtn = new Button("⏸  Pause");
        pauseBtn.getStyleClass().add("btn-warning");
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> pauseSimulation());

        resumeBtn = new Button("▶  Resume");
        resumeBtn.getStyleClass().add("btn-live");
        resumeBtn.setDisable(true);
        resumeBtn.setVisible(false);
        resumeBtn.setManaged(false);
        resumeBtn.setOnAction(e -> resumeSimulation());

        addDynamicBtn = new Button("+ Add Process");
        addDynamicBtn.getStyleClass().add("btn-accent");
        addDynamicBtn.setVisible(false);
        addDynamicBtn.setManaged(false);

        HBox controls = new HBox(12, liveBtn, instantBtn, pauseBtn, resumeBtn);
        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    // ==================== GANTT CHART ====================

    private VBox createGanttSection() {
        Label sectionLabel = new Label("Gantt Chart");
        sectionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        sectionLabel.setStyle("-fx-text-fill: #e0e0e0;");

        timeLabel = new Label("Time: 0");
        timeLabel.setStyle("-fx-text-fill: #00d2ff; -fx-font-size: 14; -fx-font-weight: bold;");

        HBox headerRow = new HBox(sectionLabel, new Region(), timeLabel);
        HBox.setHgrow(headerRow.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        ganttChart = new GanttChartPane();

        VBox section = new VBox(8, headerRow, ganttChart);
        section.setPadding(new Insets(14, 18, 14, 18));
        section.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;"
                + "-fx-border-color: #2a2a5a; -fx-border-radius: 10;");
        return section;
    }

    // ==================== RESULTS TABLE ====================

    @SuppressWarnings("unchecked")
    private VBox createResultsSection() {
        Label sectionLabel = new Label("Process Details");
        sectionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        sectionLabel.setStyle("-fx-text-fill: #e0e0e0;");

        resultProcesses = FXCollections.observableArrayList();
        resultsTable = new TableView<>(resultProcesses);
        resultsTable.setPrefHeight(180);
        resultsTable.setPlaceholder(new Label("Run a simulation to see results"));

        TableColumn<Process, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(70);

        TableColumn<Process, Integer> arrCol = new TableColumn<>("Arrival");
        arrCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        arrCol.setPrefWidth(70);

        TableColumn<Process, Integer> burstCol = new TableColumn<>("Burst");
        burstCol.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        burstCol.setPrefWidth(70);

        TableColumn<Process, Integer> remCol = new TableColumn<>("Remaining");
        remCol.setCellValueFactory(new PropertyValueFactory<>("remainingTime"));
        remCol.setPrefWidth(95);
        remCol.setStyle("-fx-font-weight: bold;");

        TableColumn<Process, Integer> compCol = new TableColumn<>("Completion");
        compCol.setCellValueFactory(new PropertyValueFactory<>("completionTime"));
        compCol.setPrefWidth(95);

        TableColumn<Process, Integer> tatCol = new TableColumn<>("Turnaround");
        tatCol.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        tatCol.setPrefWidth(100);

        TableColumn<Process, Integer> waitCol = new TableColumn<>("Waiting");
        waitCol.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        waitCol.setPrefWidth(85);

        resultsTable.getColumns().addAll(nameCol, arrCol, burstCol, remCol, compCol, tatCol, waitCol);
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        VBox section = new VBox(10, sectionLabel, resultsTable);
        section.setPadding(new Insets(14, 18, 14, 18));
        section.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;"
                + "-fx-border-color: #2a2a5a; -fx-border-radius: 10;");
        return section;
    }

    // ==================== AVERAGES ====================

    private HBox createAveragesSection() {
        avgWaitLabel = new Label("Avg Waiting Time:  —");
        avgWaitLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        avgWaitLabel.setStyle("-fx-text-fill: #4ECDC4;");

        avgTurnaroundLabel = new Label("Avg Turnaround Time:  —");
        avgTurnaroundLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        avgTurnaroundLabel.setStyle("-fx-text-fill: #FF6B6B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox section = new HBox(30, avgWaitLabel, spacer, avgTurnaroundLabel);
        section.setAlignment(Pos.CENTER_LEFT);
        section.setPadding(new Insets(14, 18, 14, 18));
        section.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;"
                + "-fx-border-color: #2a2a5a; -fx-border-radius: 10;");
        return section;
    }

    // ==================== SIMULATION ====================

    private Scheduler createScheduler() {
        String algo = algorithmCombo.getValue();
        boolean preemptive = preemptiveCheck.isSelected();
        return switch (algo) {
            case "FCFS" -> new FCFS();
            case "SJF" -> new SJF(preemptive);
            case "Priority" -> new cpuscheduler.algorithm.Priority(preemptive);
            case "Round Robin" -> new RoundRobin(quantumSpinner.getValue());
            default -> new FCFS();
        };
    }

    private void startLiveRun() {
        if (inputProcesses.isEmpty()) {
            showError("Please add at least one process.");
            return;
        }

        scheduler = createScheduler();
        ganttChart.clear();
        resultProcesses.clear();

        for (Process p : inputProcesses) {
            Process copy = new Process(p.getName(), p.getArrivalTime(), p.getBurstTime(), p.getPriority());
            scheduler.addProcess(copy);
            resultProcesses.add(copy);
        }

        setRunningState(true);
        arrivalField.clear();
        arrivalField.setPromptText("now (0)");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tickLive()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void tickLive() {
        int pid = scheduler.step();
        int time = scheduler.getStepTime();

        ganttChart.addTick(pid, time - 1);
        timeLabel.setText("Time: " + time);
        resultsTable.refresh();

        // Update arrival field prompt with current time
        arrivalField.setPromptText("now (" + time + ")");

        if (scheduler.isAllDone()) {
            ganttChart.finalizeCurrent();
            ganttChart.addFinalTimeLabel(time);
            finishSimulation();
            updateAverages();
        }
    }

    private void runInstant() {
        if (inputProcesses.isEmpty()) {
            showError("Please add at least one process.");
            return;
        }

        scheduler = createScheduler();
        ganttChart.clear();
        resultProcesses.clear();

        for (Process p : inputProcesses) {
            Process copy = new Process(p.getName(), p.getArrivalTime(), p.getBurstTime(), p.getPriority());
            scheduler.addProcess(copy);
            resultProcesses.add(copy);
        }

        // Run to completion
        int maxTime = 10000;
        while (!scheduler.isAllDone() && scheduler.getStepTime() < maxTime) {
            int pid = scheduler.step();
            int time = scheduler.getStepTime();
            ganttChart.addTick(pid, time - 1);
        }

        ganttChart.finalizeCurrent();
        ganttChart.addFinalTimeLabel(scheduler.getStepTime());
        timeLabel.setText("Time: " + scheduler.getStepTime());
        resultsTable.refresh();
        updateAverages();
    }

    private void pauseSimulation() {
        if (timeline != null) {
            timeline.pause();
        }
        isPaused = true;
        pauseBtn.setVisible(false);
        pauseBtn.setManaged(false);
        resumeBtn.setVisible(true);
        resumeBtn.setManaged(true);
        resumeBtn.setDisable(false);
    }

    private void resumeSimulation() {
        if (timeline != null) {
            timeline.play();
        }
        isPaused = false;
        resumeBtn.setVisible(false);
        resumeBtn.setManaged(false);
        pauseBtn.setVisible(true);
        pauseBtn.setManaged(true);
        pauseBtn.setDisable(false);
    }

    private void finishSimulation() {
        if (timeline != null) {
            timeline.stop();
        }
        isPaused = false;
        setRunningState(false);
        arrivalField.setText("0");
        arrivalField.setPromptText("0");
    }

    private void setRunningState(boolean running) {
        isRunning = running;
        liveBtn.setDisable(running);
        instantBtn.setDisable(running);
        pauseBtn.setDisable(!running);
        pauseBtn.setVisible(true);
        pauseBtn.setManaged(true);
        resumeBtn.setVisible(false);
        resumeBtn.setManaged(false);
        algorithmCombo.setDisable(running);
        preemptiveCheck.setDisable(running);
        quantumSpinner.setDisable(running);
    }

    private void updateAverages() {
        if (resultProcesses.isEmpty())
            return;

        double totalWait = 0, totalTat = 0;
        int completed = 0;
        for (Process p : resultProcesses) {
            if (p.getCompletionTime() > 0) {
                totalWait += p.getWaitingTime();
                totalTat += p.getTurnaroundTime();
                completed++;
            }
        }
        if (completed > 0) {
            avgWaitLabel.setText(String.format("Avg Waiting Time:  %.2f", totalWait / completed));
            avgTurnaroundLabel.setText(String.format("Avg Turnaround Time:  %.2f", totalTat / completed));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
