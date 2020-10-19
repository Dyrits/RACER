package com.codegym.games.racer;

import com.codegym.engine.cell.*;
import com.codegym.games.racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int
            WIDTH = 64,
            HEIGHT = 64,
            CENTER_X = WIDTH / 2,
            ROADSIDE_WIDTH = 14;
    private static final int RACE_GOAL_CARS_COUNT = 40;
    private PlayerCar player;
    private RoadMarking roadMarking;
    private RoadManager roadManager;
    private FinishLine finishLine;
    private ProgressBar progressBar;
    private boolean isGameStopped;
    private int score;

    @Override
    public void initialize() {
        showGrid(false);
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame() {
        score = 3500;
        isGameStopped = false;
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        setTurnTimer(40);
        drawScene();
    }

    private void drawScene() {
        drawField();
        roadMarking.draw(this);
        player.draw(this);
        roadManager.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }

    private void drawField() {
        for (int x = 0; x < WIDTH; x ++) {
            for (int y = 0; y < HEIGHT; y ++) {
                if (x == CENTER_X) { setCellColor(x, y, Color.WHITE); }
                else if (x > ROADSIDE_WIDTH - 1 && x < WIDTH - ROADSIDE_WIDTH) { setCellColor(x, y, Color.DIMGREY); }
                else { setCellColor(x, y, Color.GREEN); }
            }
        }
    }

    private void moveAll() {
        roadMarking.move(player.speed);
        roadManager.move(player.speed);
        player.move();
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        boolean isOutOfField = x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1;
        if (!isOutOfField) { super.setCellColor(x, y, color); }
    }

    @Override
    public void onTurn(int step) {
        if (roadManager.checkCrash(player)) { gameOver(); }
        else {
            if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) { finishLine.show(); }
            if (finishLine.isCrossed(player)) { win(); }
            else {
                moveAll();
                roadManager.generateNewRoadObjects(this);
            }
        }
        score -= 5;
        setScore(score);
        drawScene();
    }

    @Override
    public void onKeyPress(Key key) {
        if (!isGameStopped) {
            if (key.equals(Key.RIGHT)) { player.setDirection(Direction.RIGHT); }
            if (key.equals(Key.LEFT)) { player.setDirection(Direction.LEFT); }
            if (key.equals(Key.UP)) { player.speed = 2; }
        } else if (key.equals(Key.SPACE)) { createGame(); }
    }

    @Override
    public void onKeyReleased(Key key) {
        if (!isGameStopped) {
            if (key.equals(Key.RIGHT) && player.getDirection().equals(Direction.RIGHT)) { player.setDirection(Direction.NONE); }
            if (key.equals(Key.LEFT) && player.getDirection().equals(Direction.LEFT)) { player.setDirection(Direction.NONE); }
            if (key.equals(Key.UP)) { player.speed = 1; }
        }
    }

    private void gameOver() {
        isGameStopped = true;
        stopTurnTimer();
        showMessageDialog(Color.FIREBRICK, "GAME OVER!", Color.WHITE, 36);
        player.stop();
    }

    private void win() {
        isGameStopped = true;
        stopTurnTimer();
        showMessageDialog(Color.DARKGREEN, "VICTORY!", Color.WHITE, 36);

    }
}
