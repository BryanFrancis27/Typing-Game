package com.typinggame.service;

import com.typinggame.model.Score;
import com.typinggame.model.GameSession;
import com.typinggame.model.GameMode;
import java.util.*;

/**
 * Service class for managing scores and leaderboards.
 * Handles score storage, ranking, and retrieval operations.
 */
public class ScoreManager {
    
    /**
     * DATA STRUCTURE: TreeMap<Integer, List<Score>>
     * PURPOSE: Automatically maintains scores sorted by WPM for efficient ranking
     * JUSTIFICATION:
     * - Automatic sorting by key (WPM) in descending order
     * - O(log n) insertion and retrieval operations
     * - Efficient range queries for top scores
     * - Red-Black tree implementation ensures balanced structure
     * - Natural ordering for leaderboard display
     * 
     * Key: WPM (rounded to integer) - stored in reverse order for descending sort
     * Value: List of scores with that WPM (handles ties)
     * 
     * Alternative considered: HashMap would provide O(1) operations but no ordering,
     * requiring separate sorting. TreeMap's automatic sorting is more efficient
     * for leaderboard scenarios.
     */
    private TreeMap<Integer, List<Score>> scoresByWPM;
    
    /**
     * Secondary storage: All scores by username for quick user lookup
     * HashMap provides O(1) access to a user's score history
     */
    private Map<String, List<Score>> scoresByUser;
    
    /**
     * All scores in chronological order
     */
    private List<Score> allScores;
    
    /**
     * Constructor initializes the score manager.
     * TreeMap uses reverseOrder() comparator for descending WPM sort.
     */
    public ScoreManager() {
        // TreeMap with reverse order for descending WPM ranking
        this.scoresByWPM = new TreeMap<>(Collections.reverseOrder());
        this.scoresByUser = new HashMap<>();
        this.allScores = new ArrayList<>();
    }
    
    /**
     * Adds a score to the manager.
     * Demonstrates TreeMap's automatic sorting capability.
     * 
     * @param score Score to add
     */
    public void addScore(Score score) {
        if (score == null) return;
        
        // Add to chronological list
        allScores.add(score);
        
        // Add to TreeMap (O(log n) insertion with automatic sorting)
        int wpmKey = (int) Math.round(score.getWpm());
        scoresByWPM.computeIfAbsent(wpmKey, k -> new ArrayList<>()).add(score);
        
        // Add to user's score history (O(1) HashMap operation)
        scoresByUser.computeIfAbsent(score.getUsername(), k -> new ArrayList<>()).add(score);
        
        System.out.println("Score added: " + score.getUsername() + 
                         " - " + score.getWpm() + " WPM");
    }
    
    /**
     * Adds a score from a completed game session.
     * 
     * @param session Game session to convert to score
     */
    public void addScoreFromSession(GameSession session) {
        Score score = Score.fromGameSession(session);
        addScore(score);
    }
    
    /**
     * Gets the top N scores across all users.
     * Leverages TreeMap's sorted order for efficient retrieval.
     * 
     * @param limit Maximum number of scores to return
     * @return List of top scores
     */
    public List<Score> getTopScores(int limit) {
        List<Score> topScores = new ArrayList<>();
        int rank = 1;
        
        // TreeMap iteration is in sorted order (descending WPM)
        for (Map.Entry<Integer, List<Score>> entry : scoresByWPM.entrySet()) {
            List<Score> scoresAtWPM = entry.getValue();
            
            // Sort scores at same WPM by accuracy
            scoresAtWPM.sort(Comparator.comparingDouble(Score::getAccuracy).reversed());
            
            for (Score score : scoresAtWPM) {
                if (topScores.size() >= limit) {
                    return topScores;
                }
                
                score.setRank(rank++);
                topScores.add(score);
            }
        }
        
        return topScores;
    }
    
    /**
     * Gets all scores for a specific user.
     * 
     * @param username Username to search for
     * @return List of user's scores, sorted by date (most recent first)
     */
    public List<Score> getUserScores(String username) {
        List<Score> userScores = scoresByUser.getOrDefault(username, new ArrayList<>());
        
        // Sort by date (most recent first)
        userScores.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));
        
        return new ArrayList<>(userScores);
    }
    
    /**
     * Gets the user's best (highest WPM) score.
     * 
     * @param username Username to search for
     * @return Best score, or null if user has no scores
     */
    public Score getUserBestScore(String username) {
        List<Score> userScores = scoresByUser.get(username);
        
        if (userScores == null || userScores.isEmpty()) {
            return null;
        }
        
        return userScores.stream()
                .max(Comparator.comparingDouble(Score::getWpm))
                .orElse(null);
    }
    
    /**
     * Gets the user's rank based on their best score.
     * 
     * @param username Username to check
     * @return User's rank (1-based), or -1 if user has no scores
     */
    public int getUserRank(String username) {
        Score bestScore = getUserBestScore(username);
        
        if (bestScore == null) {
            return -1;
        }
        
        int rank = 1;
        
        // Iterate through TreeMap in sorted order
        for (Map.Entry<Integer, List<Score>> entry : scoresByWPM.entrySet()) {
            for (Score score : entry.getValue()) {
                if (score.getUsername().equals(username) && 
                    score.getWpm() == bestScore.getWpm()) {
                    return rank;
                }
                rank++;
            }
        }
        
        return -1;
    }
    
    /**
     * Gets top scores for a specific game mode.
     * 
     * @param mode Game mode to filter by
     * @param limit Maximum number of scores
     * @return List of top scores for that mode
     */
    public List<Score> getTopScoresByMode(GameMode mode, int limit) {
        List<Score> modeScores = new ArrayList<>();
        
        for (Score score : allScores) {
            if (score.getMode() == mode) {
                modeScores.add(score);
            }
        }
        
        // Sort by WPM descending, then accuracy descending
        modeScores.sort((s1, s2) -> {
            int wpmCompare = Double.compare(s2.getWpm(), s1.getWpm());
            if (wpmCompare != 0) return wpmCompare;
            return Double.compare(s2.getAccuracy(), s1.getAccuracy());
        });
        
        // Assign ranks and limit results
        int rank = 1;
        List<Score> result = new ArrayList<>();
        for (Score score : modeScores) {
            if (result.size() >= limit) break;
            score.setRank(rank++);
            result.add(score);
        }
        
        return result;
    }
    
    /**
     * Gets scores within a WPM range.
     * Demonstrates TreeMap's efficient range query capability.
     * 
     * @param minWPM Minimum WPM (inclusive)
     * @param maxWPM Maximum WPM (inclusive)
     * @return List of scores in range
     */
    public List<Score> getScoresInRange(int minWPM, int maxWPM) {
        List<Score> rangeScores = new ArrayList<>();
        
        // TreeMap's subMap provides efficient range queries
        SortedMap<Integer, List<Score>> subMap = scoresByWPM.subMap(maxWPM, true, minWPM, true);
        
        for (List<Score> scores : subMap.values()) {
            rangeScores.addAll(scores);
        }
        
        return rangeScores;
    }
    
    /**
     * Gets the total number of scores stored.
     * 
     * @return Total score count
     */
    public int getTotalScoreCount() {
        return allScores.size();
    }
    
    /**
     * Gets the number of unique players with scores.
     * 
     * @return Unique player count
     */
    public int getUniquePlayerCount() {
        return scoresByUser.size();
    }
    
    /**
     * Gets the highest WPM ever recorded.
     * 
     * @return Highest WPM, or 0 if no scores
     */
    public double getHighestWPM() {
        if (scoresByWPM.isEmpty()) {
            return 0.0;
        }
        
        // First key in reverse-ordered TreeMap is the highest
        Integer highestKey = scoresByWPM.firstKey();
        List<Score> topScores = scoresByWPM.get(highestKey);
        
        return topScores.stream()
                .mapToDouble(Score::getWpm)
                .max()
                .orElse(0.0);
    }
    
    /**
     * Gets the average WPM across all scores.
     * 
     * @return Average WPM
     */
    public double getAverageWPM() {
        if (allScores.isEmpty()) {
            return 0.0;
        }
        
        double totalWPM = allScores.stream()
                .mapToDouble(Score::getWpm)
                .sum();
        
        return Math.round((totalWPM / allScores.size()) * 10.0) / 10.0;
    }
    
    /**
     * Clears all scores (admin function).
     */
    public void clearAllScores() {
        scoresByWPM.clear();
        scoresByUser.clear();
        allScores.clear();
        System.out.println("All scores cleared");
    }
    
    /**
     * Clears scores for a specific user (admin function).
     * 
     * @param username Username whose scores to clear
     */
    public void clearUserScores(String username) {
        List<Score> userScores = scoresByUser.remove(username);
        
        if (userScores != null) {
            // Remove from TreeMap
            for (Score score : userScores) {
                int wpmKey = (int) Math.round(score.getWpm());
                List<Score> scoresAtWPM = scoresByWPM.get(wpmKey);
                if (scoresAtWPM != null) {
                    scoresAtWPM.remove(score);
                    if (scoresAtWPM.isEmpty()) {
                        scoresByWPM.remove(wpmKey);
                    }
                }
            }
            
            // Remove from all scores
            allScores.removeAll(userScores);
            
            System.out.println("Cleared " + userScores.size() + " scores for " + username);
        }
    }
    
    /**
     * Gets statistics about the score database.
     * 
     * @return Statistics string
     */
    public String getStatistics() {
        return String.format(
            "Total Scores: %d | Unique Players: %d | Highest WPM: %.1f | Average WPM: %.1f",
            getTotalScoreCount(), getUniquePlayerCount(), getHighestWPM(), getAverageWPM()
        );
    }
    
    /**
     * Generates a leaderboard string for display.
     * 
     * @param limit Number of top scores to include
     * @return Formatted leaderboard string
     */
    public String generateLeaderboard(int limit) {
        StringBuilder leaderboard = new StringBuilder();
        leaderboard.append("=== LEADERBOARD (Top ").append(limit).append(") ===\n");
        leaderboard.append(String.format("%-5s %-15s %-8s %-10s %-15s\n", 
                "Rank", "Player", "WPM", "Accuracy", "Mode"));
        leaderboard.append("-".repeat(60)).append("\n");
        
        List<Score> topScores = getTopScores(limit);
        
        for (Score score : topScores) {
            leaderboard.append(String.format("%-5d %-15s %-8.1f %-10.1f%% %-15s\n",
                    score.getRank(),
                    score.getUsername(),
                    score.getWpm(),
                    score.getAccuracy(),
                    score.getMode().getDisplayName()));
        }
        
        return leaderboard.toString();
    }
}
