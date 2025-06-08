package org.com.game.state;

import org.com.game.role.Chess;
import org.com.game.role.ChessFactory;

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

    public GameState(){
        initializeChess();
    }
    private void initializeChess(){
        String[] chessName = {"Rook", "Horse", "Bishop", "Guard", "General", "Guard", "Bishop", "Horse",
                "Rook", "Cannon", "Cannon", "Solider", "Solider", "Solider", "Solider", "Solider"};
        int[] chessXs = {0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 3, 3, 3, 3};
        int[] chessYs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 1, 7, 0, 2, 4, 6, 8};

        for (int i = 0; i < 16; ++ i){
            board[chessXs[i]][chessYs[i]] = ChessFactory.create(chessName[i], false, new Point(chessXs[i], chessYs[i]));
        }
        for (int i = 0; i < 16; ++ i){
            board[9 - chessXs[i]][chessYs[i]] = ChessFactory.create(chessName[i], true, new Point(9 - chessXs[i], chessYs[i]));
        }
    }
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
