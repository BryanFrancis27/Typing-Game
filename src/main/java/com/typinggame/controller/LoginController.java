package com.typinggame.controller;

import com.typinggame.model.User;
import com.typinggame.model.Role;
import com.typinggame.service.UserService;
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
 * Controller for the Login screen.
 * Handles user authentication and navigation to the main menu.
 */
public class LoginController {
    
    private UserService userService;
    private SceneManager sceneManager;
    private Scene loginScene;
    
    // UI Components
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Button loginButton;
    private Button registerButton;
    
    /**
     * Constructor initializes the login controller.
     * 
     * @param userService Service for user authentication
     * @param sceneManager Manager for scene navigation
     */
    public LoginController(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
        this.loginScene = createLoginScene();
    }
    
    /**
     * Creates the login scene with all UI components.
     * 
     * @return Configured login scene
     */
    private Scene createLoginScene() {
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");
        
        // Title
        Label titleLabel = new Label("⌨️ Typing Game");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.WHITE);
        
        Label subtitleLabel = new Label("Improve Your Typing Speed & Accuracy");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitleLabel.setTextFill(Color.web("#E0E0E0"));
        
        // Login form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(400);
        formContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);"
        );
        
        // Form title
        Label formTitle = new Label("Login");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        formTitle.setTextFill(Color.web("#333333"));
        
        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        usernameLabel.setTextFill(Color.web("#555555"));
        
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );
        
        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        passwordLabel.setTextFill(Color.web("#555555"));
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );
        
        // Message label for feedback
        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);
        messageLabel.setAlignment(Pos.CENTER);
        
        // Login button
        loginButton = new Button("Login");
        loginButton.setPrefWidth(350);
        loginButton.setPrefHeight(45);
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        loginButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        loginButton.setOnAction(e -> handleLogin());
        
        // Register button
        registerButton = new Button("Create New Account");
        registerButton.setPrefWidth(350);
        registerButton.setPrefHeight(40);
        registerButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        registerButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #667eea;" +
            "-fx-border-color: #667eea;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        registerButton.setOnAction(e -> handleRegister());
        
        // Default credentials info
        Label infoLabel = new Label("Default Login:\nAdmin: admin / admin123\nPlayer: player1 / pass123");
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        infoLabel.setTextFill(Color.web("#888888"));
        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 10; -fx-background-radius: 5;");
        
        // Add components to form
        formContainer.getChildren().addAll(
            formTitle,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            messageLabel,
            loginButton,
            registerButton,
            infoLabel
        );
        
        // Add all to main container
        mainContainer.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            formContainer
        );
        
        // Enable Enter key to login
        passwordField.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> passwordField.requestFocus());
        
        Scene scene = new Scene(mainContainer, 900, 600);
        return scene;
    }
    
    /**
     * Handles the login button action.
     * Validates credentials and navigates to menu on success.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password", "error");
            return;
        }
        
        // Attempt login
        User user = userService.login(username, password);
        
        if (user != null) {
            showMessage("Login successful! Welcome, " + user.getUsername(), "success");
            
            // Clear fields
            usernameField.clear();
            passwordField.clear();
            messageLabel.setText("");
            
            // Navigate to appropriate screen based on user role
            final String destinationScene = user.getRole() == Role.ADMIN ? 
                                           SceneManager.ADMIN_SCENE : 
                                           SceneManager.MENU_SCENE;
            
            // Navigate after short delay
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    javafx.application.Platform.runLater(() -> {
                        sceneManager.navigateTo(destinationScene);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showMessage("Invalid username or password. Please try again.", "error");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
    
    /**
     * Handles the register button action.
     * Opens a dialog for new user registration.
     */
    private void handleRegister() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Account");
        dialog.setHeaderText("Register a new player account");
        
        // Create registration form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField newUsername = new TextField();
        newUsername.setPromptText("Username");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm Password");
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(newUsername, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Confirm:"), 0, 2);
        grid.add(confirmPassword, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String username = newUsername.getText().trim();
                String password = newPassword.getText();
                String confirm = confirmPassword.getText();
                
                if (username.isEmpty() || password.isEmpty()) {
                    showMessage("Username and password cannot be empty", "error");
                } else if (!password.equals(confirm)) {
                    showMessage("Passwords do not match", "error");
                } else if (password.length() < 3) {
                    showMessage("Password must be at least 3 characters", "error");
                } else {
                    boolean success = userService.register(username, password, 
                                                          com.typinggame.model.Role.PLAYER);
                    if (success) {
                        showMessage("Account created successfully! You can now login.", "success");
                        usernameField.setText(username);
                        passwordField.requestFocus();
                    } else {
                        showMessage("Username already exists. Please choose another.", "error");
                    }
                }
            }
        });
    }
    
    /**
     * Displays a message to the user.
     * 
     * @param message Message text
     * @param type Message type ("success" or "error")
     */
    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        
        if (type.equals("success")) {
            messageLabel.setTextFill(Color.web("#4CAF50"));
        } else {
            messageLabel.setTextFill(Color.web("#F44336"));
        }
    }
    
    /**
     * Gets the login scene.
     * 
     * @return Login scene
     */
    public Scene getScene() {
        return loginScene;
    }
    
    /**
     * Resets the login form (called when returning to login screen).
     */
    public void reset() {
        usernameField.clear();
        passwordField.clear();
        messageLabel.setText("");
        usernameField.requestFocus();
    }
}
