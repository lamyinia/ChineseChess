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
    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }
    public Point getEndPoint() {
        return endPoint;
    }
    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
    public Chess getMovingChess() {
        return movingChess;
    }
    public void setMovingChess(Chess movingChess) {
        this.movingChess = movingChess;
    }
    public Chess getEatenChess() {
        return EatenChess;
    }
    public void setEatenChess(Chess eatenChess) {
        EatenChess = eatenChess;
    }
    public boolean isEating() {
        return Eating;
    }
    public void setEating(boolean eating) {
        Eating = eating;
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", movingChess=" + movingChess +
                ", EatenChess=" + EatenChess +
                ", Eating=" + Eating +
                '}';
    }
}
