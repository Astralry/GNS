package com.joelbarsky.gns_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;


public class Home extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    public void game_screen(View v){
        Intent toGame = new Intent(v.getContext(), Game.class);
        startActivityForResult(toGame, 0);
    }

}
