package org.com.game.role;

import org.com.game.state.GameState;

import java.awt.*;

public class Solider extends Chess {
    public Solider(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        if (!isOneStep(target)) return false;
        if (isGoForward(target)) return true;

        return isOverRiver(point) && isGoSide(target);
    }
}