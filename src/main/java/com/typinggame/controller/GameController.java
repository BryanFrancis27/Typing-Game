package com.typinggame.controller;

import com.typinggame.model.GameMode;
import com.typinggame.model.GameSession;
import com.typinggame.model.User;
import com.typinggame.service.GameService;
import com.typinggame.service.PerformanceTracker;
import com.typinggame.service.ScoreManager;
import com.typinggame.service.UserService;
import com.typinggame.util.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Controller for the Game screen.
 * Handles the main typing gameplay, real-time performance tracking, and timer management.
 */
public class GameController {
    
    private UserService userService;
    private GameService gameService;
    private PerformanceTracker performanceTracker;
    private ScoreManager scoreManager;
    private SceneManager sceneManager;
    private Scene gameScene;
    
    // Game state
    private GameMode currentMode;
    private Timeline gameTimer;
    private int timeRemaining;
    private boolean gameActive;
    private String currentWord;
    
    // UI Components
    private Label timerLabel;
    private Label wpmLabel;
    private Label accuracyLabel;
    private Label errorLabel;
    private Label wordToTypeLabel;
    private Label instructionLabel;
    private TextField inputField;
    private ProgressBar progressBar;
    private Label progressLabel;
    private Button startButton;
    private Button pauseButton;
    private Button quitButton;
    
    /**
     * Constructor initializes the game controller.
     * 
     * @param userService Service for user management
     * @param gameService Service for game content
     * @param performanceTracker Service for performance tracking
     * @param scoreManager Service for score management
     * @param sceneManager Manager for scene navigation
     */
    public GameController(UserService userService, GameService gameService,
                         PerformanceTracker performanceTracker, ScoreManager scoreManager,
                         SceneManager sceneManager) {
        this.userService = userService;
        this.gameService = gameService;
        this.performanceTracker = performanceTracker;
        this.scoreManager = scoreManager;
        this.sceneManager = sceneManager;
        this.gameScene = createGameScene();
        this.gameActive = false;
    }
    
    /**
     * Creates the game scene with all UI components.
     * 
     * @return Configured game scene
     */
    private Scene createGameScene() {
        // Main container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: #2C3E50;");
        
        // Top section - Stats and timer
        HBox topSection = createTopSection();
        mainContainer.setTop(topSection);
        
        // Center section - Word display and input
        VBox centerSection = createCenterSection();
        mainContainer.setCenter(centerSection);
        
        // Bottom section - Controls
        HBox bottomSection = createBottomSection();
        mainContainer.setBottom(bottomSection);
        
        Scene scene = new Scene(mainContainer, 900, 600);
        return scene;
    }
    
    /**
     * Creates the top section with stats display.
     * 
     * @return Top section HBox
     */
    private HBox createTopSection() {
        HBox topSection = new HBox(30);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20));
        topSection.setStyle("-fx-background-color: #34495E;");
        
        // Timer
        VBox timerBox = createStatBox("Time", "60s");
        timerLabel = (Label) ((VBox) timerBox.getChildren().get(0)).getChildren().get(1);

        // WPM
        VBox wpmBox = createStatBox("WPM", "0.0");
        wpmLabel = (Label) ((VBox) wpmBox.getChildren().get(0)).getChildren().get(1);

        // Accuracy
        VBox accuracyBox = createStatBox("Accuracy", "100%");
        accuracyLabel = (Label) ((VBox) accuracyBox.getChildren().get(0)).getChildren().get(1);

        // Keystrokes (Correct / Errors)
        VBox errorBox = createStatBox("Keystrokes", "0 / 0");
        errorLabel = (Label) ((VBox) errorBox.getChildren().get(0)).getChildren().get(1);
        
        topSection.getChildren().addAll(timerBox, wpmBox, accuracyBox, errorBox);
        return topSection;
    }
    
    /**
     * Creates a stat display box.
     * 
     * @param title Stat title
     * @param initialValue Initial value
     * @return Stat box VBox
     */
    private VBox createStatBox(String title, String initialValue) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(15));
        statBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 10;"
        );
        statBox.setPrefWidth(180);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.web("#BDC3C7"));
        
        Label valueLabel = new Label(initialValue);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.WHITE);
        
        VBox innerBox = new VBox(5);
        innerBox.setAlignment(Pos.CENTER);
        innerBox.getChildren().addAll(titleLabel, valueLabel);
        
        statBox.getChildren().add(innerBox);
        return statBox;
    }
    
    /**
     * Creates the center section with word display and input.
     * 
     * @return Center section VBox
     */
    private VBox createCenterSection() {
        VBox centerSection = new VBox(30);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(40));
        
        // Progress bar
        VBox progressBox = new VBox(10);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.setMaxWidth(600);
        
        progressLabel = new Label("Word 0 of 0");
        progressLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        progressLabel.setTextFill(Color.web("#BDC3C7"));
        
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(600);
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-accent: #3498DB;");
        
        progressBox.getChildren().addAll(progressLabel, progressBar);
        
        // Word to type display
        VBox wordBox = new VBox(20);
        wordBox.setAlignment(Pos.CENTER);
        wordBox.setPadding(new Insets(40));
        wordBox.setMaxWidth(800);
        wordBox.setPrefWidth(800);
        wordBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.05);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;"
        );

        instructionLabel = new Label("Type the word below:");
        instructionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        instructionLabel.setTextFill(Color.web("#BDC3C7"));

        wordToTypeLabel = new Label("Ready to Start");
        wordToTypeLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        wordToTypeLabel.setTextFill(Color.web("#3498DB"));
        wordToTypeLabel.setWrapText(true);
        wordToTypeLabel.setAlignment(Pos.CENTER);
        wordToTypeLabel.setMaxWidth(650);
        wordToTypeLabel.setPrefWidth(650);
        wordToTypeLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

        wordBox.getChildren().addAll(instructionLabel, wordToTypeLabel);
        
        // Input field
        inputField = new TextField();
        inputField.setPromptText("Type here...");
        inputField.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        inputField.setMaxWidth(600);
        inputField.setPrefHeight(60);
        inputField.setAlignment(Pos.CENTER);
        inputField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        inputField.setDisable(true);
        
        // Handle typing
        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (gameActive) {
                handleTyping(newVal);
            }
        });
        
        // Handle Enter key
        inputField.setOnAction(e -> {
            if (gameActive) {
                checkWord();
            }
        });
        
        centerSection.getChildren().addAll(progressBox, wordBox, inputField);
        return centerSection;
    }
    
    /**
     * Creates the bottom section with control buttons.
     * 
     * @return Bottom section HBox
     */
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20));

        startButton = new Button("Start Game");
        startButton.setPrefWidth(150);
        startButton.setPrefHeight(45);
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        startButton.setStyle(
            "-fx-background-color: #27AE60;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        startButton.setOnAction(e -> handleStart());
        startButton.setVisible(false);
        startButton.setManaged(false);

        pauseButton = new Button("Pause");
        pauseButton.setPrefWidth(150);
        pauseButton.setPrefHeight(45);
        pauseButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        pauseButton.setStyle(
            "-fx-background-color: #F39C12;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        pauseButton.setOnAction(e -> handlePause());
        pauseButton.setDisable(true);

        quitButton = new Button("Quit");
        quitButton.setPrefWidth(150);
        quitButton.setPrefHeight(45);
        quitButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        quitButton.setStyle(
            "-fx-background-color: #E74C3C;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        quitButton.setOnAction(e -> handleQuit());

        bottomSection.getChildren().addAll(startButton, pauseButton, quitButton);
        return bottomSection;
    }
    
    /**
     * Starts a new game with the specified mode.
     * Called when entering the game scene.
     *
     * @param mode Game mode to play
     */
    public void startGame(GameMode mode) {
        this.currentMode = mode;
        this.gameActive = false;

        // Reset UI
        if (mode.isTimed()) {
            // Show default duration for timed games
            timerLabel.setText(mode.getDefaultDuration() + "s");
            // Show pause button (initially disabled until game starts)
            pauseButton.setVisible(true);
            pauseButton.setManaged(true);
            pauseButton.setDisable(true);
        } else {
            // Practice mode: no active timer countdown
            timerLabel.setText("");
            // Hide pause button entirely in practice mode
            pauseButton.setVisible(false);
            pauseButton.setManaged(false);
        }
        wpmLabel.setText("0.0");
        accuracyLabel.setText("100%");
        errorLabel.setText("0 / 0");
        progressBar.setProgress(0);
        progressLabel.setText("Word 0 of 0");
        wordToTypeLabel.setText("Ready to Start");
        wordToTypeLabel.setTextFill(Color.web("#3498DB"));
        inputField.clear();
        inputField.setDisable(true);
        pauseButton.setDisable(true);

        // Set subtle instruction based on mode
        if (mode.isTimed()) {
            instructionLabel.setText("Timed Game: Press Start, then begin typing to start the 60s timer.");

            // Show start button with mode-specific label
            startButton.setVisible(true);
            startButton.setManaged(true);
            startButton.setText("Start Timed Game");

            System.out.println("Timed game prepared.");
        } else {
            instructionLabel.setText("Free Practice: Start typing to practice at your own pace.");

            // Practice mode: hide the start button and immediately start the session
            startButton.setVisible(false);
            startButton.setManaged(false);

            System.out.println("Free practice prepared. Starting session immediately...");
            handleStart(); // Begin practice session right away
        }
    }

    /**
     * Starts the actual game.
     * Called when the start button is clicked.
     */
    private void handleStart() {
        if (currentMode == null) {
            return;
        }

        this.gameActive = true;

        // Reset services
        performanceTracker.reset();
        gameService.reset();

        // Load sentence-based content for both modes. The wordCount parameter
        // represents the number of sentences to include in this session.
        gameService.loadContent(currentMode, "sentences", 5);

        // Setup timer
        if (currentMode.isTimed()) {
            timeRemaining = currentMode.getDefaultDuration();
            setupTimer();
        }

        // Start tracking
        performanceTracker.startTracking();

        // Load first word
        loadNextWord();

        // Enable input
        inputField.setDisable(false);
        inputField.clear();
        inputField.requestFocus();
        // Only enable pause in timed mode; practice mode has no pause control
        if (currentMode.isTimed()) {
            pauseButton.setDisable(false);
        }

        // Hide start button
        startButton.setVisible(false);
        startButton.setManaged(false);

        // Update progress
        updateProgress();

        System.out.println("Game started: " + currentMode.getDisplayName());
    }
    
    /**
     * Sets up the game timer for timed mode.
     */
    private void setupTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            timerLabel.setText(timeRemaining + "s");
            
            if (timeRemaining <= 0) {
                endGame();
            } else if (timeRemaining <= 10) {
                timerLabel.setTextFill(Color.web("#E74C3C"));
            }
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        // The timer will be started on the player's first keystroke to ensure
        // the 60-second countdown reflects actual typing time.
    }
    
        /**
         * Loads the next sentence from the game service.
         */
    private void loadNextWord() {
        currentWord = gameService.getNextWord();
        
        if (currentWord != null) {
            wordToTypeLabel.setText(currentWord);
            wordToTypeLabel.setTextFill(Color.web("#3498DB"));
            updateProgress();
        } else {
                // No more sentences remaining in this session
            endGame();
        }
    }
    
    /**
     * Handles typing input and provides real-time feedback.
     * 
     * @param typedText Current text in input field
     */
    private void handleTyping(String typedText) {
        if (currentWord == null || typedText.isEmpty()) {
            return;
        }

        // For timed games, start the countdown on the first keystroke so the
        // 60-second timer aligns with actual typing time rather than idle time.
        if (currentMode != null
                && currentMode.isTimed()
                && gameTimer != null
                && gameTimer.getStatus() != Animation.Status.RUNNING) {
            gameTimer.play();
        }
        
        // Check if typed text matches the beginning of the word
        if (currentWord.startsWith(typedText)) {
            // Correct so far
            wordToTypeLabel.setTextFill(Color.web("#2ECC71"));
            inputField.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #2ECC71;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;"
            );
        } else {
            // Error
            wordToTypeLabel.setTextFill(Color.web("#E74C3C"));
            inputField.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #E74C3C;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;"
            );
        }
        
        // Auto-submit when word is complete
        if (typedText.length() >= currentWord.length()) {
            checkWord();
        }
    }
    
    /**
     * Checks the typed word and updates performance metrics.
     */
    private void checkWord() {
        String typedText = inputField.getText().trim();
        
        if (typedText.isEmpty() || currentWord == null) {
            return;
        }
        
        // Validate typing character by character
        int minLength = Math.min(currentWord.length(), typedText.length());
        
        for (int i = 0; i < minLength; i++) {
            char expected = currentWord.charAt(i);
            char typed = typedText.charAt(i);
            
            if (expected == typed) {
                performanceTracker.recordCorrectCharacter(typed);
            } else {
                performanceTracker.recordError(expected, typed);
            }
        }
        
        // Handle length differences
        if (typedText.length() < currentWord.length()) {
            // Missing characters count as errors
            for (int i = typedText.length(); i < currentWord.length(); i++) {
                performanceTracker.recordError(currentWord.charAt(i), ' ');
            }
        } else if (typedText.length() > currentWord.length()) {
            // Extra characters count as errors
            for (int i = currentWord.length(); i < typedText.length(); i++) {
                performanceTracker.recordError(' ', typedText.charAt(i));
            }
        }
        
        // Update UI
        updateStats();
        
        // Clear input and load next word
        inputField.clear();
        inputField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        loadNextWord();
    }
    
    /**
     * Updates the real-time statistics display.
     */
    private void updateStats() {
        wpmLabel.setText(String.format("%.1f", performanceTracker.getCurrentWPM()));
        accuracyLabel.setText(String.format("%.1f%%", performanceTracker.getCurrentAccuracy()));
        errorLabel.setText(
                performanceTracker.getCorrectCharacters() + " / " + performanceTracker.getErrorCount()
        );
    }
    
    /**
     * Updates the progress bar and label.
     */
    private void updateProgress() {
        int delivered = gameService.getWordsDelivered();
        int remaining = gameService.getRemainingWordCount();
        int total = delivered + remaining;
        
        if (total > 0) {
            double progress = (double) delivered / total;
            progressBar.setProgress(progress);
            progressLabel.setText(String.format("Word %d of %d", delivered, total));
        }
    }
    
    /**
     * Handles pause button action.
     */
    private void handlePause() {
        if (gameActive) {
            gameActive = false;
            if (gameTimer != null) {
                gameTimer.pause();
            }
            inputField.setDisable(true);
            pauseButton.setText("Resume");
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Paused");
            alert.setHeaderText("Game Paused");
            alert.setContentText("Click OK to resume playing.");
            alert.showAndWait();
            
            gameActive = true;
            if (gameTimer != null) {
                gameTimer.play();
            }
            inputField.setDisable(false);
            inputField.requestFocus();
            pauseButton.setText("⏸️ Pause");
        }
    }
    
    /**
     * Handles quit button action.
     */
    private void handleQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Game");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Your progress will be lost if you quit now.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (gameTimer != null) {
                    gameTimer.stop();
                }
                gameActive = false;
                sceneManager.navigateTo(SceneManager.MENU_SCENE);
            }
        });
    }
    
    /**
     * Ends the game and saves the session.
     */
    private void endGame() {
        gameActive = false;
        
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        performanceTracker.stopTracking();
        inputField.setDisable(true);
        pauseButton.setDisable(true);
        
        // Create game session
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            GameSession session = new GameSession(
                currentUser.getUsername(),
                currentMode,
                performanceTracker.getElapsedSeconds(),
                performanceTracker.getTotalCharactersTyped(),
                performanceTracker.getCorrectCharacters(),
                performanceTracker.getErrorCount()
            );
            
            // Save session
            currentUser.addSession(session);
            scoreManager.addScoreFromSession(session);
            
            System.out.println("Game ended. Session saved.");
        }
        
        // Navigate to results
        sceneManager.navigateTo(SceneManager.RESULTS_SCENE);
    }
    
    /**
     * Gets the game scene.
     * 
     * @return Game scene
     */
    public Scene getScene() {
        return gameScene;
    }
    
    /**
     * Gets the current game mode.
     * 
     * @return Current game mode
     */
    public GameMode getCurrentMode() {
        return currentMode;
    }
    
    /**
     * Gets the performance tracker.
     * 
     * @return Performance tracker
     */
    public PerformanceTracker getPerformanceTracker() {
        return performanceTracker;
    }
}
