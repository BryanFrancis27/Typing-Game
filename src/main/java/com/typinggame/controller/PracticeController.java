package com.typinggame.controller;

import com.typinggame.model.GameMode;
import com.typinggame.model.GameSession;
import com.typinggame.model.User;
import com.typinggame.service.GameService;
import com.typinggame.service.PerformanceTracker;
import com.typinggame.service.ScoreManager;
import com.typinggame.service.UserService;
import com.typinggame.util.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for the Free Practice game screen.
 * Provides an untimed typing experience using paragraph content with
 * real-time WPM and accuracy, but no countdown timer or pause button.
 */
public class PracticeController {

    private final UserService userService;
    private final GameService gameService;
    private final PerformanceTracker performanceTracker;
    private final ScoreManager scoreManager;
    private final SceneManager sceneManager;
    private final Scene practiceScene;

    // Game state
    private boolean gameActive;
    private String currentSentence;

    // UI components
    private Label wpmLabel;
    private Label accuracyLabel;
    private Label keystrokesLabel;
    private Label instructionLabel;
    private Label paragraphLabel;
    private TextField inputField;
    private ProgressBar progressBar;
    private Label progressLabel;
    private Button quitButton;

    public PracticeController(UserService userService,
                              GameService gameService,
                              PerformanceTracker performanceTracker,
                              ScoreManager scoreManager,
                              SceneManager sceneManager) {
        this.userService = userService;
        this.gameService = gameService;
        this.performanceTracker = performanceTracker;
        this.scoreManager = scoreManager;
        this.sceneManager = sceneManager;
        this.practiceScene = createPracticeScene();
        this.gameActive = false;
    }

    private Scene createPracticeScene() {
        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #2C3E50;");

        HBox top = createTopSection();
        main.setTop(top);

        VBox center = createCenterSection();
        main.setCenter(center);

        HBox bottom = createBottomSection();
        main.setBottom(bottom);

        return new Scene(main, 900, 600);
    }

    private HBox createTopSection() {
        HBox top = new HBox(30);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20));
        top.setStyle("-fx-background-color: #34495E;");

        // WPM
        VBox wpmBox = createStatBox("WPM", "0.0");
        wpmLabel = (Label) ((VBox) wpmBox.getChildren().get(0)).getChildren().get(1);

        // Accuracy
        VBox accuracyBox = createStatBox("Accuracy", "100%");
        accuracyLabel = (Label) ((VBox) accuracyBox.getChildren().get(0)).getChildren().get(1);

        // Keystrokes
        VBox keysBox = createStatBox("Keystrokes", "0 / 0");
        keystrokesLabel = (Label) ((VBox) keysBox.getChildren().get(0)).getChildren().get(1);

        top.getChildren().addAll(wpmBox, accuracyBox, keysBox);
        return top;
    }

    private VBox createStatBox(String title, String initialValue) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(15));
        statBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                "-fx-background-radius: 10;"
        );
        statBox.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.web("#BDC3C7"));

        Label valueLabel = new Label(initialValue);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.WHITE);

        VBox inner = new VBox(5);
        inner.setAlignment(Pos.CENTER);
        inner.getChildren().addAll(titleLabel, valueLabel);

        statBox.getChildren().add(inner);
        return statBox;
    }

    private VBox createCenterSection() {
        VBox center = new VBox(30);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        // Progress bar
        VBox progressBox = new VBox(10);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.setMaxWidth(600);

        progressLabel = new Label("Progress: 0%");
        progressLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        progressLabel.setTextFill(Color.web("#BDC3C7"));

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(600);
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-accent: #3498DB;");

        progressBox.getChildren().addAll(progressLabel, progressBar);

        // Paragraph display
        VBox paragraphBox = new VBox(20);
        paragraphBox.setAlignment(Pos.CENTER);
        paragraphBox.setPadding(new Insets(40));
        paragraphBox.setMaxWidth(800);
        paragraphBox.setPrefWidth(800);
        paragraphBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #3498DB;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;"
        );

        instructionLabel = new Label("Free Practice: Start typing to practice at your own pace.");
        instructionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        instructionLabel.setTextFill(Color.web("#BDC3C7"));

        paragraphLabel = new Label("Loading practice paragraph...");
        paragraphLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        paragraphLabel.setTextFill(Color.web("#3498DB"));
        paragraphLabel.setWrapText(true);
        paragraphLabel.setAlignment(Pos.CENTER);
        paragraphLabel.setMaxWidth(750);
        paragraphLabel.setPrefWidth(750);
        paragraphLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

        paragraphBox.getChildren().addAll(instructionLabel, paragraphLabel);

        // Input field
        inputField = new TextField();
        inputField.setPromptText("Type here...");
        inputField.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        inputField.setMaxWidth(700);
        inputField.setPrefHeight(60);
        inputField.setAlignment(Pos.CENTER_LEFT);
        inputField.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #3498DB;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;"
        );

        // Typing handler
        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (gameActive) {
                handleTyping(newVal);
            }
        });

        // Enter key submits current line (checks current sentence)
        inputField.setOnAction(e -> {
            if (gameActive) {
                checkSentence();
            }
        });

        center.getChildren().addAll(progressBox, paragraphBox, inputField);
        return center;
    }

    private HBox createBottomSection() {
        HBox bottom = new HBox(20);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(20));

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

        bottom.getChildren().add(quitButton);
        return bottom;
    }

    /**
     * Called when the practice scene becomes active.
     * Prepares a new practice session and enables typing immediately.
     */
    public void startPractice() {
        gameActive = true;

        // Reset metrics and content
        performanceTracker.reset();
        gameService.reset();
        currentSentence = null;

        wpmLabel.setText("0.0");
        accuracyLabel.setText("100%");
        keystrokesLabel.setText("0 / 0");
        progressBar.setProgress(0);
        progressLabel.setText("Progress: 0%");
        inputField.clear();
        inputField.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #3498DB;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;"
        );

        // Load multiple practice sentences for this session
        gameService.loadContent(GameMode.FREE_PRACTICE, "sentences", 5);
        loadNextSentence();

        // Start tracking and enable input immediately
        performanceTracker.startTracking();
        inputField.setDisable(false);
        inputField.requestFocus();

        System.out.println("Free practice session started.");
    }

    private void handleTyping(String typedText) {
        if (currentSentence == null || typedText.isEmpty()) {
            return;
        }

        // Live visual feedback
        if (currentSentence.startsWith(typedText)) {
            paragraphLabel.setTextFill(Color.web("#2ECC71"));
            inputField.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #2ECC71;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 10;"
            );
        } else {
            paragraphLabel.setTextFill(Color.web("#E74C3C"));
            inputField.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #E74C3C;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 10;"
            );
        }

        // Auto-check when the user has typed at least the current sentence length
        if (typedText.length() >= currentSentence.length()) {
            checkSentence();
        }
    }

    /**
     * Loads the next sentence from the game service. Ends the session when there
     * are no more sentences left in this practice set.
     */
    private void loadNextSentence() {
        currentSentence = gameService.getNextWord();

        if (currentSentence != null) {
            paragraphLabel.setText(currentSentence);
            paragraphLabel.setTextFill(Color.web("#3498DB"));
            inputField.clear();
            inputField.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #3498DB;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;"
            );
            updateProgress();
        } else {
            // No more sentences – end the practice session
            endPractice();
        }
    }

    /**
     * Compares the typed text with the current sentence and updates metrics.
     * Advances to the next sentence until all are completed.
     */
    private void checkSentence() {
        String typedText = inputField.getText();

        if (typedText.isEmpty() || currentSentence == null) {
            return;
        }

        int minLength = Math.min(currentSentence.length(), typedText.length());

        for (int i = 0; i < minLength; i++) {
            char expected = currentSentence.charAt(i);
            char typed = typedText.charAt(i);

            if (expected == typed) {
                performanceTracker.recordCorrectCharacter(typed);
            } else {
                performanceTracker.recordError(expected, typed);
            }
        }

        // Handle missing or extra characters for this sentence
        if (typedText.length() < currentSentence.length()) {
            for (int i = typedText.length(); i < currentSentence.length(); i++) {
                performanceTracker.recordError(currentSentence.charAt(i), ' ');
            }
        } else if (typedText.length() > currentSentence.length()) {
            for (int i = currentSentence.length(); i < typedText.length(); i++) {
                performanceTracker.recordError(' ', typedText.charAt(i));
            }
        }

        updateStats();
        updateProgress();

        // Advance to the next sentence in the practice set
        loadNextSentence();
    }

    private void updateStats() {
        wpmLabel.setText(String.format("%.1f", performanceTracker.getCurrentWPM()));
        accuracyLabel.setText(String.format("%.1f%%", performanceTracker.getCurrentAccuracy()));
        keystrokesLabel.setText(
                performanceTracker.getCorrectCharacters() + " / " + performanceTracker.getErrorCount()
        );
    }

    private void updateProgress() {
        int delivered = gameService.getWordsDelivered();
        int remaining = gameService.getRemainingWordCount();
        int total = delivered + remaining;

        if (total <= 0) {
            progressBar.setProgress(0);
            progressLabel.setText("Progress: 0 sentences");
            return;
        }

        double progress = (double) delivered / total;
        progressBar.setProgress(progress);
        progressLabel.setText(String.format("Sentence %d of %d", delivered, total));
    }

    private void handleQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Practice");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Your practice progress will be lost if you quit now.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                gameActive = false;
                performanceTracker.stopTracking();
                sceneManager.navigateTo(SceneManager.MENU_SCENE);
            }
        });
    }

    /**
     * Ends the practice session, saves it to history, and navigates to results.
     */
    private void endPractice() {
        gameActive = false;
        performanceTracker.stopTracking();
        inputField.setDisable(true);

        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            GameSession session = new GameSession(
                    currentUser.getUsername(),
                    GameMode.FREE_PRACTICE,
                    performanceTracker.getElapsedSeconds(),
                    performanceTracker.getTotalCharactersTyped(),
                    performanceTracker.getCorrectCharacters(),
                    performanceTracker.getErrorCount()
            );

            currentUser.addSession(session);
            scoreManager.addScoreFromSession(session);

            System.out.println("Practice session ended. Session saved.");
        }

        sceneManager.navigateTo(SceneManager.RESULTS_SCENE);
    }

    public Scene getScene() {
        return practiceScene;
    }
}


