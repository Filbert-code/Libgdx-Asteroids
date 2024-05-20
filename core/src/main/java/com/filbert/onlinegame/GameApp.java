package com.filbert.onlinegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.filbert.onlinegame.screens.GameScreen;
import com.filbert.onlinegame.screens.MenuScreen;

public class GameApp extends Game {
    public SpriteBatch batch;
    public PolygonBatch polyBatch;

    @Override
    public void create () {
        batch = new SpriteBatch();
        polyBatch = new PolygonSpriteBatch();

        // disabling these keys for the HTML5 webpage the game is running on
        Gdx.input.setCatchKey(Input.Keys.SPACE, true);
        Gdx.input.setCatchKey(Input.Keys.A, true);
        Gdx.input.setCatchKey(Input.Keys.W, true);
        Gdx.input.setCatchKey(Input.Keys.D, true);
        Gdx.input.setCatchKey(Input.Keys.S, true);

        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose () {
        batch.dispose();}
}
