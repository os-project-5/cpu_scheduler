package cpuscheduler.gui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class GanttChartPane extends ScrollPane {

    private static final double UNIT_WIDTH = 52;
    private static final double BAR_HEIGHT = 44;

    private static final String[] COLORS = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4",
            "#FFEAA7", "#DDA0DD", "#98D8C8", "#F7DC6F",
            "#BB8FCE", "#82E0AA", "#F8C471", "#85C1E9"
    };

    private HBox blocksRow;
    private HBox timesRow;
    private VBox chartContent;

    private int lastPid = -2;
    private int blockStart = 0;
    private int lastEndTime = 0;

    public GanttChartPane() {
        blocksRow = new HBox(0);
        blocksRow.setAlignment(Pos.CENTER_LEFT);

        timesRow = new HBox(0);
        timesRow.setAlignment(Pos.CENTER_LEFT);

        chartContent = new VBox(2, blocksRow, timesRow);
        chartContent.setPadding(new Insets(10));
        chartContent.setAlignment(Pos.CENTER_LEFT);

        setContent(chartContent);
        setFitToHeight(true);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setStyle("-fx-background: #12122a; -fx-background-color: #12122a; -fx-border-color: #2a2a4a; -fx-border-radius: 8; -fx-background-radius: 8;");
        setPrefHeight(110);
        setMinHeight(110);
    }

    public void addTick(int pid, int time) {
        if (pid != lastPid) {
            if (lastPid != -2) {
                addBlock(lastPid, blockStart, time);
            }
            blockStart = time;
            lastPid = pid;
        }
        lastEndTime = time + 1;
    }

    public void finalizeCurrent() {
        if (lastPid != -2) {
            addBlock(lastPid, blockStart, lastEndTime);
            lastPid = -2;
        }
    }

    private void addBlock(int pid, int start, int end) {
        double width = (end - start) * UNIT_WIDTH;
        String color = pid >= 0 ? COLORS[pid % COLORS.length] : "#3a3a5a";
        String name = pid >= 0 ? "P" + (pid + 1) : "Idle";

        // Block bar
        StackPane bar = new StackPane();
        bar.setPrefSize(width, BAR_HEIGHT);
        bar.setMinWidth(width);
        bar.setMaxWidth(width);
        bar.setStyle("-fx-background-color: " + color + ";"
                + "-fx-background-radius: 6;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 2);");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");
        bar.getChildren().add(nameLabel);

        // Time label
        Label timeLabel = new Label(String.valueOf(start));
        timeLabel.setPrefWidth(width);
        timeLabel.setMinWidth(width);
        timeLabel.setMaxWidth(width);
        timeLabel.setStyle("-fx-text-fill: #8888aa; -fx-font-size: 11;");
        timeLabel.setPadding(new Insets(2, 0, 0, 4));

        blocksRow.getChildren().add(bar);
        timesRow.getChildren().add(timeLabel);

        // Fade animation
        FadeTransition ft = new FadeTransition(Duration.millis(300), bar);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        // Auto-scroll to right
        layout();
        setHvalue(1.0);
    }

    public void addFinalTimeLabel(int endTime) {
        Label endLabel = new Label(String.valueOf(endTime));
        endLabel.setStyle("-fx-text-fill: #8888aa; -fx-font-size: 11;");
        endLabel.setPadding(new Insets(2, 0, 0, 0));

        // Add a tiny spacer block and time label
        Region spacer = new Region();
        spacer.setPrefSize(1, BAR_HEIGHT);
        blocksRow.getChildren().add(spacer);
        timesRow.getChildren().add(endLabel);
    }

    public void clear() {
        blocksRow.getChildren().clear();
        timesRow.getChildren().clear();
        lastPid = -2;
        blockStart = 0;
        lastEndTime = 0;
    }
}
