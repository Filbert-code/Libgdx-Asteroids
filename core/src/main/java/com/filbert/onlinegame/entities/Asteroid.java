package com.filbert.onlinegame.entities;

import com.badlogic.gdx.math.ConvexHull;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.StringBuilder;
import com.filbert.onlinegame.Constants;
import com.filbert.onlinegame.dataclasses.Point;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.*;

import static com.filbert.onlinegame.util.Common.getRandFloat;

public class Asteroid implements Pool.Poolable {
    public final int POINT_CLOUD_NUM_OF_POINTS = 10;
    public final float BASE_ROT_VELOCITY = (float) (Math.PI / 3);
    public float x;
    public float y;
    public float velX;
    public float velY;
    public float size;  // range of the points for the point cloud
    public List<Point> convexHullPoints = new ArrayList<>();
    public RandomXS128 random = new RandomXS128();
    public float randomRotVel;

    public void init(float x, float y, float velX, float velY, float size) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.size = size;
        createConvexHull(getRandomPointCloud());
        randomRotVel = getRandFloat(-BASE_ROT_VELOCITY, BASE_ROT_VELOCITY);
    }

    public void update(float delta) {
        for (Point point: convexHullPoints) {
            rotateConvexHull(point, delta);

            point.x += velX * delta;
            point.y += velY * delta;
        }

        handleReachingBoundary();
    }

    public void draw(ShapeDrawer drawer) {
        float[] floatArray = getConvexHullFloatArray();
        drawer.setColor(Constants.ASTEROID_INNER_COLOR);
        drawer.filledPolygon(floatArray);
        drawer.setColor(Constants.ASTEROID_OUTER_COLOR);
        drawer.polygon(floatArray, 5, JoinType.SMOOTH);
    }

    public void createConvexHull(float[] points) {
        ConvexHull convexHull = new ConvexHull();
        FloatArray array = convexHull.computePolygon(points, false);
        for (int i = 0; i < array.size - 2; i += 2) {
            float x = array.items[i];
            float y = array.items[i + 1];
            this.convexHullPoints.add(new Point(x, y));
        }
    }

    public float[] getRandomPointCloud() {
        float[] points = new float[POINT_CLOUD_NUM_OF_POINTS * 2];
        for (int i = 0; i < POINT_CLOUD_NUM_OF_POINTS * 2; i += 2) {
            points[i] = random.nextFloat() * size + x;
            points[i + 1] = random.nextFloat() * size + y;
        }
        return points;
    }

    public Vector2 getConvexHullCenter() {
        float xSum = 0;
        float ySum = 0;
        for (Point point: convexHullPoints) {
            xSum += point.x;
            ySum += point.y;
        }
        return new Vector2(xSum / convexHullPoints.size(), ySum / convexHullPoints.size());
    }

    public void rotateConvexHull(Point point, float delta) {
        // center of the convex hull in world coordinates
        Vector2 center = getConvexHullCenter();

        // update the center x,y
        x = center.x;
        y = center.y;

        // move the coordinates to a new coordinate system (centered around (0,0))
        // this makes the math a bit cleaner
        point.x -= center.x;
        point.y -= center.y;

        float distanceFromCenter = (float) Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
        double pointAngle = Math.atan2(point.y, point.x);
        double changedAngle = randomRotVel * delta;

        // rotate the point
        point.x = (float) (distanceFromCenter*Math.cos(pointAngle + changedAngle));
        point.y = (float) (distanceFromCenter*Math.sin(pointAngle + changedAngle));

        // move coordinates back to the world coordinate system
        point.x += center.x;
        point.y += center.y;
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

    @Override
    public boolean equals(Object obj) {
        Asteroid other = (Asteroid) obj;
        return MathUtils.isEqual(x, other.x) && MathUtils.isEqual(y, other.y) && MathUtils.isEqual(velX, other.velX) && MathUtils.isEqual(velY, other.velY) && MathUtils.isEqual(size, other.size);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Asteroid: ");
        for (Point point: convexHullPoints) {
            sb.append(point.toString()).append(", ");
        }
        return sb.toString();
    }

    @Override
    public void reset() {
        x = -1000;
        y = -1000;
        velX = 0;
        velY = 0;
        convexHullPoints = new ArrayList<>();
        randomRotVel = 0;
    }
}
