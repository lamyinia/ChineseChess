package org.com.game.state;

import org.com.game.role.Chess;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 悔棋时，如果不是你的回合，悔一步棋，否则悔两步棋
 * @author lanye
 * @date 2025/06/08
 */
public class GameState {
    public volatile Chess[][] board = new Chess[10][9];
    public volatile AtomicBoolean gameTurn = new AtomicBoolean(false);

    public GameState(){}
    public void doAction(GameRecord record){
        Point startPoint = record.getStartPoint();
        Point endPoint = record.getEndPoint();
        board[startPoint.x][startPoint.y] = null;
        board[endPoint.x][endPoint.y] = record.getMovingChess();

        gameTurn.set(!gameTurn.get());
    }
    public void doRepeal(GameRecord record){
        Point startPoint = record.getStartPoint();
        Point endPoint = record.getEndPoint();
        record.getMovingChess().setPoint(startPoint);

        board[startPoint.x][startPoint.y] = record.getMovingChess();
        board[endPoint.x][endPoint.y] = record.getEatenChess();

        gameTurn.set(!gameTurn.get());
    }
}
