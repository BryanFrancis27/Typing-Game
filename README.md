# 🎮 Typing Game Application

An interactive JavaFX-based typing game designed to help users improve their typing speed, accuracy, and consistency. Built with Java 17 and JavaFX, featuring real-time performance tracking, multiple game modes, and comprehensive data structure implementations.

## 📋 Table of Contents

- [Features](#features)
- [Data Structures](#data-structures)
- [Requirements](#requirements)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Usage Guide](#usage-guide)
- [Project Structure](#project-structure)
- [Default Accounts](#default-accounts)
- [Technical Details](#technical-details)

## ✨ Features

### Core Functionality

- **User Authentication System**
  - Secure login with role-based access (Admin & Player)
  - User registration with validation
  - Session management

- **Multiple Game Modes**
  - **Timed Mode**: Type as fast as possible within 60 seconds
  - **Free Practice Mode**: Unlimited practice without time constraints

- **Real-Time Performance Tracking**
  - Words Per Minute (WPM) calculation
  - Accuracy percentage monitoring
  - Error count tracking
  - Live statistics updates

- **Score Management & Leaderboards**
  - Automatic score ranking
  - Top player leaderboards
  - Personal best tracking
  - Performance grading (A+ to F)

- **Session History**
  - Complete game history tracking
  - Detailed statistics per session
  - Export functionality
  - Performance trends

- **Admin Panel**
  - View all player reports
  - System statistics
  - Player performance analysis
  - Content management capabilities

## 🔧 Data Structures

This project demonstrates intentional and justified use of various data structures:

| Data Structure | Implementation | Purpose | Time Complexity |
|---------------|----------------|---------|-----------------|
| **HashMap** | `UserService` | User authentication & storage | O(1) lookup |
| **Queue (LinkedList)** | `GameService` | Sequential word delivery (FIFO) | O(1) enqueue/dequeue |
| **Stack** | `PerformanceTracker` | Error tracking & undo (LIFO) | O(1) push/pop |
| **TreeMap** | `ScoreManager` | Automatic score ranking | O(log n) insert/search |
| **ArrayList** | `User` | Session history storage | O(1) append |
| **Graph (Adjacency List)** | `SceneManager` | UI navigation validation | O(1) transition check |

Each data structure is documented in the code with:
- Purpose explanation
- Justification for selection
- Alternative considerations
- Time complexity analysis

## 📦 Requirements

- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: Version 3.6 or higher
- **JavaFX SDK**: Version 17.0.2 (included via Maven)
- **Operating System**: Windows, macOS, or Linux

## 🚀 Installation

### 1. Clone or Download the Project

```bash
cd TypingGame-V2
```

### 2. Verify Java Installation

```bash
java -version
```

Expected output: `java version "17.x.x"` or higher

### 3. Verify Maven Installation

```bash
mvn -version
```

Expected output: `Apache Maven 3.x.x` or higher

### 4. Install Dependencies

```bash
mvn clean install
```

This will download all required dependencies including JavaFX.

## ▶️ Running the Application

### Option 1: Using Maven (Recommended)

```bash
mvn javafx:run
```

### Option 2: Using Maven with Clean Build

```bash
mvn clean javafx:run
```

### Option 3: Package and Run JAR

```bash
mvn clean package
java -jar target/typing-game-1.0.0.jar
```

## 📖 Usage Guide

### Getting Started

1. **Launch the Application**
   - Run using one of the methods above
   - The login screen will appear

2. **Login**
   - Use default accounts (see below) or create a new account
   - Click "Login" to proceed

3. **Main Menu**
   - Select a game mode (Timed or Free Practice)
   - View your history
   - Access admin panel (if admin)
   - Logout

### Playing the Game

1. **Start a Game**
   - Choose your preferred game mode from the menu
   - The game will start immediately

2. **Typing**
   - Type the displayed word in the input field
   - Press Enter or complete the word to submit
   - Real-time feedback shows correct/incorrect typing
   - Statistics update live during gameplay

3. **Game Controls**
   - **Pause**: Temporarily pause the game
   - **Quit**: Exit to main menu (progress lost)

4. **Results**
   - View your performance statistics
   - See your grade (A+ to F)
   - Check your leaderboard rank
   - Play again or return to menu

### Admin Features

Admins can access additional features:

- **Player Reports**: View all player statistics
- **Leaderboards**: See top performers
- **System Statistics**: Overall system metrics
- **Player Details**: Detailed session history for each player

### History & Statistics

- **View History**: See all your past game sessions
- **Export Data**: Export your statistics to text format
- **Clear History**: Remove all session records (with confirmation)

## 📁 Project Structure

```
TypingGame-V2/
├── src/main/java/com/typinggame/
│   ├── Main.java                          # Application entry point
│   ├── model/                             # Data models
│   │   ├── Role.java                      # User roles enum
│   │   ├── GameMode.java                  # Game modes enum
│   │   ├── User.java                      # User account model
│   │   ├── GameSession.java               # Game session data
│   │   └── Score.java                     # Score/leaderboard entry
│   ├── service/                           # Business logic layer
│   │   ├── UserService.java               # User management (HashMap)
│   │   ├── GameService.java               # Game content (Queue)
│   │   ├── PerformanceTracker.java        # Performance tracking (Stack)
│   │   └── ScoreManager.java              # Score management (TreeMap)
│   ├── controller/                        # JavaFX controllers
│   │   ├── LoginController.java           # Login screen
│   │   ├── MenuController.java            # Main menu
│   │   ├── GameController.java            # Gameplay screen
│   │   ├── ResultsController.java         # Results display
│   │   ├── AdminController.java           # Admin panel
│   │   └── HistoryController.java         # History viewer
│   └── util/                              # Utility classes
│       └── SceneManager.java              # Scene navigation (Graph)
├── pom.xml                                # Maven configuration
├── README.md                              # This file
├── PROJECT_PLAN.md                        # Development plan
└── PHASE1_SUMMARY.md                      # Phase 1 completion report
```

## 👤 Default Accounts

The application comes with pre-configured accounts for testing:

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Access**: Full admin panel access

### Player Accounts
- **Username**: `player1`
- **Password**: `pass123`

- **Username**: `player2`
- **Password**: `pass123`

You can also create new player accounts using the registration feature.

## 🔍 Technical Details

### Architecture

- **Design Pattern**: Model-View-Controller (MVC)
- **Programming Paradigm**: Object-Oriented Programming (OOP)
- **UI Framework**: JavaFX 17
- **Build Tool**: Maven
- **Java Version**: 17

### Key Components

1. **Model Layer**: Data structures and business entities
2. **Service Layer**: Business logic and data structure implementations
3. **Controller Layer**: UI logic and user interaction handling
4. **Utility Layer**: Helper classes and scene management

### Performance Metrics

- **WPM Calculation**: `(characters typed / 5) / (time in minutes)`
- **Accuracy**: `(correct characters / total characters) × 100`
- **Grade System**: Based on WPM and accuracy combination

### Word Lists

- **Easy**: 40 common words (3-5 characters)
- **Medium**: 40 intermediate words (5-7 characters)
- **Hard**: 30 advanced words (8-15 characters)
- **Sentences**: 10 complete sentences for practice

## 🐛 Troubleshooting

### JavaFX Not Found

If you encounter JavaFX-related errors:

```bash
# Ensure JavaFX is properly configured
mvn clean install -U
```

### Maven Build Fails

```bash
# Clear Maven cache and rebuild
mvn clean
mvn install -U
```

### Application Won't Start

1. Verify Java version: `java -version`
2. Check Maven installation: `mvn -version`
3. Ensure all dependencies are installed: `mvn dependency:resolve`

## 📝 Development Notes

### Code Quality

- ✅ Comprehensive JavaDoc comments
- ✅ Data structure justifications documented
- ✅ Clean code principles applied
- ✅ Proper error handling
- ✅ OOP best practices followed

### Testing

- Manual testing performed for all features
- User flow validation completed
- Data structure efficiency verified
- UI responsiveness confirmed

## 🎓 Educational Value

This project demonstrates:

- Practical application of data structures
- JavaFX GUI development
- MVC architecture implementation
- Real-time data processing
- User authentication systems
- Performance tracking algorithms
- Scene navigation patterns

## 📄 License

This project is created for educational purposes as part of a Data Structures course project.

## 👥 Credits

Developed as a comprehensive demonstration of:
- Java programming
- JavaFX GUI development
- Data structure implementation
- Object-oriented design principles

---

**Enjoy improving your typing skills! 🎯⌨️**
#   T y p i n g - G a m e  
 