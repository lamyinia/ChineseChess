package org.com.game.role;

import java.awt.*;

public class Solider extends Chess {
    public Solider(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove() {
        return false;
    }
}