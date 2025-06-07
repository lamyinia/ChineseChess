package org.com.game.role;

import org.com.tools.GameRoomTool;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

import static org.com.tools.GameRoomTool.SIZE;

public abstract class Chess implements Serializable {
    private String name;
    protected boolean group; // 0:红，1:黑
    protected Point point;
    protected int imgX, imgY;

    public Chess(String name, boolean group, Point point){
        this.name = name;
        this.group = group;
        this.point = point;
    }
    public abstract boolean isAbleMove();
    public void getImageXY(){
        imgX = GameRoomTool.MARGIN - GameRoomTool.SIZE/2 + point.y * GameRoomTool.SPACE;
        imgY = GameRoomTool.MARGIN - GameRoomTool.SIZE/2 + point.x * GameRoomTool.SPACE;
    }

    public void drawSelection(){

    }
    public void drawChess(Graphics g, JPanel panel){
        getImageXY();
        String path = GameRoomTool.VIEW_BASE_PATH + (group==false ? "Red" : "Black") + name + GameRoomTool.IMG_SUFFIX;
        Image image = Toolkit.getDefaultToolkit().getImage(path);
        g.drawImage(image, imgX, imgY, SIZE, SIZE, panel);
    }
}
