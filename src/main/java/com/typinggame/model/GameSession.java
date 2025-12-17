package com.typinggame.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single typing game session with performance metrics.
 * Stores all relevant data about a completed game including timing, accuracy, and errors.
 */
public class GameSession {
    private LocalDateTime timestamp;
    private GameMode mode;
    private double wpm;              // Words Per Minute
    private double accuracy;         // Accuracy percentage (0-100)
    private int errorCount;          // Total number of typing errors
    private int duration;            // Duration in seconds
    private int totalCharacters;     // Total characters typed
    private int correctCharacters;   // Correctly typed characters
    private String username;         // User who played this session
    
    /**
     * Constructor for creating a new game session.
     * 
     * @param username User who played the session
     * @param mode Game mode played
     * @param duration Duration of the session in seconds
     * @param totalCharacters Total characters typed
     * @param correctCharacters Correctly typed characters
     * @param errorCount Number of typing errors
     */
    public GameSession(String username, GameMode mode, int duration, 
                      int totalCharacters, int correctCharacters, int errorCount) {
        this.timestamp = LocalDateTime.now();
        this.username = username;
        this.mode = mode;
        this.duration = duration;
        this.totalCharacters = totalCharacters;
        this.correctCharacters = correctCharacters;
        this.errorCount = errorCount;
        this.wpm = calculateWPM();
        this.accuracy = calculateAccuracy();
    }
    
    /**
     * Calculates Words Per Minute (WPM) based on characters typed.
     * Formula: (Total Characters / 5) / (Time in Minutes)
     * Standard: 5 characters = 1 word
     * 
     * @return Calculated WPM
     */
    private double calculateWPM() {
        if (duration <= 0) {
            return 0.0;
        }
        double minutes = duration / 60.0;
        double words = totalCharacters / 5.0;
        return Math.round((words / minutes) * 10.0) / 10.0; // Round to 1 decimal
    }
    
    /**
     * Calculates typing accuracy as a percentage.
     * Formula: (Correct Characters / Total Characters) * 100
     * 
     * @return Accuracy percentage (0-100)
     */
    private double calculateAccuracy() {
        if (totalCharacters <= 0) {
            return 0.0;
        }
        double accuracyValue = ((double) correctCharacters / totalCharacters) * 100.0;
        return Math.round(accuracyValue * 10.0) / 10.0; // Round to 1 decimal
    }
    
    /**
     * Gets a formatted timestamp string for display.
     * 
     * @return Formatted date and time string
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    /**
     * Gets a short formatted date for display.
     * 
     * @return Formatted date string
     */
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return timestamp.format(formatter);
    }
    
    /**
     * Calculates the error rate (errors per minute).
     * 
     * @return Errors per minute
     */
    public double getErrorRate() {
        if (duration <= 0) {
            return 0.0;
        }
        double minutes = duration / 60.0;
        return Math.round((errorCount / minutes) * 10.0) / 10.0;
    }
    
    /**
     * Gets a performance grade based on WPM and accuracy.
     * 
     * @return Grade string (A+, A, B, C, D, F)
     */
    public String getPerformanceGrade() {
        double score = (wpm * 0.6) + (accuracy * 0.4);
        
        if (score >= 90 && accuracy >= 95) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }
    
    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public GameMode getMode() {
        return mode;
    }
    
    public void setMode(GameMode mode) {
        this.mode = mode;
    }
    
    public double getWpm() {
        return wpm;
    }
    
    public void setWpm(double wpm) {
        this.wpm = wpm;
    }
    
    public double getAccuracy() {
        return accuracy;
    }
    
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public int getTotalCharacters() {
        return totalCharacters;
    }
    
    public void setTotalCharacters(int totalCharacters) {
        this.totalCharacters = totalCharacters;
    }
    
    public int getCorrectCharacters() {
        return correctCharacters;
    }
    
    public void setCorrectCharacters(int correctCharacters) {
        this.correctCharacters = correctCharacters;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public String toString() {
        return "GameSession{" +
                "username='" + username + '\'' +
                ", mode=" + mode +
                ", wpm=" + wpm +
                ", accuracy=" + accuracy +
                "%, errors=" + errorCount +
                ", date=" + getFormattedDate() +
                '}';
    }
}
