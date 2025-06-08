package org.com.game.role;

import org.com.game.state.GameState;

import java.awt.*;

public class Horse extends Chess {
    public Horse(String name, boolean group, Point point){
        super(name, group, point);
    }

    private boolean isStuck(Point target, GameState state){
        return state.board[point.x + (target.x-point.x)/2][point.y + (target.y- point.y)/2] != null;
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        return ((Math.abs(target.x - point.x) == 1 && Math.abs(target.y - point.y) == 2) ||
                (Math.abs(target.y - point.y) == 1 && Math.abs(target.x - point.x) == 2))
                && !isStuck(target, state);
    }
}