package com.filbert.onlinegame.dataclasses;

import com.badlogic.gdx.math.MathUtils;

public class Point {
    public float x;
    public float y;
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        Point otherPoint = (Point) obj;
        return MathUtils.isEqual(x, otherPoint.x) && MathUtils.isEqual(y, otherPoint.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
