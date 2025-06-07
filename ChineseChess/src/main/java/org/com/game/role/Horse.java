package org.com.game.role;

import java.awt.*;

public class Horse extends Chess {
    public Horse(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove() {
        return false;
    }
}