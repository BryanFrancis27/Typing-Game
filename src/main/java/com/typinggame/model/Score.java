package com.typinggame.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a score entry for leaderboard and ranking purposes.
 * Implements Comparable to enable automatic sorting by WPM.
 */
public class Score implements Comparable<Score> {
    private String username;
    private double wpm;
    private double accuracy;
    private LocalDateTime date;
    private GameMode mode;
    private int rank;  // Position in leaderboard
    
    /**
     * Constructor for creating a score entry.
     * 
     * @param username Player's username
     * @param wpm Words per minute achieved
     * @param accuracy Accuracy percentage
     * @param date Date and time of the score
     * @param mode Game mode played
     */
    public Score(String username, double wpm, double accuracy, LocalDateTime date, GameMode mode) {
        this.username = username;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.date = date;
        this.mode = mode;
        this.rank = 0;
    }
    
    /**
     * Creates a Score from a GameSession.
     * 
     * @param session The game session to convert
     * @return Score object
     */
    public static Score fromGameSession(GameSession session) {
        return new Score(
            session.getUsername(),
            session.getWpm(),
            session.getAccuracy(),
            session.getTimestamp(),
            session.getMode()
        );
    }
    
    /**
     * Compares scores for sorting purposes.
     * Primary: WPM (descending - higher is better)
     * Secondary: Accuracy (descending - higher is better)
     * Tertiary: Date (descending - more recent is better)
     * 
     * @param other Score to compare with
     * @return Comparison result
     */
    @Override
    public int compareTo(Score other) {
        // Compare by WPM (descending)
        int wpmCompare = Double.compare(other.wpm, this.wpm);
        if (wpmCompare != 0) {
            return wpmCompare;
        }
        
        // If WPM is equal, compare by accuracy (descending)
        int accuracyCompare = Double.compare(other.accuracy, this.accuracy);
        if (accuracyCompare != 0) {
            return accuracyCompare;
        }
        
        // If accuracy is also equal, compare by date (more recent first)
        return other.date.compareTo(this.date);
    }
    
    /**
     * Gets formatted date string for display.
     * 
     * @return Formatted date
     */
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return date.format(formatter);
    }
    
    /**
     * Calculates a composite score for ranking.
     * Formula: (WPM * 0.7) + (Accuracy * 0.3)
     * 
     * @return Composite score
     */
    public double getCompositeScore() {
        return (wpm * 0.7) + (accuracy * 0.3);
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public GameMode getMode() {
        return mode;
    }
    
    public void setMode(GameMode mode) {
        this.mode = mode;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    @Override
    public String toString() {
        return String.format("#%d - %s: %.1f WPM, %.1f%% accuracy (%s)",
                rank > 0 ? rank : 0, username, wpm, accuracy, mode.getDisplayName());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return Double.compare(score.wpm, wpm) == 0 &&
               Double.compare(score.accuracy, accuracy) == 0 &&
               username.equals(score.username) &&
               date.equals(score.date);
    }
    
    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + Double.hashCode(wpm);
        result = 31 * result + Double.hashCode(accuracy);
        result = 31 * result + date.hashCode();
        return result;
    }
}
