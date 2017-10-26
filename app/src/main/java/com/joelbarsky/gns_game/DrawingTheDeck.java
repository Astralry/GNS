package com.joelbarsky.gns_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;




/**
 * Created by kevin on 2017-10-25.
 * This class is responsible for drawing the deck
 */

public class DrawingTheDeck extends View {

    private Bitmap ss;
    private Bitmap region;
    private InputStream is;
    private BitmapRegionDecoder decoder;

    public DrawingTheDeck(Context context) {
        super(context);
//        Drawable drawable = getResources().getDrawable(R.drawable.deck_sheet);
//        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
//        Bitmap bitmap = bitmapDrawable.getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        is = new ByteArrayInputStream(stream.toByteArray());
        //Load the spritesheet
        ss = BitmapFactory.decodeResource(getResources(),R.drawable.deck_sheet);

    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int col = 1;
        int row = 1;

        for (Rank rank: Rank.values()){
            for (Suit suit: Suit.values()){
                canvas.drawBitmap(setCard(suit.printSuit(), rank.getRank()), 100*col, 100*row, null);
                row++;
            }
            row = 1;
            col++;
        }
    }

    //Returns the sub image of the card with Suit suit and Rank rank
    public Bitmap setCard(String suit, int rank){

        int col = rank;
        int row = 0;

        if(suit.equals("HEARTS")){
            row = 1;
        } else if (suit.equals("DIAMONDS")){
            row = 2;
        } else if (suit.equals("CLUBS")){
            row = 3;
        } else if (suit.equals("SPADES")){
            row = 4;
        }

        int y = 351*(row - 1);
        int x = 243*(col - 1);

        Bitmap resized = Bitmap.createBitmap(ss, x, y, 243, 351);
        return resized;
    }

    //Returns the card at the position pos in the deck
    public void parseDeck (Deck deck, int pos){

    }
}
