package invaders.engine;

import java.util.*;
import java.util.List;

import invaders.ConfigReader;
import invaders.builder.BunkerBuilder;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.factory.EnemyProjectile;
import invaders.factory.PlayerProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.entities.Player;
import invaders.memento.Caretaker;
import invaders.rendering.Renderable;
import invaders.observer.Subject;
import invaders.observer.Observer;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import org.json.simple.JSONObject;
import invaders.memento.Memento;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine implements Subject {
	private List<GameObject> gameObjects = new ArrayList<>(); // A list of game objects that gets updated each frame
	private List<GameObject> pendingToAddGameObject = new ArrayList<>();
	private List<GameObject> pendingToRemoveGameObject = new ArrayList<>();

	private List<Renderable> pendingToAddRenderable = new ArrayList<>();
	private List<Renderable> pendingToRemoveRenderable = new ArrayList<>();

	private List<EnemyProjectile> projectilesToRemove = new ArrayList<>();
	private List<Enemy> enemiesToRemove = new ArrayList<>();

	private List<Renderable> renderables =  new ArrayList<>();

	private Player player;

	private boolean left;
	private boolean right;
	private int gameWidth;
	private int gameHeight;
	private int timer = 45;

	private List<Observer> observers = new ArrayList<>();// keeps a list of Observes that are observing GameEngine
	private int score = 0;
	private int time = 0;
	private final int FAST_PROJECTILE_SCORE = 2;
	private final int SLOW_ALIEN_SCORE = 3;
	private final int FAST_ALIEN_SCORE = 4;
	private Caretaker caretaker = new Caretaker();
	private boolean restored; //Flag so GameWindow knows when game has been restored


	public GameEngine(String config){
		// Read the config here
		ConfigReader.parse(config);

		// Get game width and height
		gameWidth = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("x")).intValue();
		gameHeight = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("y")).intValue();

		//Get player info
		this.player = new Player(ConfigReader.getPlayerInfo());
		renderables.add(player);


		Director director = new Director();
		BunkerBuilder bunkerBuilder = new BunkerBuilder();
		//Get Bunkers info
		for(Object eachBunkerInfo:ConfigReader.getBunkersInfo()){
			Bunker bunker = director.constructBunker(bunkerBuilder, (JSONObject) eachBunkerInfo);
			gameObjects.add(bunker);
			renderables.add(bunker);
		}

		EnemyBuilder enemyBuilder = new EnemyBuilder();
		//Get Enemy info
		for(Object eachEnemyInfo:ConfigReader.getEnemiesInfo()){
			Enemy enemy = director.constructEnemy(this,enemyBuilder,(JSONObject)eachEnemyInfo);
			gameObjects.add(enemy);
			renderables.add(enemy);
		}

	}

	/**
	 * Updates the game/simulation
	 */
	public void update(){

		if(!isGameOver() && !isGameWon()){
			timer+=1;
			if (timer % 120 == 0){
				time++;
			}
		}

		movePlayer();

		for(GameObject go: gameObjects){
			go.update(this);
		}

		for (int i = 0; i < renderables.size(); i++) {
			Renderable renderableA = renderables.get(i);
			for (int j = i+1; j < renderables.size(); j++) {
				Renderable renderableB = renderables.get(j);

				if((renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))
						||(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("Enemy"))||
						(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))){
				}else{
					if(renderableA.isColliding(renderableB) && (renderableA.getHealth()>0 && renderableB.getHealth()>0)) {
						renderableA.takeDamage(1);
						renderableB.takeDamage(1);

						if (renderableA instanceof EnemyProjectile && renderableB instanceof PlayerProjectile) {
							((EnemyProjectile) renderableA).awardScore(this);// award score based on type of EnemyProjectile
						}
						if (renderableB instanceof EnemyProjectile && renderableA instanceof EnemyProjectile) {
							((EnemyProjectile) renderableB).awardScore(this);// award score based on type of EnemyProjectile
						}
					}
				}
			}
		}


		// ensure that renderable foreground objects don't go off-screen
		int offset = 1;
		for(Renderable ro: renderables){
			if(!ro.getLayer().equals(Renderable.Layer.FOREGROUND)){
				continue;
			}
			if(ro.getPosition().getX() + ro.getWidth() >= gameWidth) {
				ro.getPosition().setX((gameWidth - offset) -ro.getWidth());
			}

			if(ro.getPosition().getX() <= 0) {
				ro.getPosition().setX(offset);
			}

			if(ro.getPosition().getY() + ro.getHeight() >= gameHeight) {
				ro.getPosition().setY((gameHeight - offset) -ro.getHeight());
			}

			if(ro.getPosition().getY() <= 0) {
				ro.getPosition().setY(offset);
			}
		}

		notifyObservers();// notify observers

	}

	public List<Renderable> getRenderables(){
		return renderables;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	public List<GameObject> getPendingToAddGameObject() {
		return pendingToAddGameObject;
	}

	public List<GameObject> getPendingToRemoveGameObject() {
		return pendingToRemoveGameObject;
	}

	public List<Renderable> getPendingToAddRenderable() {
		return pendingToAddRenderable;
	}

	public List<Renderable> getPendingToRemoveRenderable() {
		return pendingToRemoveRenderable;
	}


	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased(){
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}
	public void rightPressed(){
		this.right = true;
	}

	public boolean shootPressed(){
		if(timer>45 && player.isAlive()){
			Projectile projectile = player.shoot();
			gameObjects.add(projectile);
			renderables.add(projectile);
			timer=0;
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}

		if(right){
			player.right();
		}
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public void registerObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}


	/**
	 * Notifies observers. Updates their information
	 *
	 */
	@Override
	public void notifyObservers() {
		for (Observer o : observers) {
			o.update(time, score);
		}
	}

	/**
	 * Increases the current game score by a specified value.
	 *
	 * @param value The amount by which to increase the score.
	 */
	public void increaseScore(int value){
		this.score += value;
	}
	/**
	 * Saves the current state of the game, including game objects, renderables,
	 * player, score, and time, using the Memento pattern.
	 */

	public void saveState() {

		List<GameObject> clonedGameObjects = new ArrayList<>();
		List<Renderable> clonedRenderables = new ArrayList<>();
		Player clonedPlayer = null;

		//clone every GameObject and store in clonedGameObjects
		for(GameObject go: gameObjects){
			if(go instanceof Player){
				clonedPlayer = (Player) go.clone();
			}
			if(go instanceof Enemy){
				Enemy clonedEnemy = (Enemy) go.clone();
				clonedGameObjects.add(clonedEnemy);
			}
			if(go instanceof PlayerProjectile){
				clonedGameObjects.add((PlayerProjectile) go.clone());
			}
			if(go instanceof EnemyProjectile){
				clonedGameObjects.add((EnemyProjectile) go.clone());
			}
			if(go instanceof Bunker){
				clonedGameObjects.add((Bunker) go.clone());
			}
		}

		//clone every Renderables and store in renderables
		for(Renderable r: renderables){
			if(r instanceof Player){
				clonedPlayer = (Player) r.clone();
			}
			if(r instanceof Enemy){
				Enemy clonedEnemy = (Enemy) r.clone();
				clonedRenderables.add(clonedEnemy);
			}
			if(r instanceof PlayerProjectile){
				clonedRenderables.add((PlayerProjectile) r.clone());
			}
			if(r instanceof Bunker){
				clonedRenderables.add((Bunker) r.clone());
			}
			if(r instanceof EnemyProjectile){
				clonedRenderables.add((EnemyProjectile) r.clone());
			}

		}

		Memento memento = new Memento(score, time, clonedGameObjects, clonedRenderables, clonedPlayer);
		caretaker.saveState(memento);//save memento in caretaker
	}

	/**
	 * Restores the game state to the last saved state using the Memento pattern.
	 */
	public void restoreState(){

		Memento memento = caretaker.getLastSavedState();

		if (memento == null) {
			System.out.println("Memento is null");
			return;
		}
		if(memento.getRenderablesState().size() <= 0){
			System.out.println("Renderables is empty");
			return;
		}


		pendingToAddGameObject = new ArrayList<>(memento.getGameObjectsState());
		pendingToAddRenderable = new ArrayList<>(memento.getRenderablesState());

		score = memento.getScore();
		time = memento.getTime();
		player = memento.getPlayer();

		restored = true;// game is restored

	}
	/**
	 * Checks if the game state has been restored recently.
	 *
	 * @return True if the game state has been restored, false otherwise.
	 */
	public boolean getRestored(){
		return this.restored;
	}
	public void setRestored(boolean value){
		this.restored = value;
	}
	/**
	 * Removes all enemy projectiles with a slow strategy from the game,
	 * increasing the score in the process.
	 */
	public void cheatRemoveSlowProjectiles(){
		int count = 0;

		for(GameObject go : gameObjects){
			if(go instanceof EnemyProjectile && ((EnemyProjectile) go).getStrategy() instanceof SlowProjectileStrategy){
				projectilesToRemove.add((EnemyProjectile) go);

				for(GameObject enemyObj : gameObjects){
					if(enemyObj instanceof Enemy){
						Enemy enemy = (Enemy) enemyObj;
						if(enemy.getEnemyProjectile().contains(go)){
							enemy.getEnemyProjectile().remove(go);
							break;

						}
					}
				}

				pendingToRemoveGameObject.add(go);
				pendingToRemoveRenderable.add((Renderable) go);
				count++;
			}
		}
		increaseScore(count);
	}
	/**
	 * Removes all enemy projectiles without a slow strategy (i.e., fast projectiles)
	 * from the game, increasing the score in the process.
	 */
	public void cheatRemoveFastProjectiles() {
		int count = 0;

		for (GameObject go : gameObjects) {
			if (go instanceof EnemyProjectile && !(((EnemyProjectile) go).getStrategy() instanceof SlowProjectileStrategy)) {
				projectilesToRemove.add((EnemyProjectile) go);

				for (GameObject enemyObj : gameObjects) {
					if (enemyObj instanceof Enemy) {
						Enemy enemy = (Enemy) enemyObj;
						if (enemy.getEnemyProjectile().contains(go)) {
							enemy.getEnemyProjectile().remove(go);
							break;
						}
					}
				}

				pendingToRemoveGameObject.add(go);
				pendingToRemoveRenderable.add((Renderable) go);
				count++;
			}
		}
		increaseScore(count * FAST_PROJECTILE_SCORE);

	}

	/**
	 * Removes all enemies with a slow projectile strategy from the game,
	 * increasing the score in the process.
	 */
	public void cheatSlowEnemy(){
		int count = 0;

		for(GameObject go : gameObjects){
			if(go instanceof Enemy && ((Enemy) go).getStrategy() instanceof SlowProjectileStrategy){
				pendingToRemoveGameObject.add(go);
				pendingToRemoveRenderable.add((Renderable) go);
				enemiesToRemove.add((Enemy) go);
				count++;
			}
		}
		increaseScore(count * SLOW_ALIEN_SCORE);
	}
	/**
	 * Removes all enemies with a fast projectile strategy from the game,
	 * increasing the score in the process.
	 */
	public void cheatFastEnemy(){
		int count = 0;

		for(GameObject go : gameObjects){
			if(go instanceof Enemy && ((Enemy) go).getStrategy() instanceof FastProjectileStrategy){
				pendingToRemoveGameObject.add(go);
				pendingToRemoveRenderable.add((Renderable) go);
				enemiesToRemove.add((Enemy) go);
				count++;
			}
		}
		increaseScore(count * FAST_ALIEN_SCORE);
	}

	/**
	 * Retrieves a list of enemy projectiles that are marked for removal.
	 *
	 * @return A list of enemy projectiles that are to be removed.
	 */
	public List<EnemyProjectile> getProjectilesToRemove() {return projectilesToRemove;}

	/**
	 * Retrieves a list of enemies that are marked for removal.
	 *
	 * @return A list of enemies that are to be removed.
	 */
	public List<Enemy> getEnemiesToRemove() {return enemiesToRemove;}

	/**
	 * Determines if the game is over based on the player's state.
	 *
	 * @return True if the player is no longer alive, indicating the game is over.
	 */
	public boolean isGameOver() {
		return !player.isAlive();
	}


	/**
	 * Checks if the player has won the game based on the presence of enemies.
	 *
	 * @return True if no enemies remain, indicating the game has been won.
	 */
	public boolean isGameWon() {
		for (GameObject go : gameObjects) {
			if (go instanceof Enemy) {
				return false;
			}
		}
		return true;
	}

}

