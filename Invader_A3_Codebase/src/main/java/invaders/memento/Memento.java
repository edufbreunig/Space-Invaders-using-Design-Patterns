package invaders.memento;

import invaders.entities.Player;
import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.ArrayList;
import java.util.List;

import invaders.gameobject.GameObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a memento in the memento design pattern.
 * The memento captures and externalizes an object's internal state so that
 * the object can later be restored to this state.
 * <p>
 * This memento captures the game state including score, time,
 * list of game objects, list of renderables, and player state.
 *
 */

public class Memento {
    private int score;
    private int time;
    private List<GameObject> gameObjectsState;
    private List<Renderable> renderablesState;
    private Player player = null;

    public Memento(int score, int time, List<GameObject> gameObjectsState, List<Renderable> renderablesState, Player player) {
        this.score = score;
        this.time = time;
        this.gameObjectsState = gameObjectsState;
        this.renderablesState = renderablesState;
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public int getTime() {
        return time;
    }

    public List<GameObject> getGameObjectsState() {
        return gameObjectsState;
    }

    public List<Renderable> getRenderablesState() {
        return renderablesState;
    }
    public Player getPlayer(){
        return player;
    }
}