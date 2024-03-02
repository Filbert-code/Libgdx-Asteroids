package com.filbert.onlinegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameApp extends Game {
    public SpriteBatch batch;

    @Override
    public void create () {
        batch = new SpriteBatch();
        this.setScreen(new MyGame(this));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose () {
        batch.dispose();}
}
