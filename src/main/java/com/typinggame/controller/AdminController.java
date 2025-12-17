package com.typinggame.controller;

import com.typinggame.model.User;
import com.typinggame.model.GameSession;
import com.typinggame.service.UserService;
import com.typinggame.service.ScoreManager;
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
 * Controller for the Admin Panel screen.
 * Provides administrative functions for viewing player reports and managing content.
 */
public class AdminController {
    
    private UserService userService;
    private ScoreManager scoreManager;
    private SceneManager sceneManager;
    private Scene adminScene;
    
    // UI Components
    private TableView<PlayerReport> playerTable;
    private Label statsLabel;
    private TextArea detailsArea;
    
    /**
     * Constructor initializes the admin controller.
     * 
     * @param userService Service for user management
     * @param scoreManager Service for score management
     * @param sceneManager Manager for scene navigation
     */
    public AdminController(UserService userService, ScoreManager scoreManager,
                          SceneManager sceneManager) {
        this.userService = userService;
        this.scoreManager = scoreManager;
        this.sceneManager = sceneManager;
        this.adminScene = createAdminScene();
    }
    
    /**
     * Creates the admin scene with all UI components.
     * 
     * @return Configured admin scene
     */
    private Scene createAdminScene() {
        // Main container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: #2C3E50;");
        
        // Top section - Title and stats
        VBox topSection = createTopSection();
        mainContainer.setTop(topSection);
        
        // Center section - Player table
        VBox centerSection = createCenterSection();
        mainContainer.setCenter(centerSection);
        
        // Right section - Details panel
        VBox rightSection = createRightSection();
        mainContainer.setRight(rightSection);
        
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
        VBox topSection = new VBox(10);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20));
        topSection.setStyle("-fx-background-color: #34495E;");
        
        Label titleLabel = new Label("⚙️ Admin Panel");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);
        
        statsLabel = new Label("");
        statsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statsLabel.setTextFill(Color.web("#BDC3C7"));
        
        topSection.getChildren().addAll(titleLabel, statsLabel);
        return topSection;
    }
    
    /**
     * Creates the center section with player table.
     * 
     * @return Center section VBox
     */
    @SuppressWarnings("unchecked")
    private VBox createCenterSection() {
        VBox centerSection = new VBox(10);
        centerSection.setPadding(new Insets(20));
        
        Label tableTitle = new Label("Player Performance Reports");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tableTitle.setTextFill(Color.WHITE);
        
        // Create table
        playerTable = new TableView<>();
        playerTable.setStyle("-fx-background-color: #34495E;");
        
        // Username column
        TableColumn<PlayerReport, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(120);
        
        // Total Games column
        TableColumn<PlayerReport, Integer> gamesCol = new TableColumn<>("Total Games");
        gamesCol.setCellValueFactory(new PropertyValueFactory<>("totalGames"));
        gamesCol.setPrefWidth(100);
        
        // Best WPM column
        TableColumn<PlayerReport, String> bestWpmCol = new TableColumn<>("Best WPM");
        bestWpmCol.setCellValueFactory(new PropertyValueFactory<>("bestWpm"));
        bestWpmCol.setPrefWidth(100);
        
        // Avg WPM column
        TableColumn<PlayerReport, String> avgWpmCol = new TableColumn<>("Avg WPM");
        avgWpmCol.setCellValueFactory(new PropertyValueFactory<>("avgWpm"));
        avgWpmCol.setPrefWidth(100);
        
        // Avg Accuracy column
        TableColumn<PlayerReport, String> avgAccCol = new TableColumn<>("Avg Accuracy");
        avgAccCol.setCellValueFactory(new PropertyValueFactory<>("avgAccuracy"));
        avgAccCol.setPrefWidth(120);
        
        playerTable.getColumns().addAll(usernameCol, gamesCol, bestWpmCol, avgWpmCol, avgAccCol);
        
        // Selection listener
        playerTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    showPlayerDetails(newSelection.getUsername());
                }
            }
        );
        
        centerSection.getChildren().addAll(tableTitle, playerTable);
        VBox.setVgrow(playerTable, Priority.ALWAYS);
        
        return centerSection;
    }
    
    /**
     * Creates the right section with player details.
     * 
     * @return Right section VBox
     */
    private VBox createRightSection() {
        VBox rightSection = new VBox(10);
        rightSection.setPadding(new Insets(20));
        rightSection.setPrefWidth(250);
        rightSection.setStyle("-fx-background-color: #34495E;");
        
        Label detailsTitle = new Label("Player Details");
        detailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        detailsTitle.setTextFill(Color.WHITE);
        
        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setFont(Font.font("Courier New", 11));
        detailsArea.setStyle("-fx-control-inner-background: #2C3E50; -fx-text-fill: white;");
        detailsArea.setText("Select a player to view details");
        
        rightSection.getChildren().addAll(detailsTitle, detailsArea);
        VBox.setVgrow(detailsArea, Priority.ALWAYS);
        
        return rightSection;
    }
    
    /**
     * Creates the bottom section with action buttons.
     * 
     * @return Bottom section HBox
     */
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(15);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20));
        
        // Refresh button
        Button refreshButton = new Button("🔄 Refresh Data");
        refreshButton.setPrefWidth(150);
        refreshButton.setPrefHeight(40);
        refreshButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        refreshButton.setStyle(
            "-fx-background-color: #3498DB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        refreshButton.setOnAction(e -> refreshData());
        
        // View Leaderboard button
        Button leaderboardButton = new Button("🏆 Leaderboard");
        leaderboardButton.setPrefWidth(150);
        leaderboardButton.setPrefHeight(40);
        leaderboardButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        leaderboardButton.setStyle(
            "-fx-background-color: #F39C12;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        leaderboardButton.setOnAction(e -> showLeaderboard());
        
        // System Stats button
        Button statsButton = new Button("📊 System Stats");
        statsButton.setPrefWidth(150);
        statsButton.setPrefHeight(40);
        statsButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        statsButton.setStyle(
            "-fx-background-color: #9B59B6;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        statsButton.setOnAction(e -> showSystemStats());
        
        // Back button
        Button backButton = new Button("🏠 Back to Menu");
        backButton.setPrefWidth(150);
        backButton.setPrefHeight(40);
        backButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        backButton.setStyle(
            "-fx-background-color: #95A5A6;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> handleBack());
        
        bottomSection.getChildren().addAll(refreshButton, leaderboardButton, statsButton, backButton);
        return bottomSection;
    }
    
    /**
     * Refreshes the admin panel data.
     */
    public void refreshData() {
        ObservableList<PlayerReport> reports = FXCollections.observableArrayList();
        
        for (User user : userService.getAllUsers()) {
            if (user.isPlayer()) {
                reports.add(new PlayerReport(
                    user.getUsername(),
                    user.getTotalGamesPlayed(),
                    String.format("%.1f", user.getBestWPM()),
                    String.format("%.1f", user.getAverageWPM()),
                    String.format("%.1f%%", user.getAverageAccuracy())
                ));
            }
        }
        
        playerTable.setItems(reports);
        
        // Update stats
        String stats = userService.getUserStats() + " | " + scoreManager.getStatistics();
        statsLabel.setText(stats);
    }
    
    /**
     * Shows detailed information for a selected player.
     * 
     * @param username Username of the player
     */
    private void showPlayerDetails(String username) {
        User user = userService.getUserByUsername(username);
        
        if (user == null) {
            detailsArea.setText("Player not found");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("=== PLAYER DETAILS ===\n\n");
        details.append("Username: ").append(user.getUsername()).append("\n");
        details.append("Role: ").append(user.getRole()).append("\n");
        details.append("Total Games: ").append(user.getTotalGamesPlayed()).append("\n\n");
        
        details.append("=== STATISTICS ===\n\n");
        details.append(String.format("Best WPM: %.1f\n", user.getBestWPM()));
        details.append(String.format("Avg WPM: %.1f\n", user.getAverageWPM()));
        details.append(String.format("Avg Accuracy: %.1f%%\n\n", user.getAverageAccuracy()));
        
        details.append("=== RECENT SESSIONS ===\n\n");
        
        int sessionCount = Math.min(5, user.getSessionHistory().size());
        if (sessionCount > 0) {
            for (int i = user.getSessionHistory().size() - 1; 
                 i >= user.getSessionHistory().size() - sessionCount; i--) {
                GameSession session = user.getSessionHistory().get(i);
                details.append(String.format("%s\n", session.getFormattedDate()));
                details.append(String.format("  Mode: %s\n", session.getMode().getDisplayName()));
                details.append(String.format("  WPM: %.1f\n", session.getWpm()));
                details.append(String.format("  Accuracy: %.1f%%\n", session.getAccuracy()));
                details.append(String.format("  Errors: %d\n\n", session.getErrorCount()));
            }
        } else {
            details.append("No sessions recorded\n");
        }
        
        detailsArea.setText(details.toString());
    }
    
    /**
     * Shows the leaderboard dialog.
     */
    private void showLeaderboard() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Leaderboard");
        alert.setHeaderText("Top 20 Players");
        
        String leaderboard = scoreManager.generateLeaderboard(20);
        
        TextArea textArea = new TextArea(leaderboard);
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setFont(Font.font("Courier New", 12));
        textArea.setPrefSize(700, 500);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    /**
     * Shows system statistics dialog.
     */
    private void showSystemStats() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("System Statistics");
        alert.setHeaderText("Overall System Statistics");
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== USER STATISTICS ===\n\n");
        stats.append(userService.getUserStats()).append("\n\n");
        
        stats.append("=== SCORE STATISTICS ===\n\n");
        stats.append(scoreManager.getStatistics()).append("\n\n");
        
        stats.append("=== DETAILED BREAKDOWN ===\n\n");
        stats.append(String.format("Total Scores Recorded: %d\n", scoreManager.getTotalScoreCount()));
        stats.append(String.format("Unique Players: %d\n", scoreManager.getUniquePlayerCount()));
        stats.append(String.format("Highest WPM: %.1f\n", scoreManager.getHighestWPM()));
        stats.append(String.format("Average WPM: %.1f\n", scoreManager.getAverageWPM()));
        
        TextArea textArea = new TextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setFont(Font.font("Courier New", 12));
        textArea.setPrefSize(500, 400);
        
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
     * Gets the admin scene.
     * 
     * @return Admin scene
     */
    public Scene getScene() {
        return adminScene;
    }
    
    /**
     * Inner class representing a player report for the table.
     */
    public static class PlayerReport {
        private String username;
        private int totalGames;
        private String bestWpm;
        private String avgWpm;
        private String avgAccuracy;
        
        public PlayerReport(String username, int totalGames, String bestWpm, 
                          String avgWpm, String avgAccuracy) {
            this.username = username;
            this.totalGames = totalGames;
            this.bestWpm = bestWpm;
            this.avgWpm = avgWpm;
            this.avgAccuracy = avgAccuracy;
        }
        
        public String getUsername() { return username; }
        public int getTotalGames() { return totalGames; }
        public String getBestWpm() { return bestWpm; }
        public String getAvgWpm() { return avgWpm; }
        public String getAvgAccuracy() { return avgAccuracy; }
    }
}
