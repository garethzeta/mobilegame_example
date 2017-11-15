package com.commonsware.empublite;

/**
 * Created by Gareth on 21/03/2017.
**/
import android.graphics.Bitmap;
import android.graphics.Canvas;

import static com.commonsware.empublite.MainThread.canvas;


/**
 * Created by Gareth on 20/03/2017.
 */

public class Player extends Sprites {
    private Bitmap playerimage;
    private int score;
    private boolean playing;
    private long startTime;

    public Canvas canvas;

    public Player(Bitmap image,int x,int y){
        super.x =x;
        super.y =y;
        score = 0;
        playerimage = Bitmap.createBitmap(image,0,0,600,536);
        startTime = System.nanoTime();
    }

    /**
     * @param canvas
     * @param or Orientation
     *  This is used to draw the player onto the canvas
     */
    public void draw(Canvas canvas,int or) {
        this.canvas = canvas;

        canvas.drawBitmap(playerimage, x, y, null);
    }

    /**
     * @return the score of the player
     */
    public int getScore(){
        return score;
    }

    /**
     * @param i the base addition number
     * @param levelmult the multiplier
     *  This is used to add score to the player
     */
    public void addScorce(int i,float levelmult){
        score += i*levelmult;
    }

    /**
     * @param i the base lose number
     * @param levelmult the multipler
     * This is used to subtract score frmo the player
     * but never allows the score to be under 0
     */
    public void loseScorce(int i,float levelmult){
        score -= i*levelmult;
        if (score<=0){
            score = 0;
        }
    }

    /**
     * @return if the player is playing
     */
    public boolean getPlaying(){
        return playing;
    }

    /**
     * @param b for setting the game
     */
    public void setPlaying(boolean b){
        playing = b;
    }
}
