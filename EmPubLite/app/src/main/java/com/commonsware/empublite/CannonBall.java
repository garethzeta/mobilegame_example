package com.commonsware.empublite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import static com.commonsware.empublite.GamePanel.HEIGHT;
import static com.commonsware.empublite.GamePanel.WIDTH;

public class CannonBall extends Sprites {
    private Bitmap spriteimage;
    private int speed;
    private float dx; //speed on x
    private float dy; // speed on y
    private float gradient;
    private boolean hitb= false;

    /**
     * @param image Bitmap of cannon ball
     * @param c_x its x
     * @param c_y its y
     * @param tar_x the clicked x
     * @param tar_y the clicked y
     *  this creates a Cannon ball
     */
    public CannonBall(Bitmap image, int c_x,int c_y, float tar_x, float tar_y) {

        speed = 45;// needs its own speed
        super.x = c_x;
        super.y = c_y;
        this.gradient = get_grad(tar_x,tar_y);
        this.dx = speed*(1/gradient);
        this.dy = dx*gradient;
        if (dy<0){
            dy =dy*-1; // stops the cannon ball going backwards on start
        }
        //speed = 5;// needs its own speed

        spriteimage = Bitmap.createBitmap(image, 0, 0, 200, 188);
        super.width = spriteimage.getWidth();
        super.height = spriteimage.getHeight();
    }

    /**
     * if the cannonball hits a block
     * its x and y and times by -1
     * to have a bounce look effect
     */
    public void hitblock(){
        dy = -dy;
        dx =dx*-1;
        hitb = true; // set hit on blocker
    }

    /**
     * @return true if cannonball has
     * already hit a blocker
     */
    public boolean beenhit(){
        return hitb;
    }

    /**
     * @param tar_x click co-ord x
     * @param tar_y click co-ord y
     * @return the gradient
     * gets the gradient of the touch from the
     * middle of the bottom of the scree
     */
    public float get_grad(float tar_x, float tar_y){
        float dif_x = tar_x-((WIDTH/2)-25);
        float dif_y = (HEIGHT-tar_y);
        return dif_y/dif_x;

    }

    /**
     * for increasing x and y each update
     */
    public void update(){
        x+=dx;
        y-=dy;

    }

    /**
     * @param canvas
     * draws the cannon ball
     */
    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(spriteimage,x,y,null);
        }catch(Exception e){}
    }

}

