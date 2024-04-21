package invaders.gameobject;

import invaders.engine.GameEngine;
import invaders.Cloneable.Cloneable;

// contains basic methods that all GameObjects must implement
public interface GameObject extends Cloneable {

    public void start();
    public void update(GameEngine model);

}
