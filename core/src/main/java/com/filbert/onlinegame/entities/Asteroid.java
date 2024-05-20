package com.filbert.onlinegame.entities;

import com.badlogic.gdx.math.*;
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
    public Polygon polygon;

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
        polygon.translate(velX * delta, velY * delta);
        Vector2 polyCenter = getPolygonCenter();
        polygon.setOrigin(polyCenter.x, polyCenter.y);
        polygon.rotate((float) Math.toDegrees(randomRotVel * delta));

        Vector2 center = getPolygonTransformedCenter();

        // update the center x,y
        x = center.x;
        y = center.y;

        handleReachingBoundary();
    }

    public void draw(ShapeDrawer drawer) {
        drawer.setColor(Constants.ASTEROID_INNER_COLOR);
        drawer.filledPolygon(polygon);
        drawer.setColor(Constants.ASTEROID_OUTER_COLOR);
        drawer.polygon(polygon, 2);
    }

    public float[] getRandomPointCloud() {
        float[] points = new float[POINT_CLOUD_NUM_OF_POINTS * 2];
        for (int i = 0; i < POINT_CLOUD_NUM_OF_POINTS * 2; i += 2) {
            points[i] = random.nextFloat() * size + x;
            points[i + 1] = random.nextFloat() * size + y;
        }
        return points;
    }

    public void createConvexHull(float[] points) {
        ConvexHull convexHull = new ConvexHull();
        FloatArray array = convexHull.computePolygon(points, false);
        for (int i = 0; i < array.size - 2; i += 2) {
            float x = array.items[i];
            float y = array.items[i + 1];
            this.convexHullPoints.add(new Point(x, y));
        }
        polygon = new Polygon(array.toArray());
    }

    public Vector2 getPolygonCenter() {
        float xSum = 0;
        float ySum = 0;
        float[] vertices = polygon.getVertices();
        for (int i = 0; i < vertices.length; i++) {
            if (i % 2 == 0) {
                xSum += vertices[i];
            } else {
                ySum += vertices[i];
            }
        }
        return new Vector2(xSum / ((float) vertices.length / 2), ySum / ((float) vertices.length / 2));
    }

    public Vector2 getPolygonTransformedCenter() {
        float xSum = 0;
        float ySum = 0;
        float[] vertices = polygon.getTransformedVertices();
        for (int i = 0; i < vertices.length; i++) {
            if (i % 2 == 0) {
                xSum += vertices[i];
            } else {
                ySum += vertices[i];
            }
        }
        return new Vector2(xSum / ((float) vertices.length / 2), ySum / ((float) vertices.length / 2));
    }

    public void handleReachingBoundary() {
        // wrap asteroid if reaches the end of the screen
        if (velX > 0 && x > Constants.WINDOW_WIDTH + size / 2) {
            polygon.translate(-Constants.WINDOW_WIDTH - size, 0);
        } else if (velX < 0 && x < -size / 2) {
            polygon.translate(Constants.WINDOW_WIDTH + size, 0);
        }
        if (velY > 0 && y > Constants.WINDOW_HEIGHT + size / 2) {
            polygon.translate(0, -Constants.WINDOW_HEIGHT - size);
        } else if (velY < 0 && y < -size / 2) {
            polygon.translate(0, Constants.WINDOW_HEIGHT + size);
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
