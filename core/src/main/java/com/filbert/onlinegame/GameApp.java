package com.filbert.onlinegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.filbert.onlinegame.screens.GameScreen;
import com.filbert.onlinegame.screens.MenuScreen;

public class GameApp extends Game {
    public SpriteBatch batch;

    @Override
    public void create () {
        batch = new SpriteBatch();
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
