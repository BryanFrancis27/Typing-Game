package com.typinggame.model;

/**
 * Enum representing different game modes available in the typing game.
 * Each mode offers a unique typing challenge experience.
 */
public enum GameMode {
    /**
     * Timed Mode: Players type as much as possible within a fixed time limit.
     * Focuses on speed and accuracy under time pressure.
     * Default duration: 60 seconds
     */
    TIMED("Timed Mode", 60),
    
    /**
     * Free Practice Mode: Unlimited practice without time constraints.
     * Allows players to focus on accuracy and technique improvement.
     * No time limit
     */
    FREE_PRACTICE("Free Practice", -1);
    
    private final String displayName;
    private final int defaultDuration; // in seconds, -1 for unlimited
    
    GameMode(String displayName, int defaultDuration) {
        this.displayName = displayName;
        this.defaultDuration = defaultDuration;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getDefaultDuration() {
        return defaultDuration;
    }
    
    public boolean isTimed() {
        return defaultDuration > 0;
    }
}
