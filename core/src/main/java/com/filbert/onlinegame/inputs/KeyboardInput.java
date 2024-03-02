package com.filbert.onlinegame.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardInput implements UserInput{
    @Override
    public boolean isLeftPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            return true;
        else
            return false;
    }

    @Override
    public boolean isRightPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            return true;
        else
            return false;
    }

    @Override
    public boolean isUpPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            return true;
        else
            return false;
    }

    @Override
    public boolean isDownPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            return true;
        else
            return false;
    }

//    @Override
//    public boolean isJumpPressed() {
//        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
//            return true;
//        else
//            return false;
//    }
//
//    @Override
//    public boolean isDashPressed() {
//        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
//            return true;
//        else
//            return false;
//    }
//
//    @Override
//    public boolean isGrabPressed() {
//        if (Gdx.input.isKeyPressed(Input.Keys.L))
//            return true;
//        else
//            return false;
//    }

    @Override
    public boolean isStartPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            return true;
        else
            return false;
    }
}
