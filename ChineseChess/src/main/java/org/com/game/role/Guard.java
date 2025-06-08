package org.com.game.role;


import org.com.game.state.GameState;

import java.awt.*;

public class Guard extends Chess {
    public Guard(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        return Math.abs(target.x-point.x) == 1 && Math.abs(target.y-point.y) == 1 && isAtHome(target);
    }
}
