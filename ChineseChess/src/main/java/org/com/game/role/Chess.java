package org.com.game.role;

import org.com.game.state.GameState;
import org.com.tools.GameRoomTool;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

import static org.com.tools.GameRoomTool.SIZE;

public abstract class Chess implements Serializable {
    private String name;
    protected final boolean group; // 0:红，1:黑
    protected Point point;
    protected int imgX, imgY;

    public Chess(String name, boolean group, Point point){
        this.name = name;
        this.group = group;
        this.point = point;
    }
    public abstract boolean isAbleMove(Point target, GameState state);

    public void setPoint(Point point) {
        this.point = point;
    }
    public boolean getGroup() {
        return group;
    }
    public Point getPoint() {
        return point;
    }

    public void getImageXY(){
        imgX = GameRoomTool.MARGIN - GameRoomTool.SIZE/2 + point.y * GameRoomTool.SPACE;
        imgY = GameRoomTool.MARGIN - GameRoomTool.SIZE/2 + point.x * GameRoomTool.SPACE;
    }
    public void drawSelection(Graphics g){
        getImageXY();
        g.drawRect(imgX, imgY, SIZE, SIZE);
    }
    public void drawChess(Graphics g, JPanel panel){
        getImageXY();
        String path = GameRoomTool.VIEW_BASE_PATH + (group==false ? "Red" : "Black") + name + GameRoomTool.IMG_SUFFIX;
        Image image = Toolkit.getDefaultToolkit().getImage(path);
        g.drawImage(image, imgX, imgY, SIZE, SIZE, panel);
    }

    public boolean isOverRiver(Point target){
        return group==false ? target.x > 4 : target.x <= 4;
    }
    public boolean isAtHome(Point target){
        if (target.y < 3 || target.y > 5) return false;
        return group==false ? target.x >= 0 && target.x <= 2 : target.x >= 7 && target.x <= 9;
    }
}
