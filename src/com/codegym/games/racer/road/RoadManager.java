package com.codegym.games.racer.road;

import com.codegym.engine.cell.Game;
import com.codegym.games.racer.GameObject;
import com.codegym.games.racer.PlayerCar;
import com.codegym.games.racer.RacerGame;

import java.util.ArrayList;
import java.util.List;

public class RoadManager {
    public static final int
            LEFT_BORDER = RacerGame.ROADSIDE_WIDTH,
            RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int
            FIRST_LANE_POSITION = 16,
            FOURTH_LANE_POSITION = 44,
            PLAYER_CAR_DISTANCE = 12;
    private List<RoadObject> items = new ArrayList<>();
    private int passedCarsCount = 0;

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if (type == RoadObjectType.SPIKE) {
            return new Spike(x, y);
        }
        if (type == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        }
        return new Car(RoadObjectType.CAR, x, y);
    }

    public void generateNewRoadObjects(Game game) {
        generateRegularCar(game);
        generateSpike(game);
        generateMovingCar(game);
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);
        RoadObject roadObject = createRoadObject(type, x, y);
        if (isRoadSpaceFree(roadObject)) { items.add(roadObject); }
    }

    public void draw(Game game) {
        items.forEach(item -> item.draw(game));
    }

    public void move(int boost) {
        items.forEach(item -> item.move(boost + item.speed, items));
        deletePassedItems();
    }

    private void generateSpike(Game game) {
        if (game.getRandomNumber(100) < 10 && !spikeExists()) {
            addRoadObject(RoadObjectType.SPIKE, game);
        };
    }

    private void generateRegularCar(Game game) {
        int carTypeNumber = game.getRandomNumber(4);
        if (game.getRandomNumber(100) < 30) {
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        };
    }

    private void generateMovingCar(Game game) {
        int carTypeNumber = game.getRandomNumber(4);
        if (game.getRandomNumber(100) < 10 && !movingCarExists()) {
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        };
    }

    private boolean spikeExists() {
        for (RoadObject item: items) {
            if (item instanceof Spike) { return true; }
        }
        return false;
    }

    private boolean movingCarExists() {
        for (RoadObject item: items) {
            if (item instanceof MovingCar) { return true; }
        }
        return false;
    }

    private void deletePassedItems() {
        for (RoadObject item: new ArrayList<>(items)) {
            if (item.getY() >= RacerGame.HEIGHT) {
                items.remove(item);
                if (item.type != RoadObjectType.SPIKE) {
                    this.passedCarsCount ++;
                }
            }
        };
    }

    public boolean checkCrash(PlayerCar playerCar) {
        for (GameObject item : items) {
            if (item.isCollision(playerCar)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        for (RoadObject item : items) {
            if (item.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE)) {
                return false;
            }
        }
        return true;
    }

    public int getPassedCarsCount() {
        return passedCarsCount;
    }
}
