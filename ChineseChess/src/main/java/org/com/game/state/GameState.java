package org.com.game.state;

import org.com.game.role.Chess;

import java.awt.*;


/**
 * @author lanye
 * @date 2025/06/08
 * @description: 悔棋时，如果不是你的回合，悔一步棋，否则悔两步棋
 */
public class GameState {
    public volatile Chess[][] board = new Chess[10][9];
    public volatile boolean gameTurn;

    public GameState(){
        gameTurn = false;
    }
    public void doAction(GameRecord record){
        Point startPoint = record.getStartPoint();
        Point endPoint = record.getEndPoint();
        board[startPoint.x][startPoint.y] = null;
        board[endPoint.x][endPoint.y] = record.getMovingChess();

        gameTurn = !gameTurn;
    }
    public void repeal(GameRecord record){

    }
}
