package com.commonsware.empublite;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import static com.commonsware.empublite.GamePanel.WIDTH;


public class Blocker extends Sprites {

    private Bitmap blockerimage;
    private double speed;
    //goes left first
    private String direction ="left";

    /**
     * @param image blockers image
     * @param x x co-ord
     * @param y y-cord
     * @param speedmulti -multipler for speed of blocker
     */
    public Blocker(Bitmap image,int x,int y, double speedmulti){

        super.x = x;
        super.y =y;

        blockerimage = Bitmap.createBitmap(image,0,0,200,200);

        super.width = blockerimage.getWidth();
        super.height = blockerimage.getHeight();
        speed = 10*speedmulti;

    }

    public void update(int or){
        if (or == 1) {
            // makes sure it rebounds off walls
            if (x < 0 || x > WIDTH-width) {
                if (direction.equals("left")) {
                    x += speed;
                    direction = "right";
                } else {
                    x -= speed;
                    direction = "left";
                }
            }
            if (direction.equals("left")) {
                x -= speed;
            }
            if (direction.equals("right")) {
                x += speed;
            }
        }
        else{ //up(left) and downs(right)
            if (y < 0 || y > WIDTH-width) {
                if (direction.equals("left")) {
                    y += speed;
                    direction = "right";
                } else {
                    y -= speed;
                    direction = "left";
                }
            }
            if (direction.equals("left")) {
                y -= speed;
            }
            if (direction.equals("right")) {
                y += speed;
            }
        }


    }


    /**
     * @param canvas
     * @param or Orientation
     *           draws Blocker onto the canvas dependant on orientation
     */
    public void draw(Canvas canvas,int or){
        if (or == 1) {
            try {
                canvas.drawBitmap(blockerimage, x, y, null);
            } catch (Exception e) {
            }
        }
        else{
            try {
                canvas.drawBitmap(blockerimage, y, x, null);
            } catch (Exception e) {
            }

        }
    }

}
