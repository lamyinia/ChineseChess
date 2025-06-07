package org.com.game.role;

import java.awt.*;

public class ChessFactory {
    private ChessFactory(){}
    public static Chess create(String name, boolean group, Point point){
        switch (name){
            case "Bishop":
                return new Bishop(name, group, point);
            case "Cannon":
                return new Cannon(name, group, point);
            case "General":
                return new General(name, group, point);
            case "Guard":
                return new Guard(name, group, point);
            case "Horse":
                return new Horse(name, group, point);
            case "Rook":
                return new Rook(name, group, point);
            case "Solider":
                return new Solider(name, group, point);
            default:
                return null;
        }
    }
}
