package com.typinggame.controller;

import com.typinggame.model.GameSession;
import com.typinggame.model.User;
import com.typinggame.service.UserService;
import com.typinggame.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for the History screen.
 * Displays player's game session history and statistics.
 */
public class HistoryController {
    
    private UserService userService;
    private SceneManager sceneManager;
    private Scene historyScene;
    
    // UI Components
    private Label titleLabel;
    private Label statsLabel;
    private TableView<SessionDisplay> sessionTable;
    
    /**
     * Constructor initializes the history controller.
     * 
     * @param userService Service for user management
     * @param sceneManager Manager for scene navigation
     */
    public HistoryController(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
        this.historyScene = createHistoryScene();
    }
    
    /**
     * Creates the history scene with all UI components.
     * 
     * @return Configured history scene
     */
    private Scene createHistoryScene() {
        // Main container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #0f2027 0%, #203a43 50%, #2c5364 100%);");
        
        // Top section - Title and stats
        VBox topSection = createTopSection();
        mainContainer.setTop(topSection);
        
        // Center section - Session table
        VBox centerSection = createCenterSection();
        mainContainer.setCenter(centerSection);
        
        // Bottom section - Action buttons
        HBox bottomSection = createBottomSection();
        mainContainer.setBottom(bottomSection);
        
        Scene scene = new Scene(mainContainer, 900, 600);
        return scene;
    }
    
    /**
     * Creates the top section with title and statistics.
     * 
     * @return Top section VBox
     */
    private VBox createTopSection() {
        VBox topSection = new VBox(15);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(30, 20, 20, 20));
        
        titleLabel = new Label("📊 Your Game History");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        
        statsLabel = new Label("");
        statsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        statsLabel.setTextFill(Color.web("#BDC3C7"));
        statsLabel.setWrapText(true);
        statsLabel.setMaxWidth(700);
        statsLabel.setAlignment(Pos.CENTER);
        
        topSection.getChildren().addAll(titleLabel, statsLabel);
        return topSection;
    }
    
    /**
     * Creates the center section with session table.
     * 
     * @return Center section VBox
     */
    @SuppressWarnings("unchecked")
    private VBox createCenterSection() {
        VBox centerSection = new VBox(15);
        centerSection.setPadding(new Insets(20, 40, 20, 40));
        
        // Table container with styling
        VBox tableContainer = new VBox(10);
        tableContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"
        );
        
        Label tableTitle = new Label("Recent Sessions");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        tableTitle.setTextFill(Color.WHITE);
        
        // Create table
        sessionTable = new TableView<>();
        sessionTable.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-table-cell-border-color: rgba(255, 255, 255, 0.1);"
        );
        sessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Date column
        TableColumn<SessionDisplay, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(150);
        
        // Mode column
        TableColumn<SessionDisplay, String> modeCol = new TableColumn<>("Mode");
        modeCol.setCellValueFactory(new PropertyValueFactory<>("mode"));
        modeCol.setPrefWidth(120);
        
        // WPM column
        TableColumn<SessionDisplay, String> wpmCol = new TableColumn<>("WPM");
        wpmCol.setCellValueFactory(new PropertyValueFactory<>("wpm"));
        wpmCol.setPrefWidth(80);
        
        // Accuracy column
        TableColumn<SessionDisplay, String> accuracyCol = new TableColumn<>("Accuracy");
        accuracyCol.setCellValueFactory(new PropertyValueFactory<>("accuracy"));
        accuracyCol.setPrefWidth(100);
        
        // Errors column
        TableColumn<SessionDisplay, String> errorsCol = new TableColumn<>("Errors");
        errorsCol.setCellValueFactory(new PropertyValueFactory<>("errors"));
        errorsCol.setPrefWidth(80);
        
        // Duration column
        TableColumn<SessionDisplay, String> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setPrefWidth(90);
        
        // Grade column
        TableColumn<SessionDisplay, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradeCol.setPrefWidth(80);
        
        sessionTable.getColumns().addAll(dateCol, modeCol, wpmCol, accuracyCol, 
                                        errorsCol, durationCol, gradeCol);
        
        tableContainer.getChildren().addAll(tableTitle, sessionTable);
        VBox.setVgrow(sessionTable, Priority.ALWAYS);
        
        centerSection.getChildren().add(tableContainer);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        return centerSection;
    }
    
    /**
     * Creates the bottom section with action buttons.
     * 
     * @return Bottom section HBox
     */
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20));
        
        // Clear History button
        Button clearButton = new Button("🗑️ Clear History");
        clearButton.setPrefWidth(180);
        clearButton.setPrefHeight(45);
        clearButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        clearButton.setStyle(
            "-fx-background-color: #E74C3C;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        clearButton.setOnAction(e -> handleClearHistory());
        
        // Export button
        Button exportButton = new Button("📥 Export Data");
        exportButton.setPrefWidth(180);
        exportButton.setPrefHeight(45);
        exportButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        exportButton.setStyle(
            "-fx-background-color: #3498DB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        exportButton.setOnAction(e -> handleExport());
        
        // Back button
        Button backButton = new Button("🏠 Back to Menu");
        backButton.setPrefWidth(180);
        backButton.setPrefHeight(45);
        backButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        backButton.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> handleBack());
        
        bottomSection.getChildren().addAll(clearButton, exportButton, backButton);
        return bottomSection;
    }
    
    /**
     * Updates the history display with current user's data.
     */
    public void updateHistory() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            return;
        }
        
        // Update title
        titleLabel.setText("📊 " + currentUser.getUsername() + "'s Game History");
        
        // Update stats
        String stats = String.format(
            "Total Games: %d  |  Best WPM: %.1f  |  Average WPM: %.1f  |  Average Accuracy: %.1f%%",
            currentUser.getTotalGamesPlayed(),
            currentUser.getBestWPM(),
            currentUser.getAverageWPM(),
            currentUser.getAverageAccuracy()
        );
        statsLabel.setText(stats);
        
        // Populate table
        ObservableList<SessionDisplay> sessions = FXCollections.observableArrayList();
        
        // Show sessions in reverse chronological order (most recent first)
        for (int i = currentUser.getSessionHistory().size() - 1; i >= 0; i--) {
            GameSession session = currentUser.getSessionHistory().get(i);
            sessions.add(new SessionDisplay(
                session.getFormattedTimestamp(),
                session.getMode().getDisplayName(),
                String.format("%.1f", session.getWpm()),
                String.format("%.1f%%", session.getAccuracy()),
                String.valueOf(session.getErrorCount()),
                session.getDuration() + "s",
                session.getPerformanceGrade()
            ));
        }
        
        sessionTable.setItems(sessions);
        
        // Show message if no history
        if (sessions.isEmpty()) {
            sessionTable.setPlaceholder(new Label("No game history yet. Start playing to see your progress!"));
        }
    }
    
    /**
     * Handles clear history button action.
     */
    private void handleClearHistory() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser == null || currentUser.getSessionHistory().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No History");
            alert.setHeaderText("Nothing to Clear");
            alert.setContentText("You don't have any game history to clear.");
            alert.showAndWait();
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear History");
        confirmAlert.setHeaderText("Confirm Clear History");
        confirmAlert.setContentText("Are you sure you want to delete all your game history? This action cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Clear all session history (stats will recalculate as empty)
                currentUser.clearSessionHistory();
                
                // Refresh the UI to show empty state
                updateHistory();
                
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("History Cleared");
                successAlert.setHeaderText("Success");
                successAlert.setContentText("Your game history has been cleared.");
                successAlert.showAndWait();
                
                System.out.println("History cleared for: " + currentUser.getUsername());
            }
        });
    }
    
    /**
     * Handles export data button action.
     */
    private void handleExport() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser == null || currentUser.getSessionHistory().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Data");
            alert.setHeaderText("Nothing to Export");
            alert.setContentText("You don't have any game history to export.");
            alert.showAndWait();
            return;
        }
        
        // Generate export data
        StringBuilder exportData = new StringBuilder();
        exportData.append("=== TYPING GAME HISTORY EXPORT ===\n\n");
        exportData.append("Username: ").append(currentUser.getUsername()).append("\n");
        exportData.append("Export Date: ").append(java.time.LocalDateTime.now()).append("\n\n");
        
        exportData.append("=== SUMMARY STATISTICS ===\n\n");
        exportData.append(String.format("Total Games: %d\n", currentUser.getTotalGamesPlayed()));
        exportData.append(String.format("Best WPM: %.1f\n", currentUser.getBestWPM()));
        exportData.append(String.format("Average WPM: %.1f\n", currentUser.getAverageWPM()));
        exportData.append(String.format("Average Accuracy: %.1f%%\n\n", currentUser.getAverageAccuracy()));
        
        exportData.append("=== SESSION HISTORY ===\n\n");
        exportData.append(String.format("%-20s %-15s %-8s %-10s %-8s %-10s %-6s\n",
            "Date", "Mode", "WPM", "Accuracy", "Errors", "Duration", "Grade"));
        exportData.append("-".repeat(85)).append("\n");
        
        for (GameSession session : currentUser.getSessionHistory()) {
            exportData.append(String.format("%-20s %-15s %-8.1f %-10.1f%% %-8d %-10ds %-6s\n",
                session.getFormattedTimestamp(),
                session.getMode().getDisplayName(),
                session.getWpm(),
                session.getAccuracy(),
                session.getErrorCount(),
                session.getDuration(),
                session.getPerformanceGrade()
            ));
        }
        
        // Show export data in dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Data");
        alert.setHeaderText("Your Game History Data");
        alert.setContentText("Copy the data below to save it:");
        
        TextArea textArea = new TextArea(exportData.toString());
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setFont(Font.font("Courier New", 11));
        textArea.setPrefSize(750, 450);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    /**
     * Handles back button action.
     */
    private void handleBack() {
        sceneManager.navigateTo(SceneManager.MENU_SCENE);
    }
    
    /**
     * Gets the history scene.
     * 
     * @return History scene
     */
    public Scene getScene() {
        return historyScene;
    }
    
    /**
     * Inner class representing a session display for the table.
     */
    public static class SessionDisplay {
        private String date;
        private String mode;
        private String wpm;
        private String accuracy;
        private String errors;
        private String duration;
        private String grade;
        
        public SessionDisplay(String date, String mode, String wpm, String accuracy,
                            String errors, String duration, String grade) {
            this.date = date;
            this.mode = mode;
            this.wpm = wpm;
            this.accuracy = accuracy;
            this.errors = errors;
            this.duration = duration;
            this.grade = grade;
        }
        
        public String getDate() { return date; }
        public String getMode() { return mode; }
        public String getWpm() { return wpm; }
        public String getAccuracy() { return accuracy; }
        public String getErrors() { return errors; }
        public String getDuration() { return duration; }
        public String getGrade() { return grade; }
    }
}
