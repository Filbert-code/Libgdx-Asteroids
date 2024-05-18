package com.filbert.onlinegame.entities;

import com.badlogic.gdx.utils.Pool;
import com.filbert.onlinegame.Constants;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Bullet implements Pool.Poolable {
    public static final int TIME_TO_LIVE = 35;  // 60 frames a second, for X number of seconds
    public float x;
    public float y;
    public float velX;
    public float velY;
    public float rot;
    public int width = 6;
    public int timeAlive = 0;
    public boolean scheduledToDelete = false;


    public Bullet() {

    }

    public void init(float x, float y, float velX, float velY, float rot) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.rot = rot;
    }

    public void update(float delta) {
        x += (velX * delta);
        y += (velY * delta);
        timeAlive += 1;

        if (timeAlive >= TIME_TO_LIVE) {
            scheduledToDelete = true;
        }

        // wrap around the screen
        handleReachingBoundary();
    }

    public void draw(ShapeDrawer drawer) {
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
    public boolean equals(Object obj) {
        Bullet other = (Bullet) obj;
        return x == other.x && y == other.y && velX == other.velX && velY == other.velY && rot == other.rot;
    }

    @Override
    public void reset() {
        x = -1000;
        y = -1000;
        velX = 0;
        velY = 0;
        rot = 0;
        timeAlive = 0;
        scheduledToDelete = false;
    }
}
