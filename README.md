# Game Project README

## How to Run the Game
To build and run the game, use the following Gradle command:
```sh
gradle clean build run
```

## Implemented Features
- **Difficulty Level Selection**
- **Time and Score Tracking**
- **Undo and Cheat Mechanisms**

## Design Patterns Used
### 1. Singleton Pattern
- `DifficultyManager` (Singleton)
- `GameWindow` (Singleton Client)

### 2. Observer Pattern
- `Observer` (Observer)
- `ScoreBoard` (ConcreteObserver)
- `Subject` (Subject)
- `GameEngine` (ConcreteSubject)

### 3. Memento Pattern
- `Memento` (Memento)
- `Caretaker` (Caretaker)
- `GameEngine` (Originator)

## Game Instructions
### Undo Feature
- The game state is saved every time the player shoots.
- Press **'U'** to undo the last shot.

### Cheat Codes
- **Delete slow projectiles:** Press **'1'**
- **Delete fast projectiles:** Press **'2'**
- **Delete slow enemies:** Press **'3'**
- **Delete fast enemies:** Press **'4'**

### Difficulty Level Selection
- Select the difficulty level at the beginning of the game.
- You can also change the difficulty level during the game by pressing buttons on the game window.

## Additional Notes
- Ensure all dependencies are installed before running the game.
- For troubleshooting, refer to the project documentation or logs.
