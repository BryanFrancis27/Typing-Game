package com.typinggame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user account in the typing game application.
 * Stores user credentials, role information, and game session history.
 */
public class User {
    private String username;
    private String password;
    private Role role;
    
    /**
     * DATA STRUCTURE: ArrayList<GameSession>
     * PURPOSE: Stores the user's game session history in chronological order
     * JUSTIFICATION: 
     * - Dynamic sizing as users play more games
     * - Indexed access for retrieving specific sessions
     * - Maintains insertion order (chronological)
     * - Efficient for iteration when displaying history
     */
    private List<GameSession> sessionHistory;
    
    /**
     * Constructor for creating a new user account.
     * 
     * @param username Unique username for the account
     * @param password User's password (in production, should be hashed)
     * @param role User's role (ADMIN or PLAYER)
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.sessionHistory = new ArrayList<>();
    }
    
    /**
     * Validates user credentials during login.
     * 
     * @param password Password to verify
     * @return true if password matches, false otherwise
     */
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
    
    /**
     * Adds a completed game session to the user's history.
     * 
     * @param session The completed game session
     */
    public void addSession(GameSession session) {
        sessionHistory.add(session);
    }
    
    /**
     * Retrieves all game sessions for this user.
     *
     * @return List of game sessions in chronological order
     */
    public List<GameSession> getSessionHistory() {
        return new ArrayList<>(sessionHistory); // Return copy for encapsulation
    }

    /**
     * Clears all game session history for this user.
     */
    public void clearSessionHistory() {
        sessionHistory.clear();
    }
    
    /**
     * Calculates the user's average WPM across all sessions.
     * 
     * @return Average WPM, or 0 if no sessions exist
     */
    public double getAverageWPM() {
        if (sessionHistory.isEmpty()) {
            return 0.0;
        }
        double totalWPM = 0;
        for (GameSession session : sessionHistory) {
            totalWPM += session.getWpm();
        }
        return totalWPM / sessionHistory.size();
    }
    
    /**
     * Calculates the user's average accuracy across all sessions.
     * 
     * @return Average accuracy percentage, or 0 if no sessions exist
     */
    public double getAverageAccuracy() {
        if (sessionHistory.isEmpty()) {
            return 0.0;
        }
        double totalAccuracy = 0;
        for (GameSession session : sessionHistory) {
            totalAccuracy += session.getAccuracy();
        }
        return totalAccuracy / sessionHistory.size();
    }
    
    /**
     * Gets the user's best (highest) WPM score.
     * 
     * @return Best WPM, or 0 if no sessions exist
     */
    public double getBestWPM() {
        if (sessionHistory.isEmpty()) {
            return 0.0;
        }
        double bestWPM = 0;
        for (GameSession session : sessionHistory) {
            if (session.getWpm() > bestWPM) {
                bestWPM = session.getWpm();
            }
        }
        return bestWPM;
    }
    
    /**
     * Gets the total number of games played by this user.
     * 
     * @return Total game count
     */
    public int getTotalGamesPlayed() {
        return sessionHistory.size();
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    public boolean isPlayer() {
        return role == Role.PLAYER;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role=" + role +
                ", totalGames=" + sessionHistory.size() +
                '}';
    }
}
