package org.com.game.role;

import org.com.game.state.GameState;

import java.awt.*;

public class Cannon extends Chess {
    public Cannon(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        return false;
    }
}