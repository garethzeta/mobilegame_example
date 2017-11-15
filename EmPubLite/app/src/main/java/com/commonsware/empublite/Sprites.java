package com.commonsware.empublite;

import android.graphics.Rect;

public abstract class Sprites {
    protected int x;
    protected int y;
    protected int height;
    protected int width;

    /**
     * @return the x co-ord
     */
    public int getX(){
        return x;
    }

    /**
     * @return the y co-ord
     */
    public int getY(){
        return y;
    }

    /**
     * @return the height of the Sprites object
     */
    public int getHeight(){
        return height;
    }

    /**
     * @returnthe width of the Sprites Object
     */
    public int getWidth(){
        return width;
    }

    /**
     * @return a rectangle of the size of the object
     * used for collison
     */
    public Rect getRectangle(){
        return new Rect(x,y,x+width,y+height);
    }


}
