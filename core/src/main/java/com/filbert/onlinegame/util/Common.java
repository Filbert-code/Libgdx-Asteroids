package com.filbert.onlinegame.util;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.FloatArray;
import com.filbert.onlinegame.dataclasses.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    public static Random random = new RandomXS128();
    public static List<Point> getVertices(float[] vertices) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < vertices.length; i += 2) {
            points.add(new Point(vertices[i], vertices[i + 1]));
        }
        return points;
    }

    public static FloatArray getFloatArrayFromPointsList(List<Point> points) {
        FloatArray fa = new FloatArray();
        for (Point point: points) {
            fa.add(point.x, point.y);
        }
        return fa;
    }

    public static float getRandFloat(float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }
}
