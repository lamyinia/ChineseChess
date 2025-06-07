package org.com.game.role;


import java.awt.*;

public class Guard extends Chess {
    public Guard(String name, boolean group, Point point){
        super(name, group, point);
    }
    @Override
    public boolean isAbleMove() {
        return false;
    }
}
