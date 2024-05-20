package com.filbert.onlinegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.filbert.onlinegame.*;
import com.filbert.onlinegame.dataclasses.MoveState;
import com.filbert.onlinegame.entities.Asteroid;
import com.filbert.onlinegame.entities.MovableText;
import com.filbert.onlinegame.entities.Particle;
import com.filbert.onlinegame.util.Fonts;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.filbert.onlinegame.Constants.BACKGROUND_COLOR;

public class GameOverScreen implements Screen {
    final GameApp game;
    final GameScreen gameScreen;
    public Fonts fonts = new Fonts();
    public DelayedRemovalArray<Asteroid> asteroidGroup = new DelayedRemovalArray<>();
    public ShapeDrawer shapeDrawer;
    public MovableText gameOverText;
    public MovableText startText;

    public GameOverScreen(GameApp game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        shapeDrawer = new ShapeDrawer(game.batch, new TextureRegion(new Texture("white_region.png")));

        gameOverText = new MovableText(
            280,
            Constants.WINDOW_HEIGHT - 180,
            "GAME OVER",
            fonts.font86,
            MoveState.SIDE_TO_SIDE
        );
        startText = new MovableText(
            490,
            Constants.WINDOW_HEIGHT - 480,
            "[SPACE]",
            fonts.font48,
            MoveState.FLOATING
        );
    }

    @Override
    public void render(float delta) {
        handleInputs();

        // update entities
        for (Asteroid asteroid: gameScreen.asteroidGroup) {
            asteroid.update(delta);
        }
        for (Particle particle: gameScreen.particleGroup) {
            particle.update(delta);
            gameScreen.deleteParticleIfExpired(particle);
        }
        gameOverText.update(delta);
        startText.update(delta);

        // draw entities
        Gdx.gl.glClearColor(
            BACKGROUND_COLOR.r,
            BACKGROUND_COLOR.g,
            BACKGROUND_COLOR.b,
            BACKGROUND_COLOR.a
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        // draw text onto the screen
        gameScreen.drawDebugConsole();

        // draw shapes
        for (Asteroid asteroid: gameScreen.asteroidGroup) {
            asteroid.draw(shapeDrawer);
        }
        for (Particle particle: gameScreen.particleGroup) {
            particle.draw(shapeDrawer);
        }

        drawMenuText();
        gameOverText.draw(game.batch);
        startText.draw(game.batch);

        game.batch.end();
    }

    public void drawMenuText() {
        fonts.draw(game.batch, fonts.font48, "Press              to Start", 300, 240);
    }

    public void drawText(String text, int xOffset, int yOffset) {
        fonts.font48.draw(game.batch, text, xOffset, Constants.WINDOW_HEIGHT - yOffset);
    }

    public void handleInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
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

    }
}
