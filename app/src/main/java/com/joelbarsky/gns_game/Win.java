package com.joelbarsky.gns_game;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Win extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
    }

    public void home_screen(View v){
        Intent toHome = new Intent(v.getContext(), Home.class);
        startActivityForResult(toHome, 0);
    }
}
