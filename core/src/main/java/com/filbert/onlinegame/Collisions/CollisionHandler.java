package com.filbert.onlinegame.Collisions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.filbert.onlinegame.Constants;
import com.filbert.onlinegame.entities.Asteroid;
import com.filbert.onlinegame.entities.Bullet;
import com.filbert.onlinegame.screens.GameScreen;

import static com.filbert.onlinegame.util.Common.getFloatArrayFromPointsList;

public class CollisionHandler {
    public DelayedRemovalArray<Bullet> bulletGroup;
    public DelayedRemovalArray<Asteroid> asteroidGroup;
    public GameScreen game;
    public Sound asteroidExplosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
    public Sound playerExplosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/medium-explosion.mp3"));

    public CollisionHandler(DelayedRemovalArray<Bullet> bulletGroup, DelayedRemovalArray<Asteroid> asteroidGroup, GameScreen game) {
        this.bulletGroup = bulletGroup;
        this.asteroidGroup = asteroidGroup;
        this.game = game;
    }

    public void handleEvents() {
        if (game.playerAlive && game.player1 != null) {
            checkPlayerAsteroidCollision();
        }
        checkBulletAsteroidCollision();
    }

    public void checkPlayerAsteroidCollision() {
        for (Asteroid asteroid: asteroidGroup) {
            boolean isIntersecting = Intersector.intersectPolygons(
                getFloatArrayFromPointsList(asteroid.convexHullPoints),
                getFloatArrayFromPointsList(game.player1.shipVertices.subList(0, 4))
            );

            if (isIntersecting) {
                // player death logic
                game.playerDied();
                playerExplosionSound.play(1.0f);
                return;
            }
        }
    }

    public void checkBulletAsteroidCollision() {
        for (Bullet bullet: bulletGroup) {
            // check if the bullet has hit any of the asteroids
            for (Asteroid asteroid: asteroidGroup) {
                float[] convexHullPoints = asteroid.getConvexHullFloatArray();
                boolean isIntersecting = Intersector.isPointInPolygon(
                    convexHullPoints,
                    0,
                    convexHullPoints.length,
                    bullet.x,
                    bullet.y
                );
                if (isIntersecting) {
                    // handle hitting an asteroid with a bullet
                    Vector2 asteroidCenter = asteroid.getConvexHullCenter();
                    game.createParticleExplosion(asteroidCenter.x, asteroidCenter.y, 5, Constants.ASTEROID_OUTER_COLOR);
                    bulletGroup.removeValue(bullet, false);
                    game.bulletPool.free(bullet);

                    handleAsteroidCollision(asteroid);
                    asteroidGroup.removeValue(asteroid, false);
                    game.asteroidPool.free(asteroid);
                    asteroidExplosionSound.play(1.0f);

                    // update score
                    if (asteroid.size == 150) {
                        game.score += 20;
                    } else if (asteroid.size == 100) {
                        game.score += 50;
                    } else {
                        game.score += 100;
                    }
                    break;
                }
            }
        }
    }

    public void handleAsteroidCollision(Asteroid a) {
        if (a.size == 50) {
            return;
        }
        // create 2 new smaller asteroids
        Asteroid asteroid1 = game.asteroidPool.obtain();
        Asteroid asteroid2 = game.asteroidPool.obtain();
        if (a.size == 150) {
            asteroid1.init(a.x - 50, a.y - 50, (float) (a.velX * 2 * (a.random.nextFloat() + 0.5)), (float) (a.velY * 2 * (a.random.nextFloat() + 0.5)), 100);
            asteroid2.init(a.x - 50, a.y - 50, (float) (a.velX * 2 * (a.random.nextFloat() + 0.5)), (float) (a.velY * 2 * (a.random.nextFloat() + 0.5)), 100);
        } else if (a.size == 100) {
            asteroid1.init(a.x - 25, a.y - 25, (float) (a.velX * 2 * (a.random.nextFloat() + 0.5)), (float) (a.velY * 2 * (a.random.nextFloat() + 0.5)), 50);
            asteroid2.init(a.x - 25, a.y - 25, (float) (a.velX * 2 * (a.random.nextFloat() + 0.5)), (float) (a.velY * 2 * (a.random.nextFloat() + 0.5)), 50);
        }
        asteroidGroup.add(asteroid1);
        asteroidGroup.add(asteroid2);
    }
}
