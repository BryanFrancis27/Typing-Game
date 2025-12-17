package com.typinggame.model;

/**
 * Enum representing user roles in the typing game application.
 * Used to differentiate between administrative and regular player accounts.
 */
public enum Role {
    /**
     * Administrator role with access to:
     * - View all player performance reports
     * - Manage typing content
     * - System administration features
     */
    ADMIN,
    
    /**
     * Regular player role with access to:
     * - Play typing games
     * - View personal performance history
     * - Access game modes
     */
    PLAYER
}
