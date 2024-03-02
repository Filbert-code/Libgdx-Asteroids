package com.filbert.onlinegame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.filbert.onlinegame.inputs.KeyboardInput;
import com.filbert.onlinegame.inputs.UserInput;

public class Player {
    public float x;
    public float y;
    public float width;
    public float height;
    public Texture texture;
    public float velX = 0;
    public float velY = 0;

    public UserInput input = new KeyboardInput();

    public Player(float x, float y, float width, float height, Texture texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public void update(float delta) {
        if (input.isRightPressed()) {
            velX = 300;
        } else if (input.isLeftPressed()) {
            velX = -300;
        } else {
            velX = 0;
        }
        if (input.isUpPressed()) {
            velY = 300;
        } else if (input.isDownPressed()) {
            velY = -300;
        } else {
            velY = 0;
        }

        x += velX * delta;
        y += velY * delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void dispose() {
        texture.dispose();
    }
}
