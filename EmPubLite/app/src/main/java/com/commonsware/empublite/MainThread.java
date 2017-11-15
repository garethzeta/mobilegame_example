package com.commonsware.empublite;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainThread extends Thread {
    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;
    public Context context;

    /**
     * @param surfaceHolder
     * @param gamePanel
     * @param context
     * The main thread to run he game
     */
    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel,Context context) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        this.context = context;
    }

    /**
     * The threads run method to run entire game
     */
    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            //try locking the canvas for pixel editing
            try {
                // if game is playing
                if (GamePanel.IsRunning()) {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        // continous updating and drawing
                        this.gamePanel.update();
                        this.gamePanel.Draw(canvas);
                    }
                }
                else {
                    //once game ends the high score may need updating
                    // gets the shared preferce file
                    SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                    //sets the editor
                    SharedPreferences.Editor editor = prefs.edit();
                    //gets the high scores string
                    String p =prefs.getString("myPrefs",null);
                    // converts to an Arraylist<String> to Arraylist<Int> for comparison
                    List<String> myList = new ArrayList<String>(Arrays.asList(p.split(",")));
                    ArrayList<Integer> highscores = new ArrayList<>(6);
                    for (int i = 0; i<=myList.size()-1;i++){
                        highscores.add(Integer.parseInt(myList.get(i)));
                    }
                    // add the new players score
                    highscores.add(gamePanel.player.getScore());
                    // sort the scores
                    Collections.sort(highscores);
                    // remove the smallest
                    highscores.remove(0);
                    // makes the listarray<int> into a string
                    StringBuilder sb = new StringBuilder();
                    for (int i = highscores.size() - 1; i >= 0; i--) {
                        int num = highscores.get(i);
                        sb.append(num);
                        sb.append(",");
                    }
                    String result = sb.toString();
                    // adds this new leaderboard
                    editor.putString("myPrefs",result);
                    // finaled
                    editor.commit();
                    // run end game condition with data needed to produce AlertDialog
                   gamePanel.EndGame(gamePanel.getshots(),gamePanel.player.getScore(),gamePanel.endcondition(),highscores,gamePanel.gettimeelapsed());
                }
            }
            catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }
    }

    /**
     * @param b tru/false
     *          sets the thread conditon
     */
    public void setRunning(boolean b) {
        running = b;
    }
}