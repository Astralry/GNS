package com.joelbarsky.gns_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by kevin on 2017-10-25.
 * This class is responsible for drawing the deck
 */

public class DrawingTheDeck extends View {

    private Bitmap ss;

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

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);

        Deck deck = Game.getDeck();

//        canvas.drawText(deck.printCardFromDeck(deck, 1), 200, 200, paint);
        int[] xy;
        for (int i = 0; i < 52 ; i++){
            xy = calculatePosition(i);
            canvas.drawBitmap(getSubImage(deck,i), xy[0], xy[1], null);
        }
    }

    //Returns the x and y coordinate of the position with index pos
    public int[] calculatePosition(int pos){
        int[] xy = {0,0};
        int rowSpacing = 100;
        int colSpacing = 100;
        int rowWidth = 13;
        xy[0] = (pos % rowWidth) * rowSpacing + rowSpacing;
        xy[1] = (pos / rowWidth) * colSpacing + colSpacing;
        return xy;
        //TODO arrange them like the board
    }
    //Returns the sub image of the card from deck at position pos
    public Bitmap getSubImage(Deck deck, int pos){
        ArrayList<Card> cards = deck.getCards();
        Card card = cards.get(pos);

        int rank = card.getRank();
        String suit = card.getSuit();

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
}
