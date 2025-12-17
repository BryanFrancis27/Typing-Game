# Typing Game Application - Development Plan

## Project Overview
Interactive JavaFX Typing Game with user authentication, multiple game modes, and performance tracking.

## Information Gathered
- Empty project directory - building from scratch
- Requirements: JavaFX GUI, OOP principles, justified data structure usage
- Core features: Login system, game modes, performance tracking, score management

## Detailed Implementation Plan

### Phase 1: Project Structure Setup
1. Create Maven/Gradle project structure
2. Set up JavaFX dependencies
3. Create package structure:
   - `com.typinggame.main` - Main application class
   - `com.typinggame.model` - Data models (User, GameSession, Score)
   - `com.typinggame.controller` - JavaFX controllers
   - `com.typinggame.view` - FXML files (optional) or scene builders
   - `com.typinggame.service` - Business logic (UserService, GameService)
   - `com.typinggame.util` - Utility classes
   - `com.typinggame.datastructure` - Custom data structure implementations

### Phase 2: Data Structure Implementation & Documentation

**Selected Data Structures (with justification):**

1. **HashMap<String, User>** - User Management
   - Purpose: O(1) lookup for user authentication
   - Stores username as key, User object as value
   - Efficient for login validation

2. **Queue<String>** (LinkedList) - Word/Sentence Management
   - Purpose: FIFO order for typing content
   - Manages words to be typed in sequence
   - Natural fit for ordered content delivery

3. **ArrayList<GameSession>** - Session History
   - Purpose: Dynamic array for storing game sessions
   - Allows indexed access for viewing past results
   - Supports sorting for rankings

4. **Stack<Character>** - Error Tracking
   - Purpose: Track recent typing errors (undo functionality)
   - LIFO structure for backspace operations
   - Real-time error management

5. **TreeMap<Integer, List<Score>>** - Score Ranking
   - Purpose: Automatically sorted scores by WPM
   - Efficient range queries for leaderboards
   - Maintains sorted order for rankings

6. **Graph (Adjacency List)** - Scene Navigation
   - Purpose: Represent UI flow and transitions
   - Nodes: Different screens (Login, Menu, Game, Results)
   - Edges: Valid transitions between screens

### Phase 3: Core Classes Implementation

#### Model Classes
1. **User.java**
   - Fields: username, password, role (ADMIN/PLAYER), sessionHistory
   - Methods: getters, setters, authentication

2. **GameSession.java**
   - Fields: timestamp, mode, wpm, accuracy, errorCount, duration
   - Methods: calculateWPM(), calculateAccuracy()

3. **Score.java**
   - Fields: username, wpm, accuracy, date, mode
   - Implements Comparable for sorting

4. **Role.java** (Enum)
   - Values: ADMIN, PLAYER

5. **GameMode.java** (Enum)
   - Values: TIMED, FREE_PRACTICE

#### Service Classes
1. **UserService.java**
   - HashMap for user storage
   - Methods: login(), register(), getUserByUsername()

2. **GameService.java**
   - Queue for word management
   - Methods: loadContent(), getNextWord(), checkTyping()

3. **PerformanceTracker.java**
   - Real-time tracking of WPM, accuracy, errors
   - Methods: updateStats(), calculateFinalScore()

4. **ScoreManager.java**
   - TreeMap for score ranking
   - Methods: addScore(), getTopScores(), getUserScores()

#### Controller Classes
1. **LoginController.java**
   - Handles login UI and authentication

2. **MenuController.java**
   - Game mode selection
   - Admin panel access

3. **GameController.java**
   - Main gameplay logic
   - Real-time UI updates
   - Timer management

4. **ResultsController.java**
   - Display session results
   - Show statistics and rankings

5. **AdminController.java**
   - View all player reports
   - Manage typing content

### Phase 4: JavaFX UI Implementation

#### Scenes to Create
1. **LoginScene**
   - Username/password fields
   - Login button
   - Role selection (optional)

2. **MenuScene**
   - Game mode buttons (Timed, Free Practice)
   - View History button
   - Admin Panel button (if admin)
   - Logout button

3. **GameScene**
   - Text display area (words to type)
   - Input text field
   - Timer display
   - Live stats (WPM, Accuracy, Errors)
   - Progress indicator

4. **ResultsScene**
   - Final statistics display
   - Performance graph/chart
   - Play Again / Main Menu buttons

5. **AdminScene**
   - Player performance table
   - Content management panel

### Phase 5: Game Logic Implementation

1. **Word/Sentence Loading**
   - Load from predefined list or file
   - Queue management for sequential display

2. **Typing Validation**
   - Character-by-character comparison
   - Error detection and tracking (Stack)
   - Real-time feedback

3. **Timer Management**
   - JavaFX Timeline for countdown
   - Update UI every second

4. **Performance Calculation**
   - WPM = (characters typed / 5) / (time in minutes)
   - Accuracy = (correct characters / total characters) * 100
   - Error count tracking

### Phase 6: Testing & Refinement

1. Test all user flows
2. Verify data structure efficiency
3. Ensure UI responsiveness
4. Add error handling
5. Code documentation review

## File Structure
```
TypingGame-V2/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── typinggame/
│       │           ├── Main.java
│       │           ├── model/
│       │           │   ├── User.java
│       │           │   ├── GameSession.java
│       │           │   ├── Score.java
│       │           │   ├── Role.java
│       │           │   └── GameMode.java
│       │           ├── controller/
│       │           │   ├── LoginController.java
│       │           │   ├── MenuController.java
│       │           │   ├── GameController.java
│       │           │   ├── ResultsController.java
│       │           │   └── AdminController.java
│       │           ├── service/
│       │           │   ├── UserService.java
│       │           │   ├── GameService.java
│       │           │   ├── PerformanceTracker.java
│       │           │   └── ScoreManager.java
│       │           └── util/
│       │               ├── SceneManager.java
│       │               └── TypingContent.java
│       └── resources/
│           ├── styles.css
│           └── words.txt
├── pom.xml (Maven) or build.gradle (Gradle)
└── README.md
```

## Dependencies Required
- JavaFX SDK (version 17 or higher)
- Maven or Gradle for build management

## Next Steps
1. Create Maven/Gradle configuration
2. Implement model classes
3. Implement service layer with data structures
4. Create JavaFX controllers and scenes
5. Integrate all components
6. Test and refine

## Data Structure Highlights (Code Comments)
Each data structure usage will include:
```java
/**
 * DATA STRUCTURE: HashMap<String, User>
 * PURPOSE: Stores user accounts for O(1) authentication lookup
 * JUSTIFICATION: Fast retrieval by username during login process
 */
private HashMap<String, User> users = new HashMap<>();
