package com.filbert.onlinegame.entities;

import com.badlogic.gdx.utils.Pool;
import com.filbert.onlinegame.Constants;
import com.filbert.onlinegame.dataclasses.Point;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Alien implements Pool.Poolable{

    public float x;
    public float y;
    public float velX;
    public float velY;
    public float size = 50;
    public List<Point> convexHullPoints;

    public void init(float x, float y, float velX, float velY) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        convexHullPoints = List.of(
            new Point(12, 6),
            new Point(24, 0),
            new Point(12, -6),
            new Point(-12, -6),
            new Point(-24, 0),
            new Point(-12, 6),
            new Point(-12, 9),
            new Point(-6, 15),
            new Point(6, 15),
            new Point(12, 9)
        );
        convexHullPoints = convexHullPoints.stream().map(
            point -> {point.x *= 2; point.y *= 2; return point;}
        ).collect(Collectors.toList());
    }

    public void update(float delta) {
        for (Point point: convexHullPoints) {
            point.x += velX * delta;
            point.y += velY * delta;
        }

        handleReachingBoundary();
    }

    public void draw(ShapeDrawer drawer) {
        float[] floatArray = getConvexHullFloatArray();
        drawer.setColor(Constants.ALIEN_INNER_COLOR);
        drawer.filledPolygon(floatArray);
        drawer.setColor(Constants.ALIEN_OUTER_COLOR);
        drawer.polygon(floatArray, 2, JoinType.SMOOTH);
    }

    public void handleReachingBoundary() {
        // wrap asteroid if reaches the end of the screen
        if (velX > 0 && x > Constants.WINDOW_WIDTH + size / 2) {
            for (Point point: convexHullPoints) {
                point.x -= Constants.WINDOW_WIDTH + size;
            }
        } else if (velX < 0 && x < -size / 2) {
            for (Point point: convexHullPoints) {
                point.x += Constants.WINDOW_WIDTH + size;
            }
        }
        if (velY > 0 && y > Constants.WINDOW_HEIGHT + size / 2) {
            for (Point point: convexHullPoints) {
                point.y -= Constants.WINDOW_HEIGHT + size;
            }
        } else if (velY < 0 && y < -size / 2) {
            for (Point point: convexHullPoints) {
                point.y += Constants.WINDOW_HEIGHT + size;
            }
        }
    }

    public float[] getConvexHullFloatArray() {
        float[] array = new float[convexHullPoints.size() * 2];

        for (int i = 0; i < convexHullPoints.size() * 2; i += 2) {
            Point p = convexHullPoints.get(i / 2);
            array[i] = p.x;
            array[i + 1] = p.y;
        }
        return array;
    }

    @Override
    public void reset() {
        x = -1000;
        y = -1000;
        velX = 0;
        velY = 0;
        convexHullPoints = new ArrayList<>();
    }
}
