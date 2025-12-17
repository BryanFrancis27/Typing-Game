package com.typinggame;

import com.typinggame.controller.*;
import com.typinggame.model.GameMode;
import com.typinggame.service.*;
import com.typinggame.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application class for the Typing Game.
 * Initializes all services, controllers, and manages the JavaFX application lifecycle.
 * 
 * This application demonstrates the use of various data structures:
 * - HashMap: User authentication (O(1) lookup)
 * - Queue: Word sequencing (FIFO delivery)
 * - Stack: Error tracking (LIFO undo)
 * - TreeMap: Score ranking (automatic sorting)
 * - ArrayList: Session history (dynamic storage)
 * - Graph: Scene navigation (transition validation)
 */
public class Main extends Application {
    
    // Services
    private UserService userService;
    private GameService gameService;
    private PerformanceTracker performanceTracker;
    private ScoreManager scoreManager;
    private SceneManager sceneManager;
    
    // Controllers
    private LoginController loginController;
    private MenuController menuController;
    // Timed game uses the original GameController
    private GameController gameController;
    // Free practice uses its own dedicated controller
    private PracticeController practiceController;
    private ResultsController resultsController;
    private AdminController adminController;
    private HistoryController historyController;
    
    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("TYPING GAME APPLICATION");
        System.out.println("=".repeat(60));
        System.out.println("Initializing JavaFX application...");
        launch(args);
    }
    
    /**
     * Initializes the JavaFX application.
     * Called before start() method.
     */
    @Override
    public void init() {
        System.out.println("\n[INIT] Initializing services and controllers...");
        
        // Initialize services
        initializeServices();
        
        System.out.println("[INIT] All services initialized successfully");
    }
    
    /**
     * Starts the JavaFX application.
     * Sets up the primary stage and displays the login screen.
     * 
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println("\n[START] Starting application...");
        
        // Configure primary stage
        primaryStage.setTitle("Typing Game - Improve Your Typing Skills");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("\n[EXIT] Application closing...");
            handleExit();
        });
        
        // Initialize scene manager
        sceneManager = new SceneManager(primaryStage);
        
        // Initialize controllers
        initializeControllers();
        
        // Register all scenes
        registerScenes();
        
        // Validate navigation graph
        sceneManager.validateGraph();
        
        // Show login screen
        sceneManager.navigateTo(SceneManager.LOGIN_SCENE);
        primaryStage.show();
        
        System.out.println("[START] Application started successfully");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("APPLICATION READY");
        System.out.println("=".repeat(60));
        printWelcomeMessage();
    }
    
    /**
     * Initializes all service layer components.
     */
    private void initializeServices() {
        System.out.println("  - Initializing UserService (HashMap for user storage)...");
        userService = new UserService();
        
        System.out.println("  - Initializing GameService (Queue for word management)...");
        gameService = new GameService();
        
        System.out.println("  - Initializing PerformanceTracker (Stack for error tracking)...");
        performanceTracker = new PerformanceTracker();
        
        System.out.println("  - Initializing ScoreManager (TreeMap for score ranking)...");
        scoreManager = new ScoreManager();
    }
    
    /**
     * Initializes all controller components.
     */
    private void initializeControllers() {
        System.out.println("\n[CONTROLLERS] Initializing controllers...");
        
        System.out.println("  - Creating LoginController...");
        loginController = new LoginController(userService, sceneManager);
        
        System.out.println("  - Creating MenuController...");
        menuController = new MenuController(userService, scoreManager, sceneManager);
        
        System.out.println("  - Creating GameController (Timed)...");
        gameController = new GameController(userService, gameService, performanceTracker,
                                           scoreManager, sceneManager);

        System.out.println("  - Creating PracticeController (Free Practice)...");
        practiceController = new PracticeController(userService, gameService, performanceTracker,
                                                   scoreManager, sceneManager);
        
        System.out.println("  - Creating ResultsController...");
        resultsController = new ResultsController(userService, scoreManager, 
                                                 performanceTracker, sceneManager);
        
        System.out.println("  - Creating AdminController...");
        adminController = new AdminController(userService, scoreManager, sceneManager);
        
        System.out.println("  - Creating HistoryController...");
        historyController = new HistoryController(userService, sceneManager);
        
        System.out.println("[CONTROLLERS] All controllers initialized");
    }
    
    /**
     * Registers all scenes with the scene manager.
     */
    private void registerScenes() {
        System.out.println("\n[SCENES] Registering scenes...");
        
        sceneManager.registerScene(SceneManager.LOGIN_SCENE, loginController.getScene());
        sceneManager.registerScene(SceneManager.MENU_SCENE, menuController.getScene());
        sceneManager.registerScene(SceneManager.GAME_SCENE, gameController.getScene());
        sceneManager.registerScene(SceneManager.PRACTICE_SCENE, practiceController.getScene());
        sceneManager.registerScene(SceneManager.RESULTS_SCENE, resultsController.getScene());
        sceneManager.registerScene(SceneManager.ADMIN_SCENE, adminController.getScene());
        sceneManager.registerScene(SceneManager.HISTORY_SCENE, historyController.getScene());
        
        System.out.println("[SCENES] All scenes registered");
        
        // Setup scene transition listeners
        setupSceneListeners();
    }
    
    /**
     * Sets up listeners for scene transitions to update controllers.
     */
    private void setupSceneListeners() {
        System.out.println("\n[LISTENERS] Setting up scene transition listeners...");
        
        // Use the SceneManager's navigation listener callback
        sceneManager.setNavigationListener(sceneName -> {
            System.out.println("[SCENE_CHANGE] " + sceneName);
            
            switch (sceneName) {
                case SceneManager.MENU_SCENE:
                    menuController.updateDisplay();
                    break;
                case SceneManager.GAME_SCENE:
                    // Timed game always uses TIMED mode
                    gameController.startGame(GameMode.TIMED);
                    break;
                case SceneManager.PRACTICE_SCENE:
                    // Free practice uses a dedicated controller
                    practiceController.startPractice();
                    break;
                case SceneManager.RESULTS_SCENE:
                    resultsController.updateResults();
                    break;
                case SceneManager.ADMIN_SCENE:
                    adminController.refreshData();
                    break;
                case SceneManager.HISTORY_SCENE:
                    historyController.updateHistory();
                    break;
                case SceneManager.LOGIN_SCENE:
                    loginController.reset();
                    break;
            }
        });
        
        System.out.println("[LISTENERS] Scene listeners configured");
    }
    
    /**
     * Handles application exit.
     * Performs cleanup and saves any necessary data.
     */
    private void handleExit() {
        System.out.println("  - Logging out current user...");
        if (userService.isLoggedIn()) {
            userService.logout();
        }
        
        System.out.println("  - Cleaning up resources...");
        // Additional cleanup if needed
        
        System.out.println("[EXIT] Cleanup complete");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Thank you for using Typing Game!");
        System.out.println("=".repeat(60));
    }
    
    /**
     * Prints welcome message with application information.
     */
    private void printWelcomeMessage() {
        System.out.println("\nTYPING GAME - FEATURES:");
        System.out.println("  ✓ User Authentication (Admin & Player roles)");
        System.out.println("  ✓ Multiple Game Modes (Timed & Free Practice)");
        System.out.println("  ✓ Real-time Performance Tracking (WPM, Accuracy, Errors)");
        System.out.println("  ✓ Score Management & Leaderboards");
        System.out.println("  ✓ Session History & Statistics");
        System.out.println("  ✓ Admin Panel for Player Management");
        
        System.out.println("\n🔧 DATA STRUCTURES IMPLEMENTED:");
        System.out.println("  • HashMap - User authentication (O(1) lookup)");
        System.out.println("  • Queue (LinkedList) - Word sequencing (FIFO)");
        System.out.println("  • Stack - Error tracking (LIFO)");
        System.out.println("  • TreeMap - Score ranking (auto-sorted)");
        System.out.println("  • ArrayList - Session history (dynamic)");
        System.out.println("  • Graph (Adjacency List) - Scene navigation");
        
        System.out.println("\n👤 DEFAULT ACCOUNTS:");
        System.out.println("  Admin:  username='admin'   password='admin123'");
        System.out.println("  Player: username='player1' password='pass123'");
        System.out.println("  Player: username='player2' password='pass123'");
        
        System.out.println("\n" + "=".repeat(60) + "\n");
    }
    
    /**
     * Called when the application is stopped.
     * Performs final cleanup.
     */
    @Override
    public void stop() {
        System.out.println("\n[STOP] Application stopped");
    }
}
