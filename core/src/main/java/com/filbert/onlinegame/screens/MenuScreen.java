package com.filbert.onlinegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.filbert.onlinegame.*;
import com.filbert.onlinegame.entities.Asteroid;
import com.filbert.onlinegame.entities.MovableText;
import com.filbert.onlinegame.dataclasses.MoveState;
import com.filbert.onlinegame.util.Fonts;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.net.URL;

import static com.filbert.onlinegame.Constants.BACKGROUND_COLOR;
import static com.filbert.onlinegame.util.Common.getRandFloat;

public class MenuScreen implements Screen {
    final GameApp game;
    public Fonts fonts = new Fonts();
    public DelayedRemovalArray<Asteroid> asteroidGroup = new DelayedRemovalArray<>();
    public ShapeDrawer shapeDrawer;
    public MovableText titleText;
    public MovableText startText;
    public RandomXS128 random = new RandomXS128();

    public MenuScreen(GameApp game) {
//        try {
//
//            URL url = new URL("https://8qfkhd5am0.execute-api.us-west-2.amazonaws.com/prod/asteroids");
//
//            String json = "{" +
//                "\"api_method\": \"POST\"," +
//                "\"username\": \"John\"," +
//                "\"high_score\": \"3500\"" +
//            "}";
//
//            MediaType JSON = MediaType.get("application/json");
//            OkHttpClient client = new OkHttpClient();
//            RequestBody body = RequestBody.create(json, JSON);
//            Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//            try (Response response = client.newCall(request).execute()) {
//                System.out.println(response.body().string());
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        this.game = game;
        shapeDrawer = new ShapeDrawer(game.batch, new TextureRegion(new Texture("white_region.png")));

        spawnAsteroids();

        titleText = new MovableText(
            280,
            Constants.WINDOW_HEIGHT - 150,
            "ASTEROIDS",
            fonts.font86,
            MoveState.CIRCLING
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
        for (Asteroid asteroid: asteroidGroup) {
            asteroid.update(delta);
        }
        titleText.update(delta);
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

        // draw shapes
        for (Asteroid asteroid: asteroidGroup) {
            asteroid.draw(shapeDrawer);
        }

        // draw text onto the screen
        drawMenuText();
        titleText.draw(game.batch);
        startText.draw(game.batch);

        game.batch.end();
    }

    public void drawMenuText() {
//        fonts.draw(game.batch, fonts.font48, "ASTEROIDS", Constants.WINDOW_WIDTH / 3, Constants.WINDOW_HEIGHT - 250);
        fonts.draw(game.batch, fonts.font48, "Press              to Start", 300, 240);
    }

    public void handleInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    public void spawnAsteroids() {
        for (int i = 0; i < 5; i++) {
            float x = random.nextFloat() * Constants.WINDOW_WIDTH;
            float y = random.nextFloat() * Constants.WINDOW_HEIGHT;

            float velX = getRandFloat(30, 50);
            float velY = getRandFloat(30, 50);

            Asteroid newAsteroid = new Asteroid();
            newAsteroid.init(x, y, velX, velY, 150);
            asteroidGroup.add(newAsteroid);
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
        fonts.dispose();
    }
}
