# Typing Game — Project Documentation

## Overview

Typing Game is a JavaFX application that helps users practice and improve typing speed and accuracy. It supports user authentication (admin and player roles), multiple game modes (timed and free practice), performance tracking (WPM, accuracy, errors), score management and leaderboards, session history, and an admin panel for management.

## Project Structure

- `pom.xml` — Maven build file and dependencies
- `src/main/java/com/typinggame/` — application source code
  - `controller/` — JavaFX controllers for screens (login, menu, game, practice, results, admin, history)
  - `model/` — domain models (`User`, `GameSession`, `Score`, `Role`, `GameMode`)
  - `service/` — business logic and data-management services (`GameService`, `UserService`, `ScoreManager`, `PerformanceTracker`)
  - `util/` — utility classes (notably `SceneManager`)
- `src/test/java/com/typinggame/` — test runner and tests

See the codebase for implementation details.

## How to build and run

Prerequisites: Java 11+ (JDK matching the project), Maven

Build and run (from repository root):

```powershell
mvn clean javafx:run
```

## Architecture and High-Level Design

- Layered design separating UI (controllers), services (business logic), and models (domain objects).
- `Main` bootstraps services and controllers, registers scenes with `SceneManager`, and starts the JavaFX lifecycle. See [src/main/java/com/typinggame/Main.java](src/main/java/com/typinggame/Main.java).
- Controllers delegate heavy logic to services; controllers manage UI-only state.

## Key Classes and Responsibilities

- **Main** — Application lifecycle, service/controller initialization, scene registration and navigation triggers. See [src/main/java/com/typinggame/Main.java](src/main/java/com/typinggame/Main.java).

- **User** (`src/main/java/com/typinggame/model/User.java`) — stores credentials, `Role`, and `sessionHistory` (uses `ArrayList<GameSession>`). Provides authentication, history aggregation, and summary statistics (average WPM/accuracy, best WPM).

- **GameSession** (`src/main/java/com/typinggame/model/GameSession.java`) — immutable-ish record of a completed session: timestamp, mode, duration, total/correct characters, error count, WPM and accuracy calculators, and helper display methods.

- **Score** (`src/main/java/com/typinggame/model/Score.java`) — leaderboard entry implementing `Comparable<Score>` for sorting by WPM, accuracy, and date; includes a `fromGameSession` factory method.

- **GameService** (`src/main/java/com/typinggame/service/GameService.java`) — loads and serves typing content. Core data structures: `Queue<String>` (implemented as `LinkedList`) for FIFO delivery of words/sentences. Provides validation and accuracy helpers.

- **SceneManager** (`src/main/java/com/typinggame/util/SceneManager.java`) — manages JavaFX `Scene` objects, navigation, and validation using a graph-based approach (adjacency list). Uses:
  - `Map<String, List<String>>` for navigation graph (adjacency list)
  - `Map<String, Scene>` to store scenes
  - `Stack<String>` as navigation history for back navigation
  - `Map<String, Object>` for per-scene navigation data

- **Controllers** (`controller/*.java`) — UI controllers that call services, update views, and keep UI logic thin. Examples: `LoginController`, `MenuController`, `GameController`, `PracticeController`, `ResultsController`, `AdminController`, `HistoryController`.

- **Services** — `UserService`, `ScoreManager`, and `PerformanceTracker` encapsulate user management, ranking, scoring, and temporary in-game performance tracking.

## Applied Data Structures

The project explicitly documents and uses several standard data structures chosen for their respective problem fit:

- `HashMap` / `Map` (likely in `UserService`/`ScoreManager`): O(1) lookups for user authentication and quick access by key.
- `ArrayList<GameSession>` (`User.sessionHistory`): dynamic, ordered storage of session history with efficient iteration.
- `Queue<String>` implemented as `LinkedList` (`GameService.wordQueue`): FIFO delivery of words/sentences during gameplay; O(1) enqueue/dequeue.
- `Stack<String>` (`SceneManager.navigationHistory`): LIFO back navigation for scenes.
- `TreeMap` (referenced in `Main` and `ScoreManager`): automatically-sorted map suitable for ranking by keys (or could be used to group scores by composite keys) and retrieving ordered leaderboards.
- `Graph (Adjacency List)` represented as `Map<String, List<String>>` (`SceneManager.navigationGraph`): models allowed scene transitions and validates navigation.

Notes: concrete usage and variable names can be found in the source. Examples:
- `SceneManager` navigation graph: [src/main/java/com/typinggame/util/SceneManager.java](src/main/java/com/typinggame/util/SceneManager.java)
- `GameService` queue and lists: [src/main/java/com/typinggame/service/GameService.java](src/main/java/com/typinggame/service/GameService.java)

## OOP Principles Applied

- **Encapsulation**: Fields are private with getters/setters or carefully exposed copies (`User.getSessionHistory()` returns a copy). Most classes encapsulate internal state and expose behavior through methods.
- **Single Responsibility Principle (SRP)**: Classes have focused responsibilities — models hold data, services own business logic, controllers manage UI interactions, `SceneManager` handles navigation.
- **Separation of Concerns**: UI, business logic, and data models are separated into different packages.
- **Polymorphism & Interfaces**: `SceneNavigationListener` in `SceneManager` is a functional interface used as a callback; `Score` implements `Comparable<Score>` for ordering behavior via polymorphism.
- **Composition over Inheritance**: Services and controllers are composed into `Main` and wired together; models are composed of primitive fields and other models (e.g., `Score.fromGameSession`).
- **Immutability where appropriate**: `GameSession` values are effectively fixed after creation (calculations performed in constructor) to represent an atomic historical record.

## Algorithms and Complexity Notes

- Word delivery via `Queue` supports O(1) enqueue/dequeue operations.
- Authentication via `HashMap` supports O(1) lookup per login attempt.
- Leaderboard sorting uses `Comparable<Score>` enabling O(n log n) sorts when computing rankings.
- Scene navigation validation uses adjacency list lookups (O(1) to fetch neighbors + O(k) to check membership where k is neighbor count).

## Testing

- A test runner exists at `src/test/java/com/typinggame/TestRunner.java` — run unit/integration tests via Maven surefire if tests are defined.

## Default Accounts (from `Main` welcome message)

- Admin: `username='admin'`  `password='admin123'`
- Player examples: `player1`/`pass123`, `player2`/`pass123`

## Security and Production Notes

- Passwords in the code are plaintext for demo purposes. In production, ALWAYS store hashed passwords (e.g., bcrypt) and never log them.
- Persisting data: the current design appears to keep data in-memory (services and collections). For persistence, add a data access layer with a database (SQLite, H2, or a full RDBMS) and migrate in-memory collections to repositories.

## Extensibility and Improvement Ideas

- Persist user accounts, sessions, and scores to a database and add migration scripts.
- Add unit tests covering services (`GameService`, `ScoreManager`, `UserService`) and controllers with UI test automation if desired.
- Internationalization/localization of UI strings.
- Add configuration-driven content loading (external word lists, JSON or DB).
- Replace plaintext passwords with hashed storage and add password-reset flows.
- Add analytics export (CSV) and advanced leaderboards with pagination.

## File References

- `Main` — [src/main/java/com/typinggame/Main.java](src/main/java/com/typinggame/Main.java)
- `SceneManager` — [src/main/java/com/typinggame/util/SceneManager.java](src/main/java/com/typinggame/util/SceneManager.java)
- `GameService` — [src/main/java/com/typinggame/service/GameService.java](src/main/java/com/typinggame/service/GameService.java)
- `User` — [src/main/java/com/typinggame/model/User.java](src/main/java/com/typinggame/model/User.java)
- `GameSession` — [src/main/java/com/typinggame/model/GameSession.java](src/main/java/com/typinggame/model/GameSession.java)
- `Score` — [src/main/java/com/typinggame/model/Score.java](src/main/java/com/typinggame/model/Score.java)

## Contact & Development Notes

If you'd like, I can:

- Generate UML class diagrams from the source
- Add README badges and a contributor guide
- Create a small migration to persist scores to `scores.json` or an embedded DB

---

Generated on: 2025-12-18
