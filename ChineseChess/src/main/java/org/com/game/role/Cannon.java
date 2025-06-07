package org.com.game.role;

import java.awt.*;

public class Cannon extends Chess {
    public Cannon(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove() {
        return false;
    }
}