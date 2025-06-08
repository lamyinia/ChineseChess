package org.com.game.role;

import org.com.game.state.GameState;

import java.awt.*;

public class General extends Chess {
    public General(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        if (!isAtHome(target)) return false;
        return Math.abs(point.x-target.x)+Math.abs(point.y-target.y) == 1;
    }
}
