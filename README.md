# TetrisJFX — Coursework 2025

**Student Name:** Muhammad Aatik Shaikh  
**Student ID:** 20607620  
**GitHub Repository:** https://github.com/Aatik-Shaikh/CW2025

---

## 1. Compilation & Execution Instructions

This project uses the Maven Wrapper, allowing it to build and run without installing Maven system-wide.  
You may run the game either through IntelliJ IDEA (recommended) or via terminal.

---

### Option A: Running via IntelliJ IDEA (Recommended)

Requirements and dependencies:
- Java 21+
- JavaFX 21.0.6
- JUnit 5.12.1
-
1. Open the Project
    - Launch IntelliJ IDEA and open the folder named `DMS Final` (or the root project folder).
    - Allow IntelliJ to automatically import all Maven dependencies.

2. Clean & Install Dependencies
    - Open the Maven tool window.
    - Navigate to Lifecycle.
        - Double-click `clean` to remove previous builds.
        - Double-click `install` to build the project and fetch all dependencies.

3. Run the Game
    - Go to Plugins → javafx in the Maven window.
    - Double-click `javafx:run` to launch the Tetris application.

Compilation Target: The Maven build is configured for Java 23.

---

## 2. Features Fully Implemented & Working Correctly

### Complete MVC Refactor
The original monolithic codebase has been restructured into a clean Model–View–Controller design.

### Hold Piece Mechanic
Press C to store the current piece. Integrated in state and UI.

### Start Menu & Difficulty Selection
Includes high score viewing and starting level selection.

### High Score Persistence
Top 3 scores saved in `highscores.txt` with sorting and persistence.

### Dynamic Leveling & Game Speed
Level increases every 10 lines, speed increases 20% per level.

### Combo Scoring System
Five consecutive lines triggers a 1.5× score multiplier.

### Pause & Game Over Overlays
Modern overlay screens. Pause works via P or Esc.

### Unit Testing
Covers board initialization, collision, rotations, and controlled piece generation.

---

## 3. Features Implemented but Initially Buggy

### Hold Piece Swap Exploit
Fixed using `canHold` flag restricting swaps to one per turn.

### Level Not Updating
Fixed by reapplying:
```java
level.set(GameConfig.getStartLevel());
```

---

## 4. Features Not Implemented

### Sound Effects & Music
Prepared for future implementation.

### Online Leaderboard
Not included to avoid dependency on external services.

---

## 5. New Java Classes Added

The following classes were introduced to extend functionality, enable testing, and improve architecture:

| Class                    | Location                      | Purpose                                                                                                 |
|:-------------------------|:------------------------------|:--------------------------------------------------------------------------------------------------------|
| **BrickFactory**         | `com.comp2042.logic.bricks`   | Factory Pattern: Creates Brick instances based on ID, decoupling the generator from concrete classes.   |
| **HighScoreManager**     | `com.comp2042`                | Handles reading, writing, sorting, and limiting the top 3 scores stored in `highscores.txt`.            |
| **MenuController**       | `com.comp2042.controller`     | Manages Start Menu logic, level selection, and scene transitions to the main game.                      |
| **GameConfig**           | `com.comp2042`                | Central configuration store containing grid size, fall speed, colors, and starting-level settings.      |
| **ResourceLoader**       | `com.comp2042.util`           | Utility class for safe loading of fonts, images, and resources to prevent runtime file errors.          |
| **StubBrickGenerator**   | `com.comp2042.testhelpers`    | Test stub providing predictable brick sequences for deterministic JUnit testing.                        |
| **SimpleBoardTest**      | `src/test/java/...`           | Unit tests verifying board initialization, movement bounds, and collision behavior.                     |
| **BrickRotatorTest**     | `src/test/java/...`           | Tests verifying brick rotation logic and correct cycling of rotation states.                            |
| **MatrixOperationsTest** | `src/test/java/...`           | Tests validating collision math and line-clearing logic.                                                |


## 6. Modified Java Classes

Significant changes were made to the original codebase to support maintenance, refactoring, and extension:

| Class                        | Location                    | Modifications & Rationale                                                                                                                                                 |
|:-----------------------------|:----------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`SimpleBoard`**            | `com.comp2042.model`        | Moved from root package. Implemented `holdBrick()` logic and `canHold` state. Added constructor for Dependency Injection to support testing. Refactored collision checks. |
| **`GuiController`**          | `com.comp2042.controller`   | Moved from root package. Stripped of game logic to focus purely on View updates. Added `holdPiecePanel`, `startCountdown`, and fixed the spacebar focus bug.              |
| **`GameController`**         | `com.comp2042.controller`   | Moved from root package. Refactored to act as the primary MVC Controller. Coordinates between Model (`SimpleBoard`) and View (`GuiController`). Handles the game loop.    |
| **`Score`**                  | `com.comp2042.model`        | Moved from root package. Added `consecutiveLines` field and `processLineClear()` method for Combo Scoring (1.5x multiplier). Fixed configuration access.                  |
| **`RandomBrickGenerator`**   | `com.comp2042.logic.bricks` | Refactored to use `BrickFactory` for object creation (Factory Pattern). Implemented a `Deque` system to manage the "Next Piece" preview buffer.                           |
| **`Main`**                   | `com.comp2042`              | Modified `start()` to initialize `ResourceLoader` and load `startMenu.fxml` first instead of jumping directly into the game.                                              |
| **`GameOverPanel`**          | `com.comp2042`              | Completely rewrote class from a simple label to a full interactive screen with final score, high score list, and navigation buttons.                                      |
| **`MatrixOperations`**       | `com.comp2042`              | Added Javadoc documentation. Reinforced as a stateless utility (private constructor) to ensure thread safety for collision checks.                                        |
| **`ViewData`**               | `com.comp2042.model`        | Moved from root package. Added `holdBrickData` field to allow the GUI to render the held piece without accessing the Board directly.                                      |
| **`BrickRotator`**           | `com.comp2042.model`        | Relocated from root package to `model` to better organize state management classes.                                                                                       |
| **`ClearRow`**               | `com.comp2042.model`        | Relocated from root package to `model` as it serves as a data carrier for board state changes.                                                                            |
| **`DownData`**               | `com.comp2042.model`        | Relocated from root package to `model` as it encapsulates the result of movement actions.                                                                                 |
| **`NextShapeInfo`**          | `com.comp2042.model`        | Relocated from root package to `model` as it carries state information for rotation.                                                                                      |
| **`Board`** (Interface)      | `com.comp2042.model`        | Relocated from root package to `model`. Defines the contract for the game board model.                                                                                    |
| **`InputEventListener`**     | `com.comp2042.events`       | Relocated from root package to `events`. Separates event handling interfaces from core logic.                                                                             |
| **`MoveEvent`**              | `com.comp2042.events`       | Relocated from root package to `events`. Groups event-related data classes.                                                                                               |
| **`EventType`**              | `com.comp2042.events`       | Relocated from root package to `events` for better organization of enums.                                                                                                 |
| **`EventSource`**            | `com.comp2042.events`       | Relocated from root package to `events` for better organization of enums.                                                                                                 |
| **`Brick`** (Interface)      | `com.comp2042.logic.bricks` | Changed visibility to `public` to allow access from the new `BrickFactory` and unit tests in different packages.                                                          |
| **`IBrick`, `JBrick`, etc.** | `com.comp2042.logic.bricks` | Changed visibility to `public` to support the Factory pattern (instantiation from a different package) and enable unit testing.                                           |


---

## 7. Unexpected Challenges & Resolutions

### Spacebar Focus Trap
Issue: Spacebar triggered UI buttons.  
Fix: Disabled focus traversal and forced focus to game panel.

### Ghost Piece Rendering Errors
Issue: Incorrect ghost placement near walls.  
Fix: Improved collision validation.

### Font Loading Failures
Issue: Fonts not loading in JAR.  
Fix: Implemented `ResourceLoader` for safe resource streaming.

---

## Final Notes

This project demonstrates a full architectural transformation with emphasis on:
- Clean code
- Extensibility
- Testability
- Gameplay quality

Future extensions (audio, online leaderboard, themes) are supported by the updated architecture.

