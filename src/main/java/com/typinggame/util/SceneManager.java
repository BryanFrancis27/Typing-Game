package com.typinggame.util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

/**
 * Utility class for managing JavaFX scene navigation.
 * Implements a graph-based navigation system for screen transitions.
 */
public class SceneManager {
    
    /**
     * DATA STRUCTURE: Graph (Adjacency List using HashMap<String, List<String>>)
     * PURPOSE: Represents valid screen transitions in the application
     * JUSTIFICATION:
     * - Models the UI flow as a directed graph
     * - Nodes: Different screens (Login, Menu, Game, Results, Admin)
     * - Edges: Valid transitions between screens
     * - O(1) lookup to check if transition is valid
     * - Flexible for adding new screens and transitions
     * - Prevents invalid navigation paths
     * 
     * Example Graph Structure:
     *   Login -> Menu
     *   Menu -> Game, Admin, Login
     *   Game -> Results
     *   Results -> Menu, Game
     *   Admin -> Menu
     * 
     * Alternative considered: Simple switch statements would work but lack
     * the flexibility and validation that a graph structure provides.
     */
    private Map<String, List<String>> navigationGraph;
    
    /**
     * Stores actual Scene objects mapped to scene names
     */
    private Map<String, Scene> scenes;

    /**
     * Stores navigation data passed during scene transitions
     */
    private Map<String, Object> navigationData;

    /**
     * Navigation history stack for back button functionality
     */
    private Stack<String> navigationHistory;
    
    /**
     * Callback interface for scene navigation events
     */
    @FunctionalInterface
    public interface SceneNavigationListener {
        void onSceneChanged(String sceneName);
    }
    
    /**
     * Callback listener for scene changes
     */
    private SceneNavigationListener navigationListener;

    private Stage primaryStage;
    private String currentScene;
    
    // Scene name constants
    public static final String LOGIN_SCENE = "Login";
    public static final String MENU_SCENE = "Menu";
    public static final String GAME_SCENE = "Game";
    /**
     * Separate scene for Free Practice mode so that timed and practice
     * experiences can use dedicated controllers without interfering.
     */
    public static final String PRACTICE_SCENE = "PracticeGame";
    public static final String RESULTS_SCENE = "Results";
    public static final String ADMIN_SCENE = "Admin";
    public static final String HISTORY_SCENE = "History";
    
    /**
     * Constructor initializes the scene manager and navigation graph.
     * 
     * @param primaryStage The primary stage of the application
     */
    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.scenes = new HashMap<>();
        this.navigationHistory = new Stack<>();
        this.navigationGraph = new HashMap<>();
        this.navigationData = new HashMap<>();
        this.currentScene = null;

        initializeNavigationGraph();
    }
    
    /**
     * Initializes the navigation graph with valid screen transitions.
     * Defines which screens can navigate to which other screens.
     */
    private void initializeNavigationGraph() {
        // Login screen can navigate to Menu or Admin (based on user role)
        navigationGraph.put(LOGIN_SCENE, Arrays.asList(MENU_SCENE, ADMIN_SCENE));
        
        // Menu screen can navigate to Game (Timed), Practice, Admin, History, or back to Login
        navigationGraph.put(MENU_SCENE, Arrays.asList(
            GAME_SCENE, PRACTICE_SCENE, ADMIN_SCENE, HISTORY_SCENE, LOGIN_SCENE
        ));
        
        // Timed Game screen can navigate to Results or back to Menu
        navigationGraph.put(GAME_SCENE, Arrays.asList(RESULTS_SCENE, MENU_SCENE));

        // Practice screen can navigate to Results or back to Menu
        navigationGraph.put(PRACTICE_SCENE, Arrays.asList(RESULTS_SCENE, MENU_SCENE));
        
        // Results screen can navigate to Menu or start new Game
        navigationGraph.put(RESULTS_SCENE, Arrays.asList(MENU_SCENE, GAME_SCENE));
        
        // Admin screen can navigate back to Menu
        navigationGraph.put(ADMIN_SCENE, Arrays.asList(MENU_SCENE));
        
        // History screen can navigate back to Menu
        navigationGraph.put(HISTORY_SCENE, Arrays.asList(MENU_SCENE));
        
        System.out.println("Navigation graph initialized with " + 
                         navigationGraph.size() + " nodes");
    }
    
    /**
     * Registers a scene with the manager.
     * 
     * @param sceneName Name identifier for the scene
     * @param scene The JavaFX Scene object
     */
    public void registerScene(String sceneName, Scene scene) {
        scenes.put(sceneName, scene);
        System.out.println("Scene registered: " + sceneName);
    }
    
    /**
     * Navigates to a specified scene.
     * Validates the transition using the navigation graph.
     *
     * @param sceneName Name of the scene to navigate to
     * @return true if navigation successful, false if invalid transition
     */
    public boolean navigateTo(String sceneName) {
        return navigateTo(sceneName, null);
    }

    /**
     * Navigates to a specified scene with navigation data.
     * Validates the transition using the navigation graph.
     *
     * @param sceneName Name of the scene to navigate to
     * @param data Navigation data to pass to the scene
     * @return true if navigation successful, false if invalid transition
     */
    public boolean navigateTo(String sceneName, Object data) {
        // Check if scene exists
        if (!scenes.containsKey(sceneName)) {
            System.err.println("Navigation failed: Scene '" + sceneName + "' not registered");
            return false;
        }

        // Validate transition using graph (if current scene exists)
        if (currentScene != null && !isValidTransition(currentScene, sceneName)) {
            System.err.println("Navigation failed: Invalid transition from '" +
                             currentScene + "' to '" + sceneName + "'");
            return false;
        }

        // Set navigation data
        if (data != null) {
            navigationData.put(sceneName, data);
        }

        // Add current scene to history before navigating
        if (currentScene != null) {
            navigationHistory.push(currentScene);
        }

        // Perform navigation
        Scene targetScene = scenes.get(sceneName);
        primaryStage.setScene(targetScene);
        currentScene = sceneName;

        System.out.println("Navigated to: " + sceneName + (data != null ? " with data" : ""));
        
        // Trigger navigation listener callback
        if (navigationListener != null) {
            navigationListener.onSceneChanged(sceneName);
        }
        
        return true;
    }
    
    /**
     * Checks if a transition from one scene to another is valid.
     * Uses the navigation graph to validate transitions.
     * 
     * @param fromScene Source scene
     * @param toScene Destination scene
     * @return true if transition is valid, false otherwise
     */
    public boolean isValidTransition(String fromScene, String toScene) {
        List<String> validDestinations = navigationGraph.get(fromScene);
        
        if (validDestinations == null) {
            return false;
        }
        
        return validDestinations.contains(toScene);
    }
    
    /**
     * Navigates back to the previous scene using navigation history.
     * 
     * @return true if navigation successful, false if no history
     */
    public boolean navigateBack() {
        if (navigationHistory.isEmpty()) {
            System.out.println("Cannot navigate back: No history");
            return false;
        }
        
        String previousScene = navigationHistory.pop();
        
        if (!scenes.containsKey(previousScene)) {
            System.err.println("Navigation back failed: Scene not found");
            return false;
        }
        
        Scene targetScene = scenes.get(previousScene);
        primaryStage.setScene(targetScene);
        currentScene = previousScene;
        
        System.out.println("Navigated back to: " + previousScene);
        return true;
    }
    
    /**
     * Gets the current scene name.
     * 
     * @return Current scene name
     */
    public String getCurrentScene() {
        return currentScene;
    }
    
    /**
     * Sets a listener to be called when scene navigation occurs.
     * 
     * @param listener The listener to call on scene changes
     */
    public void setNavigationListener(SceneNavigationListener listener) {
        this.navigationListener = listener;
    }
    
    /**
     * Gets all valid destinations from the current scene.
     * 
     * @return List of valid destination scene names
     */
    public List<String> getValidDestinations() {
        if (currentScene == null) {
            return new ArrayList<>();
        }
        
        return navigationGraph.getOrDefault(currentScene, new ArrayList<>());
    }
    
    /**
     * Checks if navigation back is possible.
     * 
     * @return true if history exists, false otherwise
     */
    public boolean canNavigateBack() {
        return !navigationHistory.isEmpty();
    }
    
    /**
     * Clears the navigation history.
     */
    public void clearHistory() {
        navigationHistory.clear();
        System.out.println("Navigation history cleared");
    }
    
    /**
     * Gets the navigation history size.
     * 
     * @return Number of scenes in history
     */
    public int getHistorySize() {
        return navigationHistory.size();
    }
    
    /**
     * Adds a new transition to the navigation graph.
     * Allows dynamic modification of valid transitions.
     * 
     * @param fromScene Source scene
     * @param toScene Destination scene
     */
    public void addTransition(String fromScene, String toScene) {
        navigationGraph.computeIfAbsent(fromScene, k -> new ArrayList<>()).add(toScene);
        System.out.println("Transition added: " + fromScene + " -> " + toScene);
    }
    
    /**
     * Removes a transition from the navigation graph.
     * 
     * @param fromScene Source scene
     * @param toScene Destination scene
     */
    public void removeTransition(String fromScene, String toScene) {
        List<String> destinations = navigationGraph.get(fromScene);
        if (destinations != null) {
            destinations.remove(toScene);
            System.out.println("Transition removed: " + fromScene + " -> " + toScene);
        }
    }
    
    /**
     * Gets a visual representation of the navigation graph.
     * 
     * @return String representation of the graph
     */
    public String getNavigationGraphString() {
        StringBuilder graph = new StringBuilder();
        graph.append("=== Navigation Graph ===\n");
        
        for (Map.Entry<String, List<String>> entry : navigationGraph.entrySet()) {
            graph.append(entry.getKey()).append(" -> ");
            graph.append(String.join(", ", entry.getValue()));
            graph.append("\n");
        }
        
        return graph.toString();
    }
    
    /**
     * Validates the entire navigation graph for consistency.
     * Checks if all referenced scenes are registered.
     * 
     * @return true if graph is valid, false otherwise
     */
    public boolean validateGraph() {
        for (Map.Entry<String, List<String>> entry : navigationGraph.entrySet()) {
            String source = entry.getKey();
            
            if (!scenes.containsKey(source)) {
                System.err.println("Graph validation error: Source scene '" + 
                                 source + "' not registered");
                return false;
            }
            
            for (String destination : entry.getValue()) {
                if (!scenes.containsKey(destination)) {
                    System.err.println("Graph validation error: Destination scene '" + 
                                     destination + "' not registered");
                    return false;
                }
            }
        }
        
        System.out.println("Navigation graph validated successfully");
        return true;
    }
    
    /**
     * Gets navigation data for a specific scene.
     *
     * @param sceneName Name of the scene
     * @return Navigation data or null if none exists
     */
    public Object getNavigationData(String sceneName) {
        return navigationData.get(sceneName);
    }

    /**
     * Gets navigation data for the current scene.
     *
     * @return Navigation data or null if none exists
     */
    public Object getCurrentNavigationData() {
        return getNavigationData(currentScene);
    }

    /**
     * Clears navigation data for a specific scene.
     *
     * @param sceneName Name of the scene
     */
    public void clearNavigationData(String sceneName) {
        navigationData.remove(sceneName);
    }

    /**
     * Clears all navigation data.
     */
    public void clearAllNavigationData() {
        navigationData.clear();
    }

    /**
     * Gets the primary stage.
     *
     * @return Primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
