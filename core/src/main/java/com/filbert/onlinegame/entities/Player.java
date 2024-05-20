package com.filbert.onlinegame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.filbert.onlinegame.Constants;
import com.filbert.onlinegame.dataclasses.Point;
import com.filbert.onlinegame.inputs.KeyboardInput;
import com.filbert.onlinegame.inputs.UserInput;
import com.filbert.onlinegame.screens.GameScreen;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Player {
    public static final int BULLET_SPEED = 1000;
    public float TOP_SPEED = 300;
    public float SPEED = 15;
    public float x;
    public float y;
    public float width;
    public float height;
    public float velX = 0;
    public float velY = 0;
    public float rot = 0;
    public float dampening = 2;
    public List<Point> shipVertices;
    public List<Point> thrusterVertices;
    public List<Point> allVertices;
    public List<Float> verticesDistancesFromCenter = new ArrayList<>();
    public GameScreen game;
    public Point convexHullCenter = new Point(0, 0);
    public UserInput input = new KeyboardInput();
    public Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot02.mp3"));
//    public Sound thrusterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/loopingthrust.mp3"));
    public boolean firingThruster = false;
    public int timer = 0;

    public Player(float x, float y, float width, float height, GameScreen game) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        shipVertices = List.of(
            new Point(x + width / 2, y + height),
            new Point(x, y),
            new Point(x + width / 2, y + height / 4),
            new Point(x + width, y)
        );
        thrusterVertices = List.of(
            new Point(x + width / 4 + 5, y + height / 2),
            new Point(x + width * 3 / 4 - 5, y + height / 2),
            new Point(x + width / 2, y - height / 2)
        );
        allVertices = Stream.concat(shipVertices.stream(), thrusterVertices.stream()).collect(Collectors.toList());
        this.game = game;
        updateConvexHullCenter();
        fillVerticesDistancesFromCenterList();

//        thrusterSound.loop(0.5f);
//        thrusterSound.pause();
    }

    public void update(float delta) {
        processInput();

        updateConvexHullCenter();

        // update the vertices positions
        for (int i = 0; i < allVertices.size(); i++) {
            if (input.isLeftPressed() || input.isRightPressed()) {
                rotateConvexHull(allVertices.get(i), i);
            }
            allVertices.get(i).x += velX * delta;
            allVertices.get(i).y += velY * delta;
        }

        handleReachingBoundary();

        x = allVertices.get(1).x;
        y = allVertices.get(1).y;

        shipVertices = allVertices.subList(0, 4);
        thrusterVertices = allVertices.subList(4, allVertices.size());
        timer += 1;
    }

    public void updateConvexHullCenter() {
        double xSum = 0;
        double ySum = 0;
        for (Point point: shipVertices) {
            xSum += point.x;
            ySum += point.y;
        }
        convexHullCenter.x = (float) (xSum / shipVertices.size());
        convexHullCenter.y = (float) (ySum / shipVertices.size());
    }

    public void draw(ShapeDrawer drawer) {
        // draw the thruster firing
        drawer.setColor(Constants.PLAYER_OUTER_COLOR);
        if (firingThruster && ((timer / 4) % 2 == 1)) {
            drawer.polygon(getConvexHullFloatArray(thrusterVertices), 5, JoinType.POINTY);
            firingThruster = false;
        }
        // draw the ship
        drawer.setColor(Constants.PLAYER_INNER_COLOR);
        drawer.filledPolygon(getConvexHullFloatArray(shipVertices));
        drawer.setColor(Constants.PLAYER_OUTER_COLOR);
        drawer.polygon(getConvexHullFloatArray(shipVertices), 2, JoinType.POINTY);
    }

    public void fireBullet() {
        float velX = (float) -(Math.sin(rot) * BULLET_SPEED);
        float velY = (float) (Math.cos(rot) * BULLET_SPEED);

        float xPos = shipVertices.get(0).x - 3;
        float yPos = shipVertices.get(0).y;

        Bullet bullet = game.bulletPool.obtain();
        bullet.init(xPos, yPos, velX, velY, rot);
        game.bulletGroup.add(bullet);
        shootSound.play(1.0f);
    }

    public void processInput() {
        if (input.isUpPressed()) {
            move();
            firingThruster = true;
//            thrusterSound.resume();
        } else {
            float xYRatio = getXYRatio(velX, velY);
            if (Math.abs(velX) < 5) {
                velX = 0;
            } else if (velX > 0) {
                velX -= dampening;
            } else {
                velX += dampening;
            }

            if (Math.abs(velY) < 5) {
                velY = 0;
            } else if (velY > 0) {
                velY -= dampening;
            } else {
                velY += dampening;
            }
        }
        if (input.isSpacePressed()) {
            fireBullet();
        }
        if (!input.isUpPressed()) {
//            thrusterSound.pause();
        }
    }

    public void move() {
        // calculate x and y components
        float maxXVel = (float) -(Math.sin(rot) * TOP_SPEED);
        float maxYVel = (float) (Math.cos(rot) * TOP_SPEED);

        if (Math.abs(velX) < Math.abs(maxXVel)) {
            velX += (float) -(Math.sin(rot) * SPEED);
        }
        if (Math.abs(velY) < Math.abs(maxYVel)) {
            velY += (float) (Math.cos(rot) * SPEED);
        }
    }

    public float getXYRatio(float x, float y) {
        if (y == 0) {
            // dividing by zero is a no-no
            return 0f;
        }
        return x / y;
    }

    public void handleReachingBoundary() {
        // wrap player if reaches the end of the screen
        if (velX > 0 && x > Constants.WINDOW_WIDTH) {
            for (Point vertex: allVertices) {
                vertex.x -= Constants.WINDOW_WIDTH + width;
            }
        } else if (velX < 0 && x < -width) {
            for (Point vertex: allVertices) {
                vertex.x += Constants.WINDOW_WIDTH + width;
            }
        }
        if (velY > 0 && y > Constants.WINDOW_HEIGHT) {
            for (Point vertex: allVertices) {
                vertex.y -= Constants.WINDOW_HEIGHT + height;
            }
        } else if (velY < 0 && y < -height) {
            for (Point vertex: allVertices) {
                vertex.y += Constants.WINDOW_HEIGHT + height;
            }
        }
    }

    // TODO: Refactor this into a utility class
    public void rotateConvexHull(Point point, int index) {
        // move the coordinates to a new coordinate system (centered around (0,0))
        // this makes the math a bit cleaner
        point.x -= convexHullCenter.x;
        point.y -= convexHullCenter.y;

        double pointAngle = Math.atan2(point.y, point.x);

        // the first point is used to set the rotation angle
        if (index == 0) {
            rot = (float) ((pointAngle + (Math.PI * 3) / 2) % (Math.PI * 2));
        }

        double changedAngle = (Math.PI / 45);
        changedAngle = input.isLeftPressed() ? changedAngle : changedAngle * -1;

        // rotate the point
        point.x = (float) (verticesDistancesFromCenter.get(index)*Math.cos(pointAngle + changedAngle));
        point.y = (float) (verticesDistancesFromCenter.get(index)*Math.sin(pointAngle + changedAngle));

        // move coordinates back to the world coordinate system
        point.x += convexHullCenter.x;
        point.y += convexHullCenter.y;
    }

    public void fillVerticesDistancesFromCenterList() {
        for (Point point: allVertices) {
            point.x -= convexHullCenter.x;
            point.y -= convexHullCenter.y;
            float distanceFromCenter = (float) Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
            verticesDistancesFromCenter.add(distanceFromCenter);
            point.x += convexHullCenter.x;
            point.y += convexHullCenter.y;
        }
    }

    // TODO: refactor this into a utility class
    public float[] getConvexHullFloatArray(List<Point> vertices) {
        float[] array = new float[vertices.size() * 2];

        for (int i = 0; i < vertices.size() * 2; i += 2) {
            Point p = vertices.get(i / 2);
            array[i] = p.x;
            array[i + 1] = p.y;
        }
        return array;
    }

    public void dispose() {

    }
}
