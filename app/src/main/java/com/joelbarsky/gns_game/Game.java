package com.joelbarsky.gns_game;

import android.content.pm.ActivityInfo;
import android.app.Activity;
import android.os.Bundle;
import android.content.*;
import android.view.*;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public class Game extends Activity implements OnTouchListener {

    private static float x, y = 0;
    private static boolean inContact = false;
    private static boolean savingStep = true;
    private static boolean inUndo = false;
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static boolean inSnapMode = false;
    private static boolean addingStack = false;


    private static Deck deck = new Deck();
    DrawingTheDeck v;


    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels; //x resolution
        screenHeight = metrics.heightPixels; //y resolution
        //System.out.println(screenWidth);
        //System.out.println(screenHeight);
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
        // for undo button action

        // for moving the cards

            switch (event.getAction()) {
                case (MotionEvent.ACTION_DOWN):
                    if (Math.abs(event.getX() - DrawingTheDeck.getUndoXCoordinate()) < DrawingTheDeck.getUndoWidth()
                            && Math.abs(event.getY() - DrawingTheDeck.getUndoYCoordinate()) < DrawingTheDeck.getUndoHeight()){
                        inUndo = true;
                        addingStack = false;
                    }
                    else {
                        addingStack = true;
                        inUndo = false;
                        x = event.getX();
                        y = event.getY();
                    }
                    break;
                case (MotionEvent.ACTION_MOVE):

                    inUndo = false;
                    inContact = true;
                    x = event.getX();
                    y = event.getY();

                    break;
                case (MotionEvent.ACTION_UP):
                    savingStep = true;

                    inContact = false;
                    inSnapMode = true;
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

    public static boolean isSavingStep() {
        return savingStep;
    }

    public static void setSavingStep(boolean savingStep) {
        Game.savingStep = savingStep;
    }

    public static void setInUndo(boolean inUndo) {
        Game.inUndo = inUndo;
    }
    public static boolean isInUndo() {

        return inUndo;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static boolean isInSnapMode() {
        return inSnapMode;
    }

    public static void setInSnapMode(boolean inSnapMode) {
        Game.inSnapMode = inSnapMode;
    }

    public static boolean isAddingStack() {
        return addingStack;
    }
}