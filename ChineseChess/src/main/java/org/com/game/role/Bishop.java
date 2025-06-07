package org.com.game.role;

import java.awt.*;

public class Bishop extends Chess {

    public Bishop(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove() {
        return true;
    }
}
