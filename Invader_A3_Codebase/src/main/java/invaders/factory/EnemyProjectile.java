package invaders.factory;

import invaders.engine.GameEngine;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.physics.Collider;
import invaders.physics.Vector2D;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.ProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import javafx.scene.image.Image;

import java.io.File;

public class EnemyProjectile extends Projectile{
    private ProjectileStrategy strategy;
    private boolean awardedScore = false;
    private final int SLOW_PROJECTILE_SCORE = 1;
    private final int FAST_PROJECTILE_SCORE = 2;

    public EnemyProjectile(Vector2D position, ProjectileStrategy strategy, Image image) {
        super(position,image);
        this.strategy = strategy;
    }

    public EnemyProjectile(EnemyProjectile p) {
        super(new Vector2D(p.getPosition().getX(), p.getPosition().getY()), new Image(p.getImage().getUrl()));


        if (p.strategy instanceof SlowProjectileStrategy){
            this.strategy = new SlowProjectileStrategy();
        }
        else if (p.strategy instanceof FastProjectileStrategy) {
            this.strategy = new FastProjectileStrategy();
        }
        else {
            this.strategy = p.strategy;
        }

        this.awardedScore = p.awardedScore;
    }


    @Override
    public void update(GameEngine model) {
        strategy.update(this);

        if (this.getPosition().getY() >= model.getGameHeight() - this.getImage().getHeight()) {
            this.takeDamage(1);
        }

    }
    @Override
    public String getRenderableObjectName() {
        return "EnemyProjectile";
    }

    public void awardScore(GameEngine model){
        if (!awardedScore) {
            if(this.strategy instanceof SlowProjectileStrategy){
                model.increaseScore(SLOW_PROJECTILE_SCORE);
            } else if(this.strategy instanceof FastProjectileStrategy){
                model.increaseScore(FAST_PROJECTILE_SCORE);
            }
            this.awardedScore = true;
        }
    }

    public ProjectileStrategy getStrategy(){
        return strategy;
    }

    public EnemyProjectile clone(){
        return new EnemyProjectile(this);
    }

}
