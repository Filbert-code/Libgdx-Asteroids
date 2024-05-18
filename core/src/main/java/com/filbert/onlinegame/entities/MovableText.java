package com.filbert.onlinegame.entities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.filbert.onlinegame.dataclasses.MoveState;

public class MovableText {
    public float SPEED = 100;
    public String text;
    public float x;
    public float y;
    public float startingX;
    public float startingY;
    public BitmapFont font;
    public MoveState moveState;
    public RandomXS128 random = new RandomXS128();
    public float velX = 1;
    public float velY = 1;
    public float rot = 0;
    public MovableText(int x, int y, String text, BitmapFont font, MoveState moveState) {
        this.x = x;
        this.y = y;
        this.startingX = x;
        this.startingY = y;
        this.text = text;
        this.font = font;
        this.moveState = moveState;
    }

    public void update(float delta) {
        if (moveState == MoveState.CIRCLING) {
            updateCirclingText(delta);
        } else if (moveState == MoveState.FLOATING) {
            updateFloatingText(delta);
        } else if (moveState == MoveState.SIDE_TO_SIDE) {
            updateSideToSideText(delta);
        }
    }

    public void updateCirclingText(float delta) {
        rot += (float) ((Math.PI) * delta);
        x = (float) (50f * Math.cos(rot)) + startingX;
        y = (float) (50f * Math.sin(rot)) + startingY;
    }

    public void updateFloatingText(float delta) {
        rot += (float) ((Math.PI * 2) * delta);
        y = (float) (10f * Math.sin(rot)) + startingY;
    }

    public void updateSideToSideText(float delta) {
        rot += (float) ((Math.PI * 2) * delta);
        x = (float) (30f * Math.cos(rot)) + startingX;
    }

    public void draw(SpriteBatch batch) {
        font.draw(batch, text, x, y);
    }
}
