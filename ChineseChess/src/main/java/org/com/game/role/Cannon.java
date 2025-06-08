package org.com.game.role;

import org.com.game.state.GameState;

import java.awt.*;

public class Cannon extends Chess {
    public Cannon(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        if (!isLine(target)) return false;
         return state.board[target.x][target.y] != null ?
                 lineCount(target, state) == 1 : lineCount(target, state) == 0;
    }
}