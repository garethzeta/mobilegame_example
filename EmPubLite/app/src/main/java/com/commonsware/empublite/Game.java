package com.commonsware.empublite;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * The activity for the Game
 */
public class Game extends Activity {
    GamePanel gameView;
    static int levelmult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // get level from clicked button
        Intent intent = getIntent();
        int level = intent.getIntExtra("Level",0);
        levelmult = level;

        // open shared preference to get high score
        SharedPreferences prefs = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //if sharedpreferences hasnt been used e.g. first time
        if (prefs.getString("myPrefs",null)==null){
            // if new player high scores are all zero
            ArrayList<Integer> highscores = new ArrayList<>(5);
            for (int i=0;i<5;i++){
                highscores.add(0);
                // add 5 zeros
            }
            // convert to string
            StringBuilder sb = new StringBuilder();
            for (int i = highscores.size() - 1; i >= 0; i--) {
                int num = highscores.get(i);
                sb.append(num);
                sb.append(",");
            }
            String result = sb.toString();
            editor.putString("myPrefs",result);
            // creates 5 high scores of 0
            editor.commit();
        }
        // now checks for the high score
        String p =prefs.getString("myPrefs",null);
        List<String> myList = new ArrayList<String>(Arrays.asList(p.split(",")));
        ArrayList<Integer> highscores = new ArrayList<>(5);
        for (int i = 0; i<=myList.size()-1;i++){
            highscores.add(Integer.parseInt(myList.get(i)));

        }
        //sort the array
        Collections.sort(highscores);
        //last element is the highest
        int high_score = highscores.get(highscores.size()-1);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //create the game view
        gameView = new GamePanel(this,high_score);
        setContentView(gameView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_game, menu);
        return true;
    }
}