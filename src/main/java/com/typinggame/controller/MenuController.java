package com.typinggame.controller;

import com.typinggame.model.GameMode;
import com.typinggame.model.User;
import com.typinggame.service.UserService;
import com.typinggame.service.ScoreManager;
import com.typinggame.util.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for the Main Menu screen.
 * Provides navigation to game modes, history, admin panel, and logout.
 */
public class MenuController {
    
    private UserService userService;
    private ScoreManager scoreManager;
    private SceneManager sceneManager;
    private Scene menuScene;
    
    // UI Components
    private Label welcomeLabel;
    private Label statsLabel;
    private Button timedModeButton;
    private Button freePracticeButton;
    private Button viewHistoryButton;
    private Button adminPanelButton;
    private Button logoutButton;
    
    /**
     * Constructor initializes the menu controller.
     * 
     * @param userService Service for user management
     * @param scoreManager Service for score management
     * @param sceneManager Manager for scene navigation
     */
    public MenuController(UserService userService, ScoreManager scoreManager, 
                         SceneManager sceneManager) {
        this.userService = userService;
        this.scoreManager = scoreManager;
        this.sceneManager = sceneManager;
        this.menuScene = createMenuScene();
    }
    
    /**
     * Creates the menu scene with all UI components.
     * 
     * @return Configured menu scene
     */
    private Scene createMenuScene() {
        // Main container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");
        
        // Top section - Welcome and user info
        VBox topSection = createTopSection();
        mainContainer.setTop(topSection);
        
        // Center section - Game mode buttons
        VBox centerSection = createCenterSection();
        mainContainer.setCenter(centerSection);
        
        // Bottom section - Additional options
        HBox bottomSection = createBottomSection();
        mainContainer.setBottom(bottomSection);
        
        Scene scene = new Scene(mainContainer, 900, 600);
        return scene;
    }
    
    /**
     * Creates the top section with welcome message and stats.
     * 
     * @return Top section VBox
     */
    private VBox createTopSection() {
        VBox topSection = new VBox(10);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(30, 20, 20, 20));
        
        welcomeLabel = new Label("Welcome!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        welcomeLabel.setTextFill(Color.WHITE);
        
        statsLabel = new Label("");
        statsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statsLabel.setTextFill(Color.web("#E0E0E0"));
        
        topSection.getChildren().addAll(welcomeLabel, statsLabel);
        return topSection;
    }
    
    /**
     * Creates the center section with game mode buttons.
     * 
     * @return Center section VBox
     */
    private VBox createCenterSection() {
        VBox centerSection = new VBox(20);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(20));
        
        Label selectModeLabel = new Label("Select Game Mode");
        selectModeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        selectModeLabel.setTextFill(Color.WHITE);
        
        // Game mode buttons container
        HBox gameModeButtons = new HBox(30);
        gameModeButtons.setAlignment(Pos.CENTER);
        
        // Timed Mode button
        timedModeButton = createGameModeButton(
            "Timed Mode",
            "Type as fast as you can\nwithin 60 seconds",
            "#FF6B6B"
        );
        timedModeButton.setOnAction(e -> handleTimedMode());
        
        // Free Practice button
        freePracticeButton = createGameModeButton(
            "📝 Free Practice",
            "Practice without\ntime pressure",
            "#4ECDC4"
        );
        freePracticeButton.setOnAction(e -> handleFreePractice());
        
        gameModeButtons.getChildren().addAll(timedModeButton, freePracticeButton);
        
        centerSection.getChildren().addAll(selectModeLabel, gameModeButtons);
        return centerSection;
    }
    
    /**
     * Creates a styled game mode button.
     * 
     * @param title Button title
     * @param description Button description
     * @param color Button color
     * @return Styled button
     */
    private Button createGameModeButton(String title, String description, String color) {
        VBox buttonContent = new VBox(10);
        buttonContent.setAlignment(Pos.CENTER);
        buttonContent.setPadding(new Insets(30));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.web("#F0F0F0"));
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setWrapText(true);
        
        buttonContent.getChildren().addAll(titleLabel, descLabel);
        
        Button button = new Button();
        button.setGraphic(buttonContent);
        button.setPrefSize(250, 150);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: derive(" + color + ", -10%);" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 8);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);"
        ));
        
        return button;
    }
    
    /**
     * Creates the bottom section with additional options.
     * 
     * @return Bottom section HBox
     */
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(15);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20, 20, 30, 20));
        
        // View History button
        viewHistoryButton = createBottomButton("View History");
        viewHistoryButton.setOnAction(e -> handleViewHistory());

        // Admin Panel button (only visible for admins)
        adminPanelButton = createBottomButton("Admin Panel");
        adminPanelButton.setOnAction(e -> handleAdminPanel());

        // Logout button
        logoutButton = createBottomButton("Logout");
        logoutButton.setOnAction(e -> handleLogout());
        
        bottomSection.getChildren().addAll(viewHistoryButton, adminPanelButton, logoutButton);
        return bottomSection;
    }
    
    /**
     * Creates a styled bottom button.
     * 
     * @param text Button text
     * @return Styled button
     */
    private Button createBottomButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(45);
        button.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        button.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.3);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        
        return button;
    }
    
    /**
     * Handles Timed Mode button action.
     */
    private void handleTimedMode() {
        System.out.println("Starting Timed Mode...");
        sceneManager.navigateTo(SceneManager.GAME_SCENE, GameMode.TIMED);
    }

    /**
     * Handles Free Practice button action.
     */
    private void handleFreePractice() {
        System.out.println("Starting Free Practice Mode...");
        // Navigate to dedicated practice scene (no timer)
        sceneManager.navigateTo(SceneManager.PRACTICE_SCENE);
    }
    
    /**
     * Handles View History button action.
     */
    private void handleViewHistory() {
        System.out.println("Opening History...");
        sceneManager.navigateTo(SceneManager.HISTORY_SCENE);
    }
    
    /**
     * Handles Admin Panel button action.
     */
    private void handleAdminPanel() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser != null && currentUser.isAdmin()) {
            System.out.println("Opening Admin Panel...");
            sceneManager.navigateTo(SceneManager.ADMIN_SCENE);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Admin Access Required");
            alert.setContentText("You must be an administrator to access this feature.");
            alert.showAndWait();
        }
    }
    
    /**
     * Handles Logout button action.
     */
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.logout();
                sceneManager.clearHistory();
                sceneManager.navigateTo(SceneManager.LOGIN_SCENE);
            }
        });
    }
    
    /**
     * Updates the menu display with current user information.
     * Called when the menu scene is shown.
     */
    public void updateDisplay() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            
            // Update stats
            String stats = String.format(
                "Total Games: %d | Best WPM: %.1f | Average WPM: %.1f | Average Accuracy: %.1f%%",
                currentUser.getTotalGamesPlayed(),
                currentUser.getBestWPM(),
                currentUser.getAverageWPM(),
                currentUser.getAverageAccuracy()
            );
            statsLabel.setText(stats);
            
            // Show/hide admin button based on role
            adminPanelButton.setVisible(currentUser.isAdmin());
            adminPanelButton.setManaged(currentUser.isAdmin());
        }
    }
    
    /**
     * Gets the menu scene.
     * 
     * @return Menu scene
     */
    public Scene getScene() {
        return menuScene;
    }
}
