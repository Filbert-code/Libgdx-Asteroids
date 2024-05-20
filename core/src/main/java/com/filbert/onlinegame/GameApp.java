package com.filbert.onlinegame;

import com.badlogic.gdx.Game;
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
