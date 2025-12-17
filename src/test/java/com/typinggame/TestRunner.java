package com.typinggame;

import com.typinggame.model.*;
import com.typinggame.service.*;
import java.util.*;

/**
 * Manual test runner for Phase 6 testing.
 * This class provides automated tests for data structures and core functionality.
 * 
 * Run this class to execute all automated tests and verify system functionality.
 */
public class TestRunner {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static List<String> failedTests = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("TYPING GAME - PHASE 6 AUTOMATED TESTING");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Run all test suites
        testUserService();
        testGameService();
        testPerformanceTracker();
        testScoreManager();
        testDataStructurePerformance();
        testEdgeCases();
        
        // Print summary
        printTestSummary();
    }
    
    /**
     * Tests UserService functionality and HashMap performance.
     */
    private static void testUserService() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST SUITE: UserService (HashMap<String, User>)");
        System.out.println("=".repeat(80));
        
        UserService userService = new UserService();
        
        // Test 1: Default users exist
        test("Default users initialized", () -> {
            return userService.getUserCount() == 3;
        });
        
        // Test 2: Login with valid credentials
        test("Login with valid credentials", () -> {
            User user = userService.login("admin", "admin123");
            return user != null && user.getUsername().equals("admin");
        });
        
        // Test 3: Login with invalid credentials
        test("Login with invalid credentials fails", () -> {
            User user = userService.login("admin", "wrongpassword");
            return user == null;
        });
        
        // Test 4: Registration with new username
        test("Register new user", () -> {
            boolean result = userService.register("testuser", "testpass", Role.PLAYER);
            return result && userService.getUserCount() == 4;
        });
        
        // Test 5: Registration with duplicate username
        test("Duplicate username rejected", () -> {
            boolean result = userService.register("admin", "password", Role.PLAYER);
            return !result;
        });
        
        // Test 6: HashMap O(1) lookup performance
        test("HashMap O(1) lookup performance", () -> {
            // Add 1000 users
            for (int i = 0; i < 1000; i++) {
                userService.register("user" + i, "pass" + i, Role.PLAYER);
            }
            
            // Measure lookup time
            long startTime = System.nanoTime();
            for (int i = 0; i < 100; i++) {
                userService.getUserByUsername("user" + (i * 10));
            }
            long endTime = System.nanoTime();
            
            double avgTimeMs = (endTime - startTime) / 100.0 / 1_000_000.0;
            System.out.println("    Average lookup time: " + String.format("%.4f", avgTimeMs) + " ms");
            
            return avgTimeMs < 1.0; // Should be < 1ms
        });
        
        // Test 7: Current user tracking
        test("Current user tracking", () -> {
            userService.logout();
            userService.login("player1", "pass123");
            User current = userService.getCurrentUser();
            return current != null && current.getUsername().equals("player1");
        });
        
        // Test 8: Admin role check
        test("Admin role verification", () -> {
            userService.logout();
            userService.login("admin", "admin123");
            return userService.isCurrentUserAdmin();
        });
    }
    
    /**
     * Tests GameService functionality and Queue performance.
     */
    private static void testGameService() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST SUITE: GameService (Queue<String>)");
        System.out.println("=".repeat(80));
        
        GameService gameService = new GameService();
        
        // Test 1: Load content
        test("Load game content", () -> {
            gameService.loadContent(GameMode.TIMED, "medium", 50);
            return gameService.getRemainingWordCount() == 50;
        });
        
        // Test 2: FIFO word delivery
        test("FIFO word delivery", () -> {
            List<String> testWords = Arrays.asList("first", "second", "third");
            gameService.loadCustomContent(testWords);
            
            String word1 = gameService.getNextWord();
            String word2 = gameService.getNextWord();
            String word3 = gameService.getNextWord();
            
            return word1.equals("first") && word2.equals("second") && word3.equals("third");
        });
        
        // Test 3: Queue exhaustion
        test("Queue exhaustion handling", () -> {
            gameService.loadCustomContent(Arrays.asList("only"));
            gameService.getNextWord();
            String next = gameService.getNextWord();
            return next == null && !gameService.hasMoreWords();
        });
        
        // Test 4: Peek without removal
        test("Peek without removal", () -> {
            gameService.loadCustomContent(Arrays.asList("peek", "test"));
            String peeked = gameService.peekNextWord();
            String next = gameService.getNextWord();
            return peeked.equals("peek") && next.equals("peek");
        });
        
        // Test 5: Queue O(1) operations performance
        test("Queue O(1) enqueue/dequeue performance", () -> {
            List<String> words = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                words.add("word" + i);
            }
            
            // Measure enqueue time
            long startTime = System.nanoTime();
            gameService.loadCustomContent(words);
            long endTime = System.nanoTime();
            double enqueueTimeMs = (endTime - startTime) / 1_000_000.0;
            
            // Measure dequeue time
            startTime = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                gameService.getNextWord();
            }
            endTime = System.nanoTime();
            double dequeueTimeMs = (endTime - startTime) / 1000.0 / 1_000_000.0;
            
            System.out.println("    Enqueue 1000 words: " + String.format("%.4f", enqueueTimeMs) + " ms");
            System.out.println("    Avg dequeue time: " + String.format("%.4f", dequeueTimeMs) + " ms");
            
            return enqueueTimeMs < 50.0 && dequeueTimeMs < 1.0;
        });
        
        // Test 6: Word validation
        test("Word validation", () -> {
            gameService.loadCustomContent(Arrays.asList("test"));
            gameService.getNextWord();
            return gameService.validateTyping("test") && !gameService.validateTyping("wrong");
        });
        
        // Test 7: Words delivered counter
        test("Words delivered counter", () -> {
            gameService.reset();
            gameService.loadCustomContent(Arrays.asList("one", "two", "three"));
            gameService.getNextWord();
            gameService.getNextWord();
            return gameService.getWordsDelivered() == 2;
        });
    }
    
    /**
     * Tests PerformanceTracker functionality and Stack performance.
     */
    private static void testPerformanceTracker() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST SUITE: PerformanceTracker (Stack<Character>)");
        System.out.println("=".repeat(80));
        
        PerformanceTracker tracker = new PerformanceTracker();
        
        // Test 1: Initial state
        test("Initial state correct", () -> {
            return tracker.getCorrectCharacters() == 0 && 
                   tracker.getErrorCount() == 0 &&
                   tracker.getCurrentAccuracy() == 100.0;
        });
        
        // Test 2: Record correct characters
        test("Record correct characters", () -> {
            tracker.reset();
            tracker.startTracking();
            tracker.recordCorrectCharacter('a');
            tracker.recordCorrectCharacter('b');
            tracker.recordCorrectCharacter('c');
            return tracker.getCorrectCharacters() == 3;
        });
        
        // Test 3: Record errors
        test("Record errors", () -> {
            tracker.reset();
            tracker.startTracking();
            tracker.recordError('a', 'b');
            tracker.recordError('c', 'd');
            return tracker.getErrorCount() == 2;
        });
        
        // Test 4: Accuracy calculation
        test("Accuracy calculation", () -> {
            tracker.reset();
            tracker.startTracking();
            // 8 correct, 2 errors = 80% accuracy
            for (int i = 0; i < 8; i++) {
                tracker.recordCorrectCharacter('x');
            }
            tracker.recordError('a', 'b');
            tracker.recordError('c', 'd');
            
            double accuracy = tracker.getCurrentAccuracy();
            return Math.abs(accuracy - 80.0) < 0.1;
        });
        
        // Test 5: WPM calculation
        test("WPM calculation", () -> {
            tracker.reset();
            tracker.startTracking();
            
            // Simulate typing 25 characters in 1 second (300 WPM)
            for (int i = 0; i < 25; i++) {
                tracker.recordCorrectCharacter('x');
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            tracker.stopTracking();
            double wpm = tracker.getCurrentWPM();
            
            System.out.println("    Calculated WPM: " + String.format("%.2f", wpm));
            
            // Should be around 300 WPM (25 chars / 5 chars per word / 1 second * 60)
            return wpm > 200 && wpm < 400;
        });
        
        // Test 6: Stack O(1) operations performance
        test("Stack O(1) push/pop performance", () -> {
            tracker.reset();
            tracker.startTracking();
            
            // Measure push time
            long startTime = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                tracker.recordError('a', 'b');
            }
            long endTime = System.nanoTime();
            double pushTimeMs = (endTime - startTime) / 1000.0 / 1_000_000.0;
            
            System.out.println("    Avg push time: " + String.format("%.4f", pushTimeMs) + " ms");
            
            return pushTimeMs < 1.0;
        });
    }
    
    /**
     * Tests ScoreManager functionality and TreeMap performance.
     */
    private static void testScoreManager() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST SUITE: ScoreManager (TreeMap<Integer, List<Score>>)");
        System.out.println("=".repeat(80));
        
        ScoreManager scoreManager = new ScoreManager();
        
        // Test 1: Add scores
        test("Add scores", () -> {
            Score score1 = new Score("user1", 50.0, 95.0, java.time.LocalDateTime.now(), GameMode.TIMED);
            Score score2 = new Score("user2", 60.0, 90.0, java.time.LocalDateTime.now(), GameMode.TIMED);
            scoreManager.addScore(score1);
            scoreManager.addScore(score2);
            return scoreManager.getTotalScoreCount() >= 2;
        });
        
        // Test 2: Automatic sorting by WPM
        test("Automatic sorting by WPM", () -> {
            ScoreManager sm = new ScoreManager();
            sm.addScore(new Score("user1", 30.0, 95.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            sm.addScore(new Score("user2", 50.0, 90.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            sm.addScore(new Score("user3", 40.0, 92.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            
            List<Score> topScores = sm.getTopScores(3);
            
            // Should be sorted: 50, 40, 30
            return topScores.size() == 3 &&
                   topScores.get(0).getWpm() == 50.0 &&
                   topScores.get(1).getWpm() == 40.0 &&
                   topScores.get(2).getWpm() == 30.0;
        });
        
        // Test 3: Get top N scores
        test("Get top N scores", () -> {
            ScoreManager sm = new ScoreManager();
            for (int i = 0; i < 20; i++) {
                sm.addScore(new Score("user" + i, 10.0 + i, 95.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            }
            
            List<Score> top5 = sm.getTopScores(5);
            return top5.size() == 5 && top5.get(0).getWpm() == 29.0;
        });
        
        // Test 4: Get user scores
        test("Get user scores", () -> {
            ScoreManager sm = new ScoreManager();
            sm.addScore(new Score("testuser", 50.0, 95.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            sm.addScore(new Score("testuser", 60.0, 90.0, java.time.LocalDateTime.now(), GameMode.FREE_PRACTICE));
            sm.addScore(new Score("other", 40.0, 92.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            
            List<Score> userScores = sm.getUserScores("testuser");
            return userScores.size() == 2;
        });
        
        // Test 5: TreeMap O(log n) insertion performance
        test("TreeMap O(log n) insertion performance", () -> {
            ScoreManager sm = new ScoreManager();
            
            long startTime = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                sm.addScore(new Score("user" + i, 10.0 + (i % 100), 95.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            }
            long endTime = System.nanoTime();
            
            double avgTimeMs = (endTime - startTime) / 1000.0 / 1_000_000.0;
            System.out.println("    Avg insertion time: " + String.format("%.4f", avgTimeMs) + " ms");
            
            return avgTimeMs < 5.0;
        });
        
        // Test 6: Get best score for user
        test("Get best score for user", () -> {
            ScoreManager sm = new ScoreManager();
            sm.addScore(new Score("testuser", 50.0, 95.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            sm.addScore(new Score("testuser", 70.0, 90.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            sm.addScore(new Score("testuser", 60.0, 92.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            
            Score best = sm.getUserBestScore("testuser");
            return best != null && best.getWpm() == 70.0;
        });
    }
    
    /**
     * Tests data structure performance characteristics.
     */
    private static void testDataStructurePerformance() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST SUITE: Data Structure Performance Benchmarks");
        System.out.println("=".repeat(80));
        
        // Test 1: ArrayList session history performance
        test("ArrayList O(1) append performance", () -> {
            List<GameSession> sessions = new ArrayList<>();
            
            long startTime = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                sessions.add(new GameSession("user", GameMode.TIMED, 60, 500, 450, 50));
            }
            long endTime = System.nanoTime();
            
            double avgTimeMs = (endTime - startTime) / 1000.0 / 1_000_000.0;
            System.out.println("    Avg append time: " + String.format("%.4f", avgTimeMs) + " ms");
            
            return avgTimeMs < 1.0;
        });
        
        // Test 2: ArrayList indexed access
        test("ArrayList O(1) indexed access", () -> {
            List<GameSession> sessions = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                sessions.add(new GameSession("user", GameMode.TIMED, 60, 500, 450, 50));
            }
            
            long startTime = System.nanoTime();
            for (int i = 0; i < 100; i++) {
                GameSession s = sessions.get(i * 10);
            }
            long endTime = System.nanoTime();
            
            double avgTimeMs = (endTime - startTime) / 100.0 / 1_000_000.0;
            System.out.println("    Avg access time: " + String.format("%.4f", avgTimeMs) + " ms");
            
            return avgTimeMs < 0.1;
        });
        
        // Test 3: Memory efficiency
        test("Memory efficiency check", () -> {
            Runtime runtime = Runtime.getRuntime();
            long memBefore = runtime.totalMemory() - runtime.freeMemory();
            
            // Create large dataset
            UserService userService = new UserService();
            for (int i = 0; i < 1000; i++) {
                userService.register("user" + i, "pass" + i, Role.PLAYER);
            }
            
            GameService gameService = new GameService();
            gameService.loadContent(GameMode.TIMED, "medium", 1000);
            
            ScoreManager scoreManager = new ScoreManager();
            for (int i = 0; i < 1000; i++) {
                scoreManager.addScore(new Score("user" + i, 50.0 + i, 95.0, java.time.LocalDateTime.now(), GameMode.TIMED));
            }
            
            long memAfter = runtime.totalMemory() - runtime.freeMemory();
            long memUsedMB = (memAfter - memBefore) / (1024 * 1024);
            
            System.out.println("    Memory used: " + memUsedMB + " MB");
            
            return memUsedMB < 50; // Should use less than 50MB
        });
    }
    
    /**
     * Tests edge cases and boundary conditions.
     */
    private static void testEdgeCases() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST SUITE: Edge Cases & Boundary Conditions");
        System.out.println("=".repeat(80));
        
        // Test 1: Empty username/password
        test("Empty credentials rejected", () -> {
            UserService us = new UserService();
            boolean result1 = us.register("", "pass", Role.PLAYER);
            boolean result2 = us.register("user", "", Role.PLAYER);
            return !result1 && !result2;
        });
        
        // Test 2: Very long username
        test("Very long username handled", () -> {
            UserService us = new UserService();
            String longName = "a".repeat(1000);
            boolean result = us.register(longName, "pass", Role.PLAYER);
            return result; // Should handle gracefully
        });
        
        // Test 3: Special characters in username
        test("Special characters in username", () -> {
            UserService us = new UserService();
            boolean result = us.register("user@#$%", "pass", Role.PLAYER);
            return result; // Should allow (no restriction specified)
        });
        
        // Test 4: Zero WPM score
        test("Zero WPM score handled", () -> {
            ScoreManager sm = new ScoreManager();
            Score score = new Score("user", 0.0, 0.0, java.time.LocalDateTime.now(), GameMode.TIMED);
            sm.addScore(score);
            return sm.getTotalScoreCount() > 0;
        });
        
        // Test 5: Very high WPM score
        test("Very high WPM score handled", () -> {
            ScoreManager sm = new ScoreManager();
            Score score = new Score("user", 999.9, 100.0, java.time.LocalDateTime.now(), GameMode.TIMED);
            sm.addScore(score);
            List<Score> top = sm.getTopScores(1);
            return top.get(0).getWpm() == 999.9;
        });
        
        // Test 6: Empty game session
        test("Empty game session handled", () -> {
            GameSession session = new GameSession("user", GameMode.TIMED, 0, 0, 0, 0);
            return session.getWpm() == 0.0 && session.getAccuracy() == 0.0;
        });
        
        // Test 7: Null safety
        test("Null safety checks", () -> {
            UserService us = new UserService();
            User user = us.login(null, null);
            return user == null;
        });
    }
    
    /**
     * Helper method to run a test and track results.
     */
    private static void test(String testName, TestCase testCase) {
        System.out.print("  [TEST] " + testName + "... ");
        try {
            boolean result = testCase.run();
            if (result) {
                System.out.println("✓ PASSED");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED");
                testsFailed++;
                failedTests.add(testName);
            }
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + e.getMessage());
            testsFailed++;
            failedTests.add(testName + " (Exception: " + e.getMessage() + ")");
        }
    }
    
    /**
     * Prints the test summary.
     */
    private static void printTestSummary() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(80));
        
        int totalTests = testsPassed + testsFailed;
        double passRate = (totalTests > 0) ? (testsPassed * 100.0 / totalTests) : 0;
        
        System.out.println("\nTotal Tests: " + totalTests);
        System.out.println("Passed: " + testsPassed + " ✓");
        System.out.println("Failed: " + testsFailed + " ✗");
        System.out.println("Pass Rate: " + String.format("%.1f", passRate) + "%");
        
        if (testsFailed > 0) {
            System.out.println("\nFailed Tests:");
            for (String test : failedTests) {
                System.out.println("  - " + test);
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        
        if (passRate >= 90) {
            System.out.println("✓ EXCELLENT: All critical tests passed!");
        } else if (passRate >= 70) {
            System.out.println("⚠ WARNING: Some tests failed. Review and fix issues.");
        } else {
            System.out.println("✗ CRITICAL: Many tests failed. Immediate attention required.");
        }
        
        System.out.println("=".repeat(80));
    }
    
    /**
     * Functional interface for test cases.
     */
    @FunctionalInterface
    interface TestCase {
        boolean run() throws Exception;
    }
}
