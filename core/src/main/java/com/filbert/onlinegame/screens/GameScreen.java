package com.filbert.onlinegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pool;
import com.filbert.onlinegame.*;
import com.filbert.onlinegame.Collisions.CollisionHandler;
import com.filbert.onlinegame.entities.*;
import com.filbert.onlinegame.util.Fonts;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;
import java.util.Random;

import static com.filbert.onlinegame.Constants.BACKGROUND_COLOR;
import static com.filbert.onlinegame.util.Common.getRandFloat;

public class GameScreen implements Screen {
    final GameApp game;
    public Player player1;
    public Fonts fonts = new Fonts();
    public DelayedRemovalArray<Bullet> bulletGroup = new DelayedRemovalArray<>();
    // bullet pool.
    public final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet();
        }
    };
    public DelayedRemovalArray<Asteroid> asteroidGroup = new DelayedRemovalArray<>();
    public final Pool<Asteroid> asteroidPool = new Pool<Asteroid>() {
        @Override
        protected Asteroid newObject() {
            return new Asteroid();
        }
    };
    public DelayedRemovalArray<Particle> particleGroup = new DelayedRemovalArray<>();
    public final Pool<Particle> particlePool = new Pool<Particle>() {
        @Override
        protected Particle newObject() {
            return new Particle();
        }
    };
    public DelayedRemovalArray<Alien> alienGroup = new DelayedRemovalArray<>();
    public final Pool<Alien> alienPool = new Pool<Alien>() {
        @Override
        protected Alien newObject() {
            return new Alien();
        }
    };
    public CollisionHandler collisionHandler;
    public ShapeDrawer shapeDrawer;
    public Random random = new Random();
    public int score = 0;
    public int playerLives = 3;
    public boolean playerAlive = true;
    public int respawnTimer = 0;
    public boolean gameOver = false;
    public int level = 1;
    public int numOfAsteroidsToSpawn = 4;
    public Sound gameMusic = Gdx.audio.newSound(Gdx.files.internal("sounds/cyberpunk_beat.ogg"));

    public GameScreen(GameApp game) {
        this.game = game;
        player1 = spawnPlayer();

        shapeDrawer = new ShapeDrawer(game.batch, new TextureRegion(new Texture("white_region.png")));

        spawnAsteroids();

        Alien alien = alienPool.obtain();
        alien.init(50, 50, 50, 0);
        alienGroup.add(alien);

        collisionHandler = new CollisionHandler(bulletGroup, asteroidGroup, this);
        gameMusic.loop();
    }

    @Override
    public void render(float delta) {
        // update entities
        updatePlayer(delta);
        updateBullets(delta);
        updateAsteroids(delta);
        updateParticles(delta);
        updateAliens(delta);
        updateLevelProgress();

        // handle collisions
        collisionHandler.handleEvents();

        // draw entities
        Gdx.gl.glClearColor(
            BACKGROUND_COLOR.r,
            BACKGROUND_COLOR.g,
            BACKGROUND_COLOR.b,
            BACKGROUND_COLOR.a
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        drawDebugConsole();


        // draw shapes
        drawBullets();
        drawAsteroids();
        drawParticles();
        drawAliens();
        drawPlayer();

        game.batch.end();
    }

    public void spawnAsteroids() {
        for (int i = 0; i < numOfAsteroidsToSpawn; i++) {
            float minDistanceFromPlayer = 150;
            float x = random.nextFloat() * Constants.WINDOW_WIDTH;
            float y = random.nextFloat() * Constants.WINDOW_HEIGHT;
            while(player1.x - minDistanceFromPlayer < x && x < player1.x + minDistanceFromPlayer) {
                x = random.nextFloat() * Constants.WINDOW_WIDTH;
            }
            while(player1.y - minDistanceFromPlayer < y && y < player1.y + minDistanceFromPlayer) {
                y = random.nextFloat() * Constants.WINDOW_HEIGHT;
            }

            float velX = getRandFloat(30, 50);
            float velY = getRandFloat(30, 50);

            Asteroid newAsteroid = asteroidPool.obtain();
            newAsteroid.init(x, y, velX, velY, 150);
            asteroidGroup.add(newAsteroid);
        }
    }

    public void updatePlayer(float delta) {
        if (playerAlive) {
            player1.update(delta);
        } else {
            checkIfGameOver();
            if (!gameOver) {
                respawnPlayer();
            }
        }
    }

    public void updateBullets(float delta) {
        for (Bullet bullet: bulletGroup) {
            bullet.update(delta);
            deleteBulletIfExpired(bullet);
        }
    }

    public void updateAsteroids(float delta) {
        for (Asteroid asteroid: asteroidGroup) {
            asteroid.update(delta);
        }
    }

    public void updateParticles(float delta) {
        for (Particle particle: particleGroup) {
            particle.update(delta);
            deleteParticleIfExpired(particle);
        }
    }

    public void updateLevelProgress() {
        if (asteroidGroup.size == 0) {
            level += 1;
            numOfAsteroidsToSpawn += 1;
            spawnAsteroids();
        }
    }

    public void updateAliens(float delta) {
        for (Alien alien: alienGroup) {
            alien.update(delta);
        }
    }

    public void drawPlayer() {
        if (playerAlive) {
            player1.draw(shapeDrawer);
        }
    }

    public void drawBullets() {
        shapeDrawer.setColor(Constants.BULLET_COLOR);
        for (Bullet bullet: bulletGroup) {
            bullet.draw(shapeDrawer);
        }
    }

    public void drawAsteroids() {
        for (Asteroid asteroid: asteroidGroup) {
            asteroid.draw(shapeDrawer);
        }
    }

    public void drawParticles() {
        shapeDrawer.setColor(Constants.ASTEROID_INNER_COLOR);
        for (Particle particle: particleGroup) {
            particle.draw(shapeDrawer);
        }
    }

    public void drawAliens() {
        for (Alien alien: alienGroup) {
            alien.draw(shapeDrawer);
        }
    }

    public void playerDied() {
        playerAlive = false;
        bulletGroup.clear();
        playerLives -= 1;
        createParticleExplosion(player1.x, player1.y, 10, Constants.PLAYER_OUTER_COLOR);
        this.player1 = null;
    }

    public void checkIfGameOver() {
        if (playerLives == 0) {
            game.setScreen(new GameOverScreen(game, this));
            gameOver = true;
            gameMusic.stop();
        }
    }

    public void respawnPlayer() {
        respawnTimer += 1;
        if (respawnTimer > 60) {  // respawn after 1 seconds
            if(checkIfAsteroidsInPlayerSpawn()) {
                return;
            }
            player1 = spawnPlayer();
            playerAlive = true;
            respawnTimer = 0;
        }
    }

    public boolean checkIfAsteroidsInPlayerSpawn() {
        for (Asteroid asteroid: asteroidGroup) {
            if ((asteroid.x < Constants.WINDOW_WIDTH / 2f + 150 && asteroid.x > Constants.WINDOW_WIDTH / 2f - 150)
                && asteroid.y < Constants.WINDOW_HEIGHT / 2f + 150 && asteroid.y > Constants.WINDOW_HEIGHT / 2f - 150) {
                return true;
            }
        }
        return false;
    }

    public Player spawnPlayer() {
        return new Player(
            (float) Constants.WINDOW_WIDTH / 2 - 32,
            (float) Constants.WINDOW_HEIGHT / 2 - 32,
            64,
            64,
            new Texture("ship.png"),
            this
        );
    }

    public void deleteBulletIfExpired(Bullet bullet) {
        if (bullet.scheduledToDelete) {
            bulletGroup.removeValue(bullet, true);
            bulletPool.free(bullet);
        }
    }

    public void deleteParticleIfExpired(Particle particle) {
        if (particle.scheduledToDelete) {
            particleGroup.removeValue(particle, true);
            particlePool.free(particle);
        }
    }

    public void drawDebugConsole() {
        List<String> debugTexts = List.of(
            String.valueOf(score),
            "Lives: " + String.valueOf(playerLives)
        );

        for (int i = 0; i < debugTexts.size(); i++){
            writeDebugText(debugTexts.get(i), (35 * i));
        }
    }

    public void writeDebugText(String text, int yOffset) {
        fonts.font24.draw(game.batch, text, 12, Constants.WINDOW_HEIGHT - yOffset);
    }

    public void createParticleExplosion(float x, float y, int numOfParticles, Color color) {
        for (int i = 0; i < numOfParticles; i++) {
            Particle newParticle = particlePool.obtain();
            newParticle.init(x, y, color);
            particleGroup.add(newParticle);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        game.batch.dispose();
        player1.dispose();
        gameMusic.dispose();
    }
}
