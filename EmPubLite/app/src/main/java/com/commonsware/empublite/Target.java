package com.commonsware.empublite;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import static com.commonsware.empublite.GamePanel.WIDTH;


public class Target extends Sprites {
    private Bitmap Targetimage;
    private int speed;
    // goes right first
    private String direction ="right";

    /**
     * @param image bitmap of the target
     * @param x x co-ord
     * @param y y-co-ord
     * @param speedmulti -level multiple for speed
     */
    public Target(Bitmap image, int x , int y,  int speedmulti){

        super.x = x;
        super.y =y;
        speed = 5*speedmulti;

        Targetimage = Bitmap.createBitmap(image,0,0,200,200);
        super.width = Targetimage.getWidth();
        super.height = Targetimage.getHeight();
    }

    /**
     * @param or Orientation
     *   This updates the x,y values of the target, and postions if Orientation chnages
     */
    public void update(int or){
        if (or == 1) {
            // so it rebounds off walls
            if (x < 0 || x > WIDTH-200) {
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
            if (y < 0 || y > WIDTH-200) {
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
     * @param canvas the canvas
     * @param or the orientation
     *           this draws the Target onto the screen dependant on the Orientation
     */
    public void draw(Canvas canvas, int or){
        if (or == 1) {
            try {
                canvas.drawBitmap(Targetimage, x, y, null);
            } catch (Exception e) {
            }
        }
        else{
            try {
                canvas.drawBitmap(Targetimage, y, x, null);
            } catch (Exception e) {
            }

        }
    }

}
