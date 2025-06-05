package org.com.chracters;

import java.awt.*;
import java.io.Serializable;

public abstract class Chess implements Serializable {
    private static final int SIZE = 80;
    private static final int MARGIN = 40;
    private static final int SPACE = 40;
    private String name;
    protected int player; // 0:红，1:黑
    protected Point point;
}
