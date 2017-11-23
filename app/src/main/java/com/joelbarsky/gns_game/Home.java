package com.joelbarsky.gns_game;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.content.*;
import android.view.*;


public class Home extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    public void game_screen(View v){
        Intent toGame = new Intent(v.getContext(), Game.class);
        startActivityForResult(toGame, 0);
    }
    public void about_screen(View v){
        Intent toGame = new Intent(v.getContext(), About.class);
        startActivityForResult(toGame, 0);
    }
    public void how_screen(View v){
        Intent toGame = new Intent(v.getContext(), How.class);
        startActivityForResult(toGame, 0);
    }

}
