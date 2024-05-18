package com.filbert.onlinegame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Pool;
import com.filbert.onlinegame.Constants;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.filbert.onlinegame.util.Common.getRandFloat;

public class Particle implements Pool.Poolable {
    public static final int BASE_TIME_TO_LIVE = 60 * 1;  // half a second
    public static final float BASE_SPEED = 150;
    public float x;
    public float y;
    public RandomXS128 random = new RandomXS128();
    public float velX;
    public float velY;
    public float rot;
    public float width = 4;
    public int timeAlive = 0;
    public float timeToLive;
    public float randomSpeed;
    public boolean scheduledToDelete = false;
    public Color color;

    public void init(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        rot = (float) (random.nextFloat() * 2 * Math.PI);
        randomSpeed = getRandFloat(BASE_SPEED - 100, BASE_SPEED + 100);
        velX = (float) (randomSpeed * Math.cos(rot));
        velY = (float) (randomSpeed * Math.sin(rot));
        timeToLive = getRandFloat(BASE_TIME_TO_LIVE - 10, BASE_TIME_TO_LIVE);
        this.color = color;
    }

    public void init(float x, float y, float velX, float velY, Color color) {
        this.x = x;
        this.y = y;
        rot = (float) (random.nextFloat() * 2 * Math.PI);
        this.velX = velX;
        this.velY = velY;
        timeToLive = getRandFloat(BASE_TIME_TO_LIVE - 10, BASE_TIME_TO_LIVE);
        this.color = color;
    }

    public void update(float delta) {
        x += (velX * delta);
        y += (velY * delta);
        timeAlive += 1;

        if (timeAlive >= timeToLive) {
            scheduledToDelete = true;
        }

        // wrap around the screen
        handleReachingBoundary();
    }

    public void draw(ShapeDrawer drawer) {
        drawer.setColor(color);
        drawer.filledRectangle(x, y, width, width, rot);
    }

    public void handleReachingBoundary() {
        if (velX > 0 && x > Constants.WINDOW_WIDTH + width / 2f) {
            x -= Constants.WINDOW_WIDTH + width;
        } else if (velX < 0 && x < -width / 2f) {
            x += Constants.WINDOW_WIDTH + width;
        }
        if (velY > 0 && y > Constants.WINDOW_HEIGHT + width / 2f) {
            y -= Constants.WINDOW_HEIGHT + width;
        } else if (velY < 0 && y < -width / 2f) {
            y += Constants.WINDOW_HEIGHT + width;
        }
    }

    @Override
    public void reset() {
        x = -1000;
        y = -1000;
        timeAlive = 0;
        scheduledToDelete = false;
        randomSpeed = 0;
        timeToLive = 0;
    }
}
