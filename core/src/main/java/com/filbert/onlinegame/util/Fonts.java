package com.filbert.onlinegame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.filbert.onlinegame.Constants;

public class Fonts {
    public BitmapFont font24;
    public BitmapFont font48;
    public BitmapFont font64;
    public BitmapFont font86;

    public Fonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/SF_Funk_Master.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 0.5f;
        font24 = generator.generateFont(parameter);
        parameter.size = 48;
        font48 = generator.generateFont(parameter);
        parameter.size = 64;
        font64 = generator.generateFont(parameter);
        parameter.size = 128;
        font86 = generator.generateFont(parameter);
        generator.dispose();
    }

    public void draw(SpriteBatch batch, BitmapFont font, String text, int x, int y) {
        font.draw(batch, text, x, y);
    }

    public void dispose() {
        font24.dispose();
    }
}
