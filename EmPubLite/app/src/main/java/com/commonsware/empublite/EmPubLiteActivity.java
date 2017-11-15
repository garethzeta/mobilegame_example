package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class EmPubLiteActivity extends Activity {

    private ContentsAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    /**
     * @param menu
     * for each button if clicked an intent to start
     * Game activity is started with an extra
     * int for the level/multiplier
     */
    public void onClickLevel(View menu) {
        Intent i;

        switch (menu.getId()) {
            case R.id.Level_1:
                i = new Intent(this, Game.class)
                        .putExtra("Level", 1);
                startActivity(i);
                break;


            case R.id.Level_2:
                i = new Intent(this, Game.class)
                        .putExtra("Level", 2);
                startActivity(i);
                break;

            case R.id.Level_3:
                i = new Intent(this, Game.class)
                        .putExtra("Level", 3);
                startActivity(i);
                break;
            case R.id.Level_4:
                i = new Intent(this, Game.class)
                        .putExtra("Level", 4);
                startActivity(i);
                break;
            case R.id.Level_5:
                i = new Intent(this, Game.class)
                        .putExtra("Level", 5);
                startActivity(i);
                break;
        }
    }


    /**
     * @param item
     * @return
     * if the menu buttons are clicked for the about pages
     * are loaded in simple content
     * activity
     */
    @Override
    public	boolean	onOptionsItemSelected(MenuItem	item)	{
        switch	(item.getItemId())	{
            case	R.id.about:
                Intent	i	=	new	Intent(this,	SimpleContentActivity.class)
                        .putExtra(SimpleContentActivity.EXTRA_FILE,
                                "file:///android_asset/misc/about.html");
                startActivity(i);
                return(true);
            case	R.id.help:
                i	=	new	Intent(this,	SimpleContentActivity.class)
                        .putExtra(SimpleContentActivity.EXTRA_FILE,
                                "file:///android_asset/misc/help.html");
                startActivity(i);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }
}
