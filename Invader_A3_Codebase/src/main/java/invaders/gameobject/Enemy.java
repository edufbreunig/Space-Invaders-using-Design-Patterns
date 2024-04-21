package invaders.gameobject;

import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectile;
import invaders.factory.EnemyProjectileFactory;
import invaders.factory.Projectile;
import invaders.factory.ProjectileFactory;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.ProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Enemy implements GameObject, Renderable {
    private Vector2D position;
    private int lives = 12;
    private Image image;
    private int xVel = -1;
    private boolean scoreAwarded = false;
    private ArrayList<Projectile> enemyProjectile;
    private ArrayList<Projectile> pendingToDeleteEnemyProjectile;
    private ProjectileStrategy projectileStrategy;
    private ProjectileFactory projectileFactory;
    private Image projectileImage;
    private Random random = new Random();

    private final int SLOW_ALIEN_SCORE = 3;
    private final int FAST_ALIEN_SCORE = 4;


    public Enemy(Vector2D position) {
        this.position = position;
        this.projectileFactory = new EnemyProjectileFactory();
        this.enemyProjectile = new ArrayList<>();
        this.pendingToDeleteEnemyProjectile = new ArrayList<>();
    }

    public Enemy(Enemy enemy) {
        this.position = new Vector2D(enemy.position.getX(), enemy.position.getY());
        this.lives = enemy.lives;

        if(enemy.projectileStrategy instanceof SlowProjectileStrategy){
            this.projectileStrategy = new SlowProjectileStrategy();
            this.image = new Image(new File("src/main/resources/slow_alien.png").toURI().toString(), 20, 20, true, true);
        }
        if(enemy.projectileStrategy instanceof FastProjectileStrategy){
            this.projectileStrategy = new  FastProjectileStrategy();
            this.image = new Image(new File("src/main/resources/fast_alien.png").toURI().toString(), 20, 20, true, true);
        }

        this.xVel = enemy.xVel;
        this.scoreAwarded = enemy.scoreAwarded;

        this.enemyProjectile = new ArrayList<>();
        for (Projectile p : this.enemyProjectile) {
            Projectile newProjectile = null;
            if (p instanceof EnemyProjectile) {
                newProjectile = new EnemyProjectile((EnemyProjectile) p);
            }

            if (newProjectile != null) {
                this.enemyProjectile.add(newProjectile);
            }
        }

        this.pendingToDeleteEnemyProjectile = new ArrayList<>();
        for (Projectile p : this.pendingToDeleteEnemyProjectile) {
            Projectile newProjectile = null;
            if (p instanceof EnemyProjectile) {
                newProjectile = new EnemyProjectile((EnemyProjectile) p);
            }
            if (newProjectile != null) {
                this.pendingToDeleteEnemyProjectile.add(newProjectile);
            }
        }

        this.projectileFactory = new EnemyProjectileFactory();

        this.projectileImage = new Image(String.valueOf(new File(enemy.projectileImage.getUrl().toString())), 20, 20, true, true);

    }


    @Override
    public void start() {
    }

    @Override
    public void update(GameEngine engine) {

        if (enemyProjectile.size() < 3){

            if (this.isAlive() && random.nextInt(120) == 20) {
                Projectile p = projectileFactory.createProjectile(new Vector2D(position.getX() + this.image.getWidth() / 2, position.getY() + image.getHeight() + 2), projectileStrategy, projectileImage);
                enemyProjectile.add(p);
                engine.getPendingToAddGameObject().add(p);
                engine.getPendingToAddRenderable().add(p);
            }
        } else {
            pendingToDeleteEnemyProjectile.clear();
            for (Projectile p : enemyProjectile) {
                if (!p.isAlive()) {
                    engine.getPendingToRemoveGameObject().add(p);
                    engine.getPendingToRemoveRenderable().add(p);
                    pendingToDeleteEnemyProjectile.add(p);
                }
            }

            for (Projectile p : pendingToDeleteEnemyProjectile) {
                enemyProjectile.remove(p);
            }
        }

        if (this.position.getX() <= this.image.getWidth() || this.position.getX() >= (engine.getGameWidth() - this.image.getWidth() - 1)) {
            this.position.setY(this.position.getY() + 25);
            xVel *= -1;
        }

        this.position.setX(this.position.getX() + xVel);

        if ((this.position.getY() + this.image.getHeight()) >= engine.getPlayer().getPosition().getY()) {
            engine.getPlayer().takeDamage(Integer.MAX_VALUE);
        }

        if (!isAlive() && !this.scoreAwarded) {
            if (this.projectileStrategy instanceof SlowProjectileStrategy) {
                engine.increaseScore(SLOW_ALIEN_SCORE);//award score for slow aliens
                this.scoreAwarded = true;
            } else if (this.projectileStrategy instanceof FastProjectileStrategy) {
                engine.increaseScore(FAST_ALIEN_SCORE);//award score for fast aliens
                this.scoreAwarded = true;
            }
        }

    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return this.image.getWidth();
    }

    @Override
    public double getHeight() {
        return this.image.getHeight();
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setProjectileImage(Image projectileImage) {
        this.projectileImage = projectileImage;
    }

    @Override
    public void takeDamage(double amount) {
        this.lives -= 1;

    }

    @Override
    public double getHealth() {
        return this.lives;
    }

    @Override
    public String getRenderableObjectName() {
        return "Enemy";
    }

    @Override
    public boolean isAlive() {
        return this.lives > 0;
    }

    public void setProjectileStrategy(ProjectileStrategy projectileStrategy) {
        this.projectileStrategy = projectileStrategy;


    }

    @Override
    public Enemy clone() {
        return new Enemy(this);
    }

    public ArrayList<Projectile> getEnemyProjectile() {
        return this.enemyProjectile;
    }

    public ProjectileStrategy getStrategy(){
        return this.projectileStrategy;
    }
}


