package com.typinggame.service;

import com.typinggame.model.GameMode;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Service class for managing game content and word delivery.
 * Handles loading typing content and providing words in sequence.
 */
public class GameService {
    
    /**
     * DATA STRUCTURE: Queue<String> (implemented as LinkedList)
     * PURPOSE: Manages words/sentences to be typed in FIFO (First-In-First-Out) order
     * JUSTIFICATION:
     * - Natural fit for sequential word delivery during gameplay
     * - FIFO ensures words are presented in the order they were loaded
     * - O(1) time complexity for enqueue (add) and dequeue (poll) operations
     * - Efficient for streaming content to the player
     * - Supports dynamic content loading during gameplay
     * 
     * Alternative considered: ArrayList would allow random access, but Queue's
     * FIFO behavior better represents the sequential nature of typing practice.
     */
    private Queue<String> wordQueue;
    
    /**
     * Predefined word lists for different difficulty levels.
     * In a production system, these could be loaded from files or a database.
     */
    private static final List<String> EASY_WORDS = Arrays.asList(
        "the", "and", "for", "are", "but", "not", "you", "all", "can", "her",
        "was", "one", "our", "out", "day", "get", "has", "him", "his", "how",
        "man", "new", "now", "old", "see", "two", "way", "who", "boy", "did",
        "its", "let", "put", "say", "she", "too", "use", "dad", "mom", "cat"
    );
    
    private static final List<String> MEDIUM_WORDS = Arrays.asList(
        "about", "after", "again", "below", "could", "every", "first", "found",
        "great", "house", "large", "learn", "never", "other", "place", "plant",
        "point", "right", "small", "sound", "spell", "still", "study", "their",
        "there", "these", "thing", "think", "three", "water", "where", "which",
        "world", "would", "write", "above", "added", "almost", "always", "began"
    );
    
    private static final List<String> HARD_WORDS = Arrays.asList(
        "algorithm", "development", "programming", "javascript", "technology",
        "implementation", "architecture", "performance", "optimization", "framework",
        "application", "infrastructure", "configuration", "authentication", "authorization",
        "synchronization", "asynchronous", "polymorphism", "encapsulation", "inheritance",
        "abstraction", "instantiation", "initialization", "documentation", "specification",
        "integration", "deployment", "maintenance", "refactoring", "debugging"
    );
    
    private static final List<String> SENTENCES = Arrays.asList(
        "The quick brown fox jumps over the lazy dog.",
        "Practice makes perfect when learning to type.",
        "Programming is the art of telling a computer what to do.",
        "Java is a powerful object-oriented programming language.",
        "Data structures are fundamental to computer science.",
        "Typing speed improves with consistent practice.",
        "Good code is easy to read and understand.",
        "Software development requires patience and dedication.",
        "Learning new skills takes time and effort.",
        "Technology continues to evolve rapidly every day."
    );
    
    private Random random;
    private String currentWord;
    private int wordsDelivered;
    
    /**
     * Constructor initializes the game service.
     */
    public GameService() {
        this.wordQueue = new LinkedList<>();
        this.random = new Random();
        this.currentWord = null;
        this.wordsDelivered = 0;
    }
    
    /**
     * Loads content into the word queue based on game mode and difficulty.
     *
     * For the "sentences" difficulty, this method enqueues individual sentences
     * so that controllers can advance one sentence at a time. The wordCount
     * parameter is interpreted as the number of sentences to load.
     *
     * @param mode Game mode (TIMED or FREE_PRACTICE)
     * @param difficulty Difficulty level ("easy", "medium", "hard", "sentences")
     * @param wordCount Number of items to load (sentences when using "sentences" difficulty)
     */
    public void loadContent(GameMode mode, String difficulty, int wordCount) {
        wordQueue.clear();
        wordsDelivered = 0;

        if ("sentences".equalsIgnoreCase(difficulty)) {
            // For sentence-based modes (timed + practice), prefer shorter
            // sentences so they fit comfortably inside the display container.
            List<String> baseList = getWordListByDifficulty("sentences");
            List<String> shortSentences = new ArrayList<>();
            int maxLength = 80; // maximum characters per sentence for UI fit

            for (String s : baseList) {
                if (s.length() <= maxLength) {
                    shortSentences.add(s);
                }
            }

            // Fallback: if filtering removed everything, use the original list
            List<String> sourceList = shortSentences.isEmpty() ? baseList : shortSentences;

            int sentenceCount = wordCount > 0 ? wordCount : 4;
            for (int i = 0; i < sentenceCount; i++) {
                String sentence = sourceList.get(random.nextInt(sourceList.size()));
                wordQueue.offer(sentence);
            }
            System.out.println("Content loaded: " + sentenceCount + " sentences (" + difficulty + ")");
            return;
        }

        List<String> sourceList = getWordListByDifficulty(difficulty);
        int itemsToLoad = wordCount > 0 ? wordCount : sourceList.size();
        for (int i = 0; i < itemsToLoad; i++) {
            String word = sourceList.get(random.nextInt(sourceList.size()));
            wordQueue.offer(word);  // O(1) enqueue operation
        }

        System.out.println("Content loaded: " + itemsToLoad + " words (" + difficulty + ")");
    }
    
    /**
     * Loads a specific list of words into the queue.
     * Used for custom content or admin-defined word lists.
     * 
     * @param words List of words to load
     */
    public void loadCustomContent(List<String> words) {
        wordQueue.clear();
        wordsDelivered = 0;
        
        for (String word : words) {
            wordQueue.offer(word);  // O(1) enqueue operation
        }
        
        System.out.println("Custom content loaded: " + words.size() + " words");
    }
    
    /**
     * Gets the next word from the queue.
     * Demonstrates Queue's FIFO behavior.
     * 
     * @return Next word to type, or null if queue is empty
     */
    public String getNextWord() {
        currentWord = wordQueue.poll();  // O(1) dequeue operation
        
        if (currentWord != null) {
            wordsDelivered++;
        }
        
        return currentWord;
    }
    
    /**
     * Peeks at the next word without removing it from the queue.
     * 
     * @return Next word, or null if queue is empty
     */
    public String peekNextWord() {
        return wordQueue.peek();  // O(1) peek operation
    }
    
    /**
     * Gets the current word being typed.
     * 
     * @return Current word
     */
    public String getCurrentWord() {
        return currentWord;
    }
    
    /**
     * Checks if there are more words in the queue.
     * 
     * @return true if queue has more words, false otherwise
     */
    public boolean hasMoreWords() {
        return !wordQueue.isEmpty();
    }
    
    /**
     * Gets the number of words remaining in the queue.
     * 
     * @return Remaining word count
     */
    public int getRemainingWordCount() {
        return wordQueue.size();
    }
    
    /**
     * Gets the number of words delivered so far.
     * 
     * @return Words delivered count
     */
    public int getWordsDelivered() {
        return wordsDelivered;
    }
    
    /**
     * Validates if the typed text matches the current word.
     * 
     * @param typedText Text typed by the user
     * @return true if match is exact, false otherwise
     */
    public boolean validateTyping(String typedText) {
        if (currentWord == null || typedText == null) {
            return false;
        }
        return currentWord.equals(typedText);
    }
    
    /**
     * Checks if typed text matches current word (case-insensitive).
     * 
     * @param typedText Text typed by the user
     * @return true if match (ignoring case), false otherwise
     */
    public boolean validateTypingIgnoreCase(String typedText) {
        if (currentWord == null || typedText == null) {
            return false;
        }
        return currentWord.equalsIgnoreCase(typedText);
    }
    
    /**
     * Calculates character-level accuracy for the current word.
     * 
     * @param typedText Text typed by the user
     * @return Accuracy percentage (0-100)
     */
    public double calculateWordAccuracy(String typedText) {
        if (currentWord == null || typedText == null) {
            return 0.0;
        }
        
        int correctChars = 0;
        int maxLength = Math.max(currentWord.length(), typedText.length());
        
        for (int i = 0; i < Math.min(currentWord.length(), typedText.length()); i++) {
            if (currentWord.charAt(i) == typedText.charAt(i)) {
                correctChars++;
            }
        }
        
        return (correctChars * 100.0) / maxLength;
    }
    
    /**
     * Gets the word list based on difficulty level.
     * 
     * @param difficulty Difficulty level string
     * @return List of words for that difficulty
     */
    private List<String> getWordListByDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return EASY_WORDS;
            case "medium":
                return MEDIUM_WORDS;
            case "hard":
                return HARD_WORDS;
            case "sentences":
                return SENTENCES;
            default:
                return MEDIUM_WORDS;
        }
    }
    
    /**
     * Resets the game service for a new game.
     */
    public void reset() {
        wordQueue.clear();
        currentWord = null;
        wordsDelivered = 0;
    }
    
    /**
     * Gets statistics about the current game session.
     * 
     * @return Statistics string
     */
    public String getGameStats() {
        return String.format("Words delivered: %d, Remaining: %d",
                wordsDelivered, wordQueue.size());
    }
}
