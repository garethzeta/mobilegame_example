package com.commonsware.empublite;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static com.commonsware.empublite.Game.levelmult;
import static com.commonsware.empublite.MainThread.canvas;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    // get the width and height of the screen
    static int WIDTH  = Resources.getSystem().getDisplayMetrics().widthPixels;
    static int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;

    // get the orientation (1 for port, 2 for land)
    Configuration config = getResources().getConfiguration();
    int Orientation = config.orientation;

    private int topscore= 0;
    private int numofshotsfired = 0;
    private int timeremoveonblocker = 2; // time to deduct on hitting blocker
    private int timeaddontarget = 3; // time to add on hitting target
    private int scoreaddonblocker =15; //base minus score for hitting blocker
    private int scoreaddontarget =10; // base score for hitting target
    private int extratime = 0;
    private int timelimit = 10;
    public Player player;
    private Context context = getContext();
    private MainThread thread;
    private Blocker Blocker;
    private ArrayList<Target> Targets= new ArrayList<>(); //holds all targets
    private ArrayList<CannonBall> cannonballs= new ArrayList<>(); //holds all cannonballs
    private static boolean Game_running;
    private boolean win;
    private MediaPlayer blockersound;
    private MediaPlayer targetsound;
    private MediaPlayer cannonsound;
    Paint paint;
    Handler handler;
    AlertDialog AD;
    private long starttime = System.currentTimeMillis()/1000;

    /**
     * @param context
     * @param highscore the current highscore
     *    sets the game up to start
     */
    public GamePanel(Context context,int highscore)
    {
        super(context);

        //load sound effect files
        blockersound =  MediaPlayer.create(context,R.raw.blockersoundm);
        cannonsound = MediaPlayer.create(context,R.raw.cannonsound);
        targetsound = MediaPlayer.create(context,R.raw.targetsound);

        //set the high score
        topscore = highscore;
        paint = new Paint();
        paint.setARGB(0, 0, 0, 0); // set background black
        Game_running = true;
        handler = new Handler();

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
        }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}


    /**
     * @param holder
     * used to destroy the surface
     * removes the main thread and activity so it can
     * safely go back to the main screen
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        // try to remove th thread
        while(retry && counter<1000&&(thread!=null))
        {
            counter++; // make sure it isn`t infinte
            try{thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            }catch(InterruptedException e){e.printStackTrace();}

        }
        if (AD != null){
            // get rid of the the action dialog
            AD.dismiss();
        }
            //close Game activity
            ((Activity) context).finish();


    }

    /**
     * @param holder surfaceholder
     *  when surface is created this makes the screen with all objects
     *               then starts the thread for the game
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder){
        Orientation = config.orientation;

        // load bitmaps of the images
        Bitmap playerbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cannon);
        Bitmap blockerbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blocker);
        Bitmap targetbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.target);

        // loads a player(the cannon)
        player = new Player(playerbitmap, WIDTH / 2-playerbitmap.getWidth()/2, HEIGHT - playerbitmap.getHeight());

        player.setPlaying(true);
        //loads the blocker
        Blocker = new Blocker(blockerbitmap, WIDTH / 2, HEIGHT / 2 - blockerbitmap.getHeight()*2, levelmult);
        // loads 2 times the level of targets
        for (int x = 0; x < (levelmult * 2); x++) {
            // at a random x value
            int randomx = targetbitmap.getWidth() + (int) (Math.random() * (((WIDTH - targetbitmap.getWidth()) - targetbitmap.getWidth()) + 1));
            Target t = new Target(targetbitmap, randomx, HEIGHT / 4 - targetbitmap.getHeight()*2, levelmult);
            Targets.add(t);}

        thread = new MainThread(getHolder(), this,context);
        //safely start the game loop
        thread.setRunning(true);
        thread.start();

    }

    /**
     * @param event a motion event
     * @return if screen was touched
     * if user touches screeen a cannon ball is fired
     * at the touch postions x,y co-ords
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Orientation = config.orientation;
        if (player.getPlaying()) {
            // get co-ord of touch event
            float target_x = event.getX();
            float target_y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //loads a cannon
                Bitmap cannonbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cannonball);
                CannonBall cannonspawn = new CannonBall(cannonbitmap, WIDTH / 2, HEIGHT - cannonbitmap.getHeight(), target_x, target_y);
                cannonballs.add(cannonspawn);
                cannonsound.start();
                numofshotsfired+=1;
                return true;

            }
            return super.onTouchEvent(event);
        }
        return true;
    }


    /**
     * The update statment for updating all the
     * x,y postions of the objects
     */
    public void update() {
        // check if game is valid and there are still targets to hit on the screen
        if (player.getPlaying() && (Targets.size() != 0)) {
            int Orientation = config.orientation;
            //update blocker
            Blocker.update(Orientation);
            //update all targets on screen
            for (Target t : Targets) {
                t.update(Orientation);
            }
            //checks cannonballs if any have left the screen and removes them
            // to save computation time on invalid cannon balls
            for (CannonBall c : cannonballs) {
                if ((c.getX()> (WIDTH+c.getWidth()))|| (c.getX()<(0 -c.getWidth()))){
                    cannonballs.remove(c);
                    break;
                }
                if ((c.getY()>(HEIGHT+c.getHeight()))|| (c.getY()<(0-c.getHeight()))){
                    cannonballs.remove(c);
                    break;
                }
                // update valid cannonballs
                c.update();

            }
            // check for collion between cannon balls and targets
            for (CannonBall c : cannonballs) {
                for (Target t : Targets) {
                    if (collision(c, t)) {
                        // adds score by defult 10(scoreaddtarget) by level(levelmult)
                        player.addScorce(scoreaddontarget, levelmult);
                        // play target sound
                        targetsound.start();
                        // add extra time 3 seconds(timeaddontarget)
                        extratime += timeaddontarget;
                        //remove that target
                        Targets.remove(t);

                    }

                }
                // check for collison between cannonballls and blocker
                if (collision(c, Blocker)) {
                    if (!c.beenhit()) {
                        player.loseScorce(scoreaddonblocker, levelmult);
                        // play blocker sound
                        blockersound.start();
                        c.hitblock();
                        // remove time from player 2 seconds(timeremoveonblocker)
                        extratime -= timeremoveonblocker;
                    }
                }
            }
            // draws text onto screen
            drawText(canvas);


        } else {
            Game_running = false;
            // win condition
            win = Targets.size() == 0;
        }
    }

    /**
     * @return if the game is still running
     * true is yes, false if no
     */
    public static boolean IsRunning()
    {
        return Game_running;
    }

    /**
     * @param canvas
     * draws onto the screen all elements
     */
    public void Draw(Canvas canvas){
        super.draw(canvas);
        player.draw(canvas,Orientation);
        Blocker.draw(canvas,Orientation);
        for (Target t :Targets){
            t.draw(canvas,Orientation);
        }
        for (CannonBall c :cannonballs){
            c.draw(canvas);
        }
        drawText(canvas);
    }

    /**
     * @param a one Object of type Sprites
     * @param b another Object of type Sprites
     * @return true if they intercept
     * used for collision detection
     */
    public boolean collision(Sprites a,Sprites b)
    {
        return Rect.intersects(a.getRectangle(), b.getRectangle());
    }

    /**
     * @param canvas
     * draws text onto the screen
     */
    public void drawText(Canvas canvas) {
        int Orientation = config.orientation;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // white text
        paint.setTextSize(50); //size of text
        long time_elapsed = (timelimit +(starttime -System.currentTimeMillis()/1000));
        long timeleft = time_elapsed + extratime;
        String score = "Score: " + player.getScore();
        String time = "Time remaining: " + timeleft;
        String topscorer = "Top Score: " + topscore;
        // check if player has ran out of time
        if (timeleft<0){

            player.setPlaying(false);
        }
        else{
            if (Orientation ==1) {
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText(score, 0, paint.getTextSize(), paint);
                canvas.drawText(time, paint.measureText(score) + 25, paint.getTextSize(), paint);
                canvas.drawText(topscorer, paint.measureText(time) + paint.measureText(score) + 50, paint.getTextSize(), paint);
            }else{
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText(score, 0, paint.getTextSize(), paint);
                    canvas.drawText(time, paint.measureText(score) + 25, paint.getTextSize(), paint);
                    canvas.drawText(topscorer, paint.measureText(time) + paint.measureText(score) + 50, paint.getTextSize(), paint);
                }
            }

    }

    /**
     * @return the game if won true, false if lost
     */
    public boolean endcondition(){
        return win;
    }

    /**
     * @return number of shots fired
     */
    public int getshots(){
        return numofshotsfired;
    }

    /**
     * @param numberfired - number of shots fired
     * @param score - players score
     * @param won - if game was run
     * @param highscores - an array of the high scores
     * @param timeel - the time elapsed
     */
    public void EndGame(final int numberfired, final int score, final boolean won, final ArrayList<Integer> highscores, final long timeel) {
        thread.setRunning(false);
        handler.post(new Runnable(){
                public void run() {
                    //create string for win
                    if (won) {
                        String message = "Congraulations! you WON! \n " + "The high scores are "+ "\n" +
                                "1: "+ highscores.get(4) +"\n" + "2: "+highscores.get(3) + "\n" + "3: " +
                                highscores.get(2) + "\n" + "4: " +highscores.get(1) + "\n"
                                + "5: " +highscores.get(0)+ "\n" +"Time elapsed: "+timeel ;
                        AlertDialog alertdialog = make_Alert(message,numberfired, score);
                        alertdialog.show();
                    }
                    //create string for lose
                    else{
                        String message = "Unlucky! you LOSE! \n" + "The high scores are "+ "\n" +"1: "
                                + highscores.get(4) +"\n" + "2: "+highscores.get(3) + "\n" + "3: " +
                                highscores.get(2) + "\n" + "4: " +highscores.get(1) + "\n" +
                                "5: " +highscores.get(0) + "\n" + "Time elapsed: "+timeel ;
                        AlertDialog alertdialog = make_Alert(message,numberfired, score);
                        alertdialog.show();
                    }
                }

        });



    }

    /**
     * @param message - the custom win/lose message
     * @param numberfired - number of shots fired
     * @param score - the players score
     * @return - the end alertdialog
     */
    public AlertDialog make_Alert(String message, int numberfired, int score){
        //create alert
        AD = new AlertDialog.Builder(context)
        .setTitle("Game Over")
        .setMessage(message +
                "\n Number of shots Fired : " + numberfired +
                "\n Score : "+ score)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                surfaceDestroyed(getHolder());
            }
        })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    return AD;
    }

    /**
     * @return timeel - the amount of time elapsed
     */
    public long gettimeelapsed (){
        return ((((System.currentTimeMillis()/1000))-starttime));
    }
}