package org.com.game.state;

import org.com.game.role.Chess;

import java.awt.*;
import java.io.Serializable;

public class GameRecord implements Serializable {
    private Point startPoint;
    private Point endPoint;
    private Chess movingChess;
    private Chess EatenChess;
    private boolean Eating;

    public GameRecord(){}
    public GameRecord(Point startPoint, Point endPoint, Chess movingChess, Chess eatenChess, boolean eating) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.movingChess = movingChess;
        EatenChess = eatenChess;
        Eating = eating;
    }
    public Point getStartPoint() {
        return startPoint;
    }
    public Point getEndPoint() {
        return endPoint;
    }
    public Chess getMovingChess() {
        return movingChess;
    }
    public Chess getEatenChess() {
        return EatenChess;
    }
    public boolean isEating() {
        return Eating;
    }
}
