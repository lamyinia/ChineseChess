package org.com.game.role;

import org.com.game.state.GameState;
import org.com.tools.GameRoomTool;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.net.URL;

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

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isGroup() {
        return group;
    }
    public Point getPoint() {
        return point;
    }
    public void setPoint(Point point) {
        this.point = point;
    }
    public int getImgX() {
        return imgX;
    }
    public void setImgX(int imgX) {
        this.imgX = imgX;
    }
    public int getImgY() {
        return imgY;
    }

    public void setImgY(int imgY) {
        this.imgY = imgY;
    }

    public void getImageXY(){
        imgX = GameRoomTool.MARGIN - GameRoomTool.SIZE/2 + point.y * GameRoomTool.SPACE;
        imgY = GameRoomTool.MARGIN - GameRoomTool.SIZE/2 + point.x * GameRoomTool.SPACE;
    }
    public void drawSelection(Graphics g){
        getImageXY();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.cyan);
        g2d.drawOval(imgX, imgY, SIZE, SIZE);
    }
    public void drawChess(Graphics g, JPanel panel){
        getImageXY();
        URL imageUrl = GameRoomTool.getChessImage(group, name);
        if (imageUrl != null) {
            Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);
            g.drawImage(image, imgX, imgY, SIZE, SIZE, panel);
        } else {
            // 处理加载失败情况
            g.setColor(Color.RED);
            g.fillOval(imgX, imgY, SIZE, SIZE);
        }
    }

    public boolean isOverRiver(Point target){
        return group==false ? target.x > 4 : target.x <= 4;
    }
    public boolean isAtHome(Point target){
        if (target.y < 3 || target.y > 5) return false;
        return group==false ? target.x >= 0 && target.x <= 2 : target.x >= 7 && target.x <= 9;
    }
    public boolean isOneStep(Point target){
        return Math.abs(point.x-target.x)+Math.abs(point.y-target.y) == 1;
    }
    public boolean isGoForward(Point target){
        if (point.y != target.y || point.x == target.x) return false;
        return group==false ? target.x > point.x : target.x < point.x;
    }
    public boolean isGoSide(Point target){
        return point.x == target.x && point.y != target.y;
    }
    public boolean isLine(Point target){
        return point.x == target.x || point.y == target.y;
    }
    public int lineCount(Point target, GameState state){
        int count = 0;
        if (point.x == target.x){
            for (int i = Math.min(point.y, target.y)+1; i < Math.max(point.y, target.y); ++ i){
                count += state.board[point.x][i] != null ? 1 : 0;
            }
        } else {
            for (int i = Math.min(point.x, target.x)+1; i < Math.max(point.x, target.x); ++ i){
                count += state.board[i][point.y] != null ? 1 : 0;
            }
        }
        return count;
    }
}
