package com.typinggame.service;

import java.util.Stack;

/**
 * Service class for tracking typing performance in real-time.
 * Monitors WPM, accuracy, errors, and provides live statistics updates.
 */
public class PerformanceTracker {
    
    /**
     * DATA STRUCTURE: Stack<Character>
     * PURPOSE: Tracks recent typing errors for undo/backspace functionality
     * JUSTIFICATION:
     * - LIFO (Last-In-First-Out) behavior matches backspace operation
     * - O(1) time complexity for push (recording error) and pop (undo) operations
     * - Natural fit for tracking most recent errors
     * - Supports error correction workflow
     * - Enables error history analysis
     * 
     * Alternative considered: ArrayList could store errors, but Stack's LIFO
     * behavior better represents the backspace/undo pattern in typing.
     */
    private Stack<Character> errorStack;
    
    private int totalCharactersTyped;
    private int correctCharacters;
    private int errorCount;
    private long startTime;
    private long endTime;
    private boolean isTracking;
    
    // Real-time metrics
    private double currentWPM;
    private double currentAccuracy;
    
    /**
     * Constructor initializes the performance tracker.
     */
    public PerformanceTracker() {
        this.errorStack = new Stack<>();
        reset();
    }
    
    /**
     * Starts tracking performance for a new session.
     */
    public void startTracking() {
        reset();
        startTime = System.currentTimeMillis();
        isTracking = true;
        System.out.println("Performance tracking started");
    }
    
    /**
     * Stops tracking and finalizes the session.
     */
    public void stopTracking() {
        if (isTracking) {
            endTime = System.currentTimeMillis();
            isTracking = false;
            updateMetrics();
            System.out.println("Performance tracking stopped");
        }
    }
    
    /**
     * Records a correctly typed character.
     * 
     * @param character The character that was typed correctly
     */
    public void recordCorrectCharacter(char character) {
        if (!isTracking) return;
        
        totalCharactersTyped++;
        correctCharacters++;
        updateMetrics();
    }
    
    /**
     * Records a typing error and pushes it onto the error stack.
     * Demonstrates Stack's push operation for error tracking.
     * 
     * @param expectedChar The character that should have been typed
     * @param typedChar The character that was actually typed
     */
    public void recordError(char expectedChar, char typedChar) {
        if (!isTracking) return;
        
        totalCharactersTyped++;
        errorCount++;
        
        // Push error onto stack (O(1) operation)
        errorStack.push(expectedChar);
        
        updateMetrics();
        
        System.out.println("Error recorded: expected '" + expectedChar + 
                         "', typed '" + typedChar + "' (Total errors: " + errorCount + ")");
    }
    
    /**
     * Handles backspace/undo operation by popping from error stack.
     * Demonstrates Stack's pop operation for error correction.
     * 
     * @return The character that was removed, or null if stack is empty
     */
    public Character undoLastError() {
        if (!errorStack.isEmpty()) {
            Character undoneChar = errorStack.pop();  // O(1) pop operation
            
            if (errorCount > 0) {
                errorCount--;
            }
            if (totalCharactersTyped > 0) {
                totalCharactersTyped--;
            }
            
            updateMetrics();
            System.out.println("Error undone: '" + undoneChar + "'");
            return undoneChar;
        }
        return null;
    }
    
    /**
     * Peeks at the most recent error without removing it.
     * 
     * @return The most recent error character, or null if no errors
     */
    public Character peekLastError() {
        if (!errorStack.isEmpty()) {
            return errorStack.peek();  // O(1) peek operation
        }
        return null;
    }
    
    /**
     * Gets the number of errors currently in the stack.
     * 
     * @return Error stack size
     */
    public int getErrorStackSize() {
        return errorStack.size();
    }
    
    /**
     * Checks if there are any errors in the stack.
     * 
     * @return true if errors exist, false otherwise
     */
    public boolean hasErrors() {
        return !errorStack.isEmpty();
    }
    
    /**
     * Updates real-time performance metrics (WPM and accuracy).
     * Called after each character is typed.
     */
    private void updateMetrics() {
        // Calculate elapsed time in minutes
        long currentTime = isTracking ? System.currentTimeMillis() : endTime;
        double elapsedMinutes = (currentTime - startTime) / 60000.0;
        
        // Prevent division by zero
        if (elapsedMinutes < 0.01) {
            elapsedMinutes = 0.01;
        }
        
        // Calculate WPM (5 characters = 1 word)
        double words = totalCharactersTyped / 5.0;
        currentWPM = words / elapsedMinutes;
        
        // Calculate accuracy
        if (totalCharactersTyped > 0) {
            currentAccuracy = (correctCharacters * 100.0) / totalCharactersTyped;
        } else {
            currentAccuracy = 100.0;
        }
    }
    
    /**
     * Gets the current Words Per Minute.
     * 
     * @return Current WPM
     */
    public double getCurrentWPM() {
        return Math.round(currentWPM * 10.0) / 10.0;
    }
    
    /**
     * Gets the current accuracy percentage.
     * 
     * @return Current accuracy (0-100)
     */
    public double getCurrentAccuracy() {
        return Math.round(currentAccuracy * 10.0) / 10.0;
    }
    
    /**
     * Gets the total number of errors (including undone errors).
     * 
     * @return Total error count
     */
    public int getErrorCount() {
        return errorCount;
    }
    
    /**
     * Gets the total characters typed.
     * 
     * @return Total character count
     */
    public int getTotalCharactersTyped() {
        return totalCharactersTyped;
    }
    
    /**
     * Gets the number of correctly typed characters.
     * 
     * @return Correct character count
     */
    public int getCorrectCharacters() {
        return correctCharacters;
    }
    
    /**
     * Gets the elapsed time in seconds.
     * 
     * @return Elapsed time in seconds
     */
    public int getElapsedSeconds() {
        long currentTime = isTracking ? System.currentTimeMillis() : endTime;
        return (int) ((currentTime - startTime) / 1000);
    }
    
    /**
     * Gets the error rate (errors per minute).
     * 
     * @return Errors per minute
     */
    public double getErrorRate() {
        double elapsedMinutes = getElapsedSeconds() / 60.0;
        if (elapsedMinutes < 0.01) {
            return 0.0;
        }
        return Math.round((errorCount / elapsedMinutes) * 10.0) / 10.0;
    }
    
    /**
     * Checks if tracking is currently active.
     * 
     * @return true if tracking, false otherwise
     */
    public boolean isTracking() {
        return isTracking;
    }
    
    /**
     * Resets all tracking metrics for a new session.
     */
    public void reset() {
        errorStack.clear();
        totalCharactersTyped = 0;
        correctCharacters = 0;
        errorCount = 0;
        startTime = 0;
        endTime = 0;
        isTracking = false;
        currentWPM = 0.0;
        currentAccuracy = 100.0;
    }
    
    /**
     * Gets a formatted string of current statistics.
     * 
     * @return Statistics string
     */
    public String getStatsString() {
        return String.format("WPM: %.1f | Accuracy: %.1f%% | Errors: %d | Time: %ds",
                getCurrentWPM(), getCurrentAccuracy(), errorCount, getElapsedSeconds());
    }
    
    /**
     * Gets a detailed performance report.
     * 
     * @return Detailed report string
     */
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Performance Report ===\n");
        report.append(String.format("WPM: %.1f\n", getCurrentWPM()));
        report.append(String.format("Accuracy: %.1f%%\n", getCurrentAccuracy()));
        report.append(String.format("Total Characters: %d\n", totalCharactersTyped));
        report.append(String.format("Correct Characters: %d\n", correctCharacters));
        report.append(String.format("Errors: %d\n", errorCount));
        report.append(String.format("Error Rate: %.1f errors/min\n", getErrorRate()));
        report.append(String.format("Time: %d seconds\n", getElapsedSeconds()));
        report.append(String.format("Errors in Stack: %d\n", errorStack.size()));
        return report.toString();
    }
}
