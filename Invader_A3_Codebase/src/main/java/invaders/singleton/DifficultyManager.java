package invaders.singleton;

import invaders.engine.GameEngine;


/**
 * Represents a singleton manager for handling game difficulty settings.
 * <p>
 * This manager provides a centralized location for managing the game's
 * difficulty settings and is implemented as a singleton to ensure there's only
 * one instance throughout the application.
 * </p>
 *
 */

public class DifficultyManager {

    private static DifficultyManager instance = null;
    private String difficulty = "easy";// easy is the default
    private GameEngine engine = null;

    private DifficultyManager(){}

    public static DifficultyManager getInstance() {
        if (instance == null) {
            instance = new DifficultyManager();
        }
        return instance;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setGameEngine(GameEngine engine) {
        this.engine = engine;
    }

    public GameEngine getGameEngine() {
        return engine;
    }
}

