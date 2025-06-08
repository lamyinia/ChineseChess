package org.com.game.role;

import org.com.game.state.GameState;

import java.awt.*;

public class Bishop extends Chess {

    public Bishop(String name, boolean group, Point point){
        super(name, group, point);
    }

    private boolean isStuck(Point target, GameState state){
        return state.board[(point.x+target.x)/2][(point.y+ target.y)/2] != null;
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        if (isOverRiver(target)) return false;
        if (Math.abs(point.x-target.x) != 2 || Math.abs(point.y-target.y) != 2) return false;
        if (isStuck(target, state)) return false;
        return true;
    }
}
