package com.typinggame.controller;

import com.typinggame.model.GameSession;
import com.typinggame.model.User;
import com.typinggame.service.PerformanceTracker;
import com.typinggame.service.ScoreManager;
import com.typinggame.service.UserService;
import com.typinggame.util.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for the Results screen.
 * Displays game session results and performance statistics.
 */
public class ResultsController {
    
    private UserService userService;
    private ScoreManager scoreManager;
    private PerformanceTracker performanceTracker;
    private SceneManager sceneManager;
    private Scene resultsScene;
    
    // UI Components
    private Label titleLabel;
    private Label gradeLabel;
    private Label wpmLabel;
    private Label accuracyLabel;
    private Label errorsLabel;
    private Label timeLabel;
    private Label rankLabel;
    private VBox statsContainer;
    
    /**
     * Constructor initializes the results controller.
     * 
     * @param userService Service for user management
     * @param scoreManager Service for score management
     * @param performanceTracker Service for performance tracking
     * @param sceneManager Manager for scene navigation
     */
    public ResultsController(UserService userService, ScoreManager scoreManager,
                            PerformanceTracker performanceTracker, SceneManager sceneManager) {
        this.userService = userService;
        this.scoreManager = scoreManager;
        this.performanceTracker = performanceTracker;
        this.sceneManager = sceneManager;
        this.resultsScene = createResultsScene();
    }
    
    /**
     * Creates the results scene with all UI components.
     * 
     * @return Configured results scene
     */
    private Scene createResultsScene() {
        // Main container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72 0%, #2a5298 100%);");
        
        // Center section - Results display
        VBox centerSection = createCenterSection();
        mainContainer.setCenter(centerSection);
        
        // Bottom section - Action buttons
        HBox bottomSection = createBottomSection();
        mainContainer.setBottom(bottomSection);
        
        Scene scene = new Scene(mainContainer, 900, 600);
        return scene;
    }
    
    /**
     * Creates the center section with results display.
     * 
     * @return Center section VBox
     */
    private VBox createCenterSection() {
        VBox centerSection = new VBox(30);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(40));
        
        // Title
        titleLabel = new Label("🎉 Game Complete!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        titleLabel.setTextFill(Color.WHITE);
        
        // Grade display
        gradeLabel = new Label("A+");
        gradeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        gradeLabel.setTextFill(Color.web("#FFD700"));
        gradeLabel.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 30, 0, 0, 0);"
        );
        
        // Stats container
        statsContainer = new VBox(20);
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.setPadding(new Insets(30));
        statsContainer.setMaxWidth(700);
        statsContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;"
        );
        
        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(40);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);
        
        // WPM
        VBox wpmBox = createStatDisplay("⚡ Words Per Minute", "0.0");
        wpmLabel = (Label) wpmBox.getChildren().get(1);
        
        // Accuracy
        VBox accuracyBox = createStatDisplay("🎯 Accuracy", "0%");
        accuracyLabel = (Label) accuracyBox.getChildren().get(1);
        
        // Errors
        VBox errorsBox = createStatDisplay("❌ Errors", "0");
        errorsLabel = (Label) errorsBox.getChildren().get(1);
        
        // Time
        VBox timeBox = createStatDisplay("⏱️ Time", "0s");
        timeLabel = (Label) timeBox.getChildren().get(1);
        
        statsGrid.add(wpmBox, 0, 0);
        statsGrid.add(accuracyBox, 1, 0);
        statsGrid.add(errorsBox, 0, 1);
        statsGrid.add(timeBox, 1, 1);
        
        // Rank info
        rankLabel = new Label("");
        rankLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        rankLabel.setTextFill(Color.web("#FFD700"));
        
        statsContainer.getChildren().addAll(statsGrid, rankLabel);
        
        centerSection.getChildren().addAll(titleLabel, gradeLabel, statsContainer);
        return centerSection;
    }
    
    /**
     * Creates a stat display box.
     * 
     * @param label Stat label
     * @param value Initial value
     * @return Stat display VBox
     */
    private VBox createStatDisplay(String label, String value) {
        VBox statBox = new VBox(8);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPrefWidth(250);
        
        Label labelText = new Label(label);
        labelText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        labelText.setTextFill(Color.web("#BDC3C7"));
        
        Label valueText = new Label(value);
        valueText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        valueText.setTextFill(Color.WHITE);
        
        statBox.getChildren().addAll(labelText, valueText);
        return statBox;
    }
    
    /**
     * Creates the bottom section with action buttons.
     * 
     * @return Bottom section HBox
     */
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(30));
        
        // Play Again button
        Button playAgainButton = new Button("🔄 Play Again");
        playAgainButton.setPrefWidth(200);
        playAgainButton.setPrefHeight(50);
        playAgainButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playAgainButton.setStyle(
            "-fx-background-color: #2ECC71;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );
        playAgainButton.setOnAction(e -> handlePlayAgain());
        
        // View Leaderboard button
        Button leaderboardButton = new Button("🏆 Leaderboard");
        leaderboardButton.setPrefWidth(200);
        leaderboardButton.setPrefHeight(50);
        leaderboardButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        leaderboardButton.setStyle(
            "-fx-background-color: #3498DB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );
        leaderboardButton.setOnAction(e -> handleLeaderboard());
        
        // Main Menu button
        Button menuButton = new Button("🏠 Main Menu");
        menuButton.setPrefWidth(200);
        menuButton.setPrefHeight(50);
        menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        menuButton.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        menuButton.setOnAction(e -> handleMainMenu());
        
        bottomSection.getChildren().addAll(playAgainButton, leaderboardButton, menuButton);
        return bottomSection;
    }
    
    /**
     * Updates the results display with the latest game session data.
     */
    public void updateResults() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            showPlaceholderResults();
            return;
        }
        
        // Get the most recent session
        if (currentUser.getSessionHistory().isEmpty()) {
            showPlaceholderResults();
            return;
        }
        
        GameSession lastSession = currentUser.getSessionHistory()
            .get(currentUser.getSessionHistory().size() - 1);
        
        // Update stats
        wpmLabel.setText(String.format("%.1f", lastSession.getWpm()));
        accuracyLabel.setText(String.format("%.1f%%", lastSession.getAccuracy()));
        errorsLabel.setText(String.valueOf(lastSession.getErrorCount()));
        timeLabel.setText(lastSession.getDuration() + "s");
        
        // Update grade
        String grade = lastSession.getPerformanceGrade();
        gradeLabel.setText(grade);
        
        // Set grade color
        Color gradeColor = getGradeColor(grade);
        gradeLabel.setTextFill(gradeColor);
        
        // Update rank
        int rank = scoreManager.getUserRank(currentUser.getUsername());
        if (rank > 0) {
            rankLabel.setText(String.format("🏆 Your Rank: #%d", rank));
        } else {
            rankLabel.setText("🏆 Keep practicing to get ranked!");
        }
        
        // Update title based on performance
        if (lastSession.getWpm() >= 60 && lastSession.getAccuracy() >= 95) {
            titleLabel.setText("🌟 Outstanding Performance!");
        } else if (lastSession.getWpm() >= 40 && lastSession.getAccuracy() >= 90) {
            titleLabel.setText("🎉 Great Job!");
        } else if (lastSession.getWpm() >= 20) {
            titleLabel.setText("👍 Good Effort!");
        } else {
            titleLabel.setText("💪 Keep Practicing!");
        }
    }
    
    /**
     * Shows placeholder results when no session data is available.
     */
    private void showPlaceholderResults() {
        titleLabel.setText("Ready to Play?");
        gradeLabel.setText("-");
        gradeLabel.setTextFill(Color.web("#BDC3C7"));
        wpmLabel.setText("0.0");
        accuracyLabel.setText("0%");
        errorsLabel.setText("0");
        timeLabel.setText("0s");
        rankLabel.setText("Start a game to see your results");
    }
    
    /**
     * Gets the color for a grade.
     * 
     * @param grade Grade string
     * @return Color for the grade
     */
    private Color getGradeColor(String grade) {
        switch (grade) {
            case "A+":
            case "A":
                return Color.web("#FFD700"); // Gold
            case "B":
                return Color.web("#C0C0C0"); // Silver
            case "C":
                return Color.web("#CD7F32"); // Bronze
            case "D":
                return Color.web("#87CEEB"); // Sky Blue
            default:
                return Color.web("#FF6B6B"); // Red
        }
    }
    
    /**
     * Handles Play Again button action.
     */
    private void handlePlayAgain() {
        sceneManager.navigateTo(SceneManager.GAME_SCENE);
    }
    
    /**
     * Handles Leaderboard button action.
     */
    private void handleLeaderboard() {
        // Show leaderboard dialog
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Leaderboard");
        alert.setHeaderText("Top 10 Players");
        
        String leaderboard = scoreManager.generateLeaderboard(10);
        
        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(leaderboard);
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setFont(Font.font("Courier New", 12));
        textArea.setPrefSize(600, 400);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    /**
     * Handles Main Menu button action.
     */
    private void handleMainMenu() {
        sceneManager.navigateTo(SceneManager.MENU_SCENE);
    }
    
    /**
     * Gets the results scene.
     * 
     * @return Results scene
     */
    public Scene getScene() {
        return resultsScene;
    }
}
