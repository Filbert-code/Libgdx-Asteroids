package com.filbert.onlinegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGame implements Screen {
    final GameApp game;
    private Player player1;
    private Player player2;

    public MyGame(GameApp game) {
        this.game = game;
        player1 = new Player(100, 100, 100, 100, new Texture("standing_astro.png"));
        player2 = new Player(200, 200, 100, 100, new Texture("bluon.png"));
    }

    @Override
    public void render(float delta) {
        player1.update(delta);
        player2.update(delta);

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        player1.draw(game.batch);
        player2.draw(game.batch);
        game.batch.end();
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
    }
}
