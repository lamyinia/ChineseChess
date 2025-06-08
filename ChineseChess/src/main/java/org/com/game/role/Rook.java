package org.com.game.role;

import org.com.game.state.GameState;

import java.awt.*;

public class Rook extends Chess {
    public Rook(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove(Point target, GameState state) {
        return false;
    }
}