package com.joelbarsky.gns_game;

import android.content.pm.ActivityInfo;
import android.app.Activity;
import android.os.Bundle;
import android.content.*;
import android.view.*;
import android.view.View.OnTouchListener;

public class Game extends Activity implements OnTouchListener {

    private static float x, y = 0;
    private static boolean inContact = false;
    private static boolean touchDown = false;



    private static Deck deck = new Deck();
    DrawingTheDeck v;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new DrawingTheDeck(this);
        v.setOnTouchListener(this);
        setContentView(v);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //        setup deck
        deck.clear();
        deck.populate();
        deck.shuffle();
        deck.setupFoundation();
        v.setupGameBoard();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
//                inContact = true;
                x = event.getX();
                y = event.getY();
                break;
            case (MotionEvent.ACTION_MOVE):
                inContact = true;
                x = event.getX();
                y = event.getY();
                break;
            case (MotionEvent.ACTION_UP):
                inContact = false;
                break;
        }
        return true;
    }

    protected void onPause() {
        super.onPause();
        v.pause();
    }

    protected void onResume() {
        super.onResume();
        v.resume();
    }

    protected void onStart() {
        super.onStart();
    }

    // return to home
    public void home_screen(View v) {
        Intent toHome = new Intent(v.getContext(), Home.class);
        startActivityForResult(toHome, 0);
    }


    //Getters and Setters
    public static Deck getDeck() {
        return deck;
    }

    public static float getX() {
        return x;
    }

    public static float getY() {
        return y;
    }

    public static boolean isInContact() {
        return inContact;
    }


}