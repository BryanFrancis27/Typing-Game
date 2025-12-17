package com.typinggame.service;

import com.typinggame.model.Role;
import com.typinggame.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Service class for managing user accounts and authentication.
 * Handles user registration, login, and user data retrieval.
 */
public class UserService {
    
    /**
     * DATA STRUCTURE: HashMap<String, User>
     * PURPOSE: Stores user accounts with username as key for fast authentication
     * JUSTIFICATION:
     * - O(1) average time complexity for login lookup by username
     * - Ensures unique usernames (key uniqueness)
     * - Efficient for frequent authentication operations
     * - No ordering required for user storage
     * 
     * Alternative considered: TreeMap would provide O(log n) lookup with sorting,
     * but sorting users alphabetically is not a requirement, and HashMap's O(1)
     * lookup is more efficient for authentication.
     */
    private Map<String, User> users;
    
    private User currentUser;  // Currently logged-in user
    
    /**
     * Constructor initializes the user storage and creates default accounts.
     */
    public UserService() {
        this.users = new HashMap<>();
        this.currentUser = null;
        initializeDefaultUsers();
    }
    
    /**
     * Initializes default user accounts for testing and demonstration.
     * Creates one admin account and two player accounts.
     */
    private void initializeDefaultUsers() {
        // Create default admin account
        User admin = new User("admin", "admin123", Role.ADMIN);
        users.put(admin.getUsername(), admin);
        
        // Create default player accounts
        User player1 = new User("player1", "pass123", Role.PLAYER);
        users.put(player1.getUsername(), player1);
        
        User player2 = new User("player2", "pass123", Role.PLAYER);
        users.put(player2.getUsername(), player2);
        
        System.out.println("Default users initialized:");
        System.out.println("  Admin: username='admin', password='admin123'");
        System.out.println("  Player: username='player1', password='pass123'");
        System.out.println("  Player: username='player2', password='pass123'");
    }
    
    /**
     * Authenticates a user with username and password.
     * Uses HashMap's O(1) lookup for efficient authentication.
     * 
     * @param username Username to authenticate
     * @param password Password to verify
     * @return User object if authentication successful, null otherwise
     */
    public User login(String username, String password) {
        // O(1) lookup in HashMap
        User user = users.get(username);
        
        if (user != null && user.authenticate(password)) {
            currentUser = user;
            System.out.println("Login successful: " + username + " (" + user.getRole() + ")");
            return user;
        }
        
        System.out.println("Login failed: Invalid credentials");
        return null;
    }
    
    /**
     * Registers a new user account.
     * 
     * @param username Desired username
     * @param password User's password
     * @param role User's role (ADMIN or PLAYER)
     * @return true if registration successful, false if username already exists
     */
    public boolean register(String username, String password, Role role) {
        // Check if username already exists (O(1) operation)
        if (users.containsKey(username)) {
            System.out.println("Registration failed: Username already exists");
            return false;
        }
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Registration failed: Username cannot be empty");
            return false;
        }
        
        if (password == null || password.length() < 3) {
            System.out.println("Registration failed: Password must be at least 3 characters");
            return false;
        }
        
        // Create and store new user (O(1) operation)
        User newUser = new User(username, password, role);
        users.put(username, newUser);
        
        System.out.println("Registration successful: " + username);
        return true;
    }
    
    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Logout: " + currentUser.getUsername());
            currentUser = null;
        }
    }
    
    /**
     * Retrieves a user by username.
     * 
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        return users.get(username);  // O(1) lookup
    }
    
    /**
     * Gets the currently logged-in user.
     * 
     * @return Current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Checks if the current user is an admin.
     * 
     * @return true if current user is admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Gets all registered users.
     * Used by admin to view all player accounts.
     * 
     * @return Collection of all users
     */
    public Collection<User> getAllUsers() {
        return users.values();
    }
    
    /**
     * Gets the total number of registered users.
     * 
     * @return User count
     */
    public int getUserCount() {
        return users.size();
    }
    
    /**
     * Deletes a user account (admin function).
     * 
     * @param username Username to delete
     * @return true if deletion successful, false if user not found
     */
    public boolean deleteUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            System.out.println("User deleted: " + username);
            return true;
        }
        return false;
    }
    
    /**
     * Changes a user's password.
     * 
     * @param username Username
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = users.get(username);
        
        if (user != null && user.authenticate(oldPassword)) {
            user.setPassword(newPassword);
            System.out.println("Password changed for: " + username);
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets statistics about the user base.
     * 
     * @return String with user statistics
     */
    public String getUserStats() {
        int adminCount = 0;
        int playerCount = 0;
        
        for (User user : users.values()) {
            if (user.isAdmin()) {
                adminCount++;
            } else {
                playerCount++;
            }
        }
        
        return String.format("Total Users: %d (Admins: %d, Players: %d)",
                users.size(), adminCount, playerCount);
    }
}
