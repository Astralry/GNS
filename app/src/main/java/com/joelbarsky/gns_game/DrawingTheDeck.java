package com.joelbarsky.gns_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

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
        ss = BitmapFactory.decodeResource(getResources(),R.drawable.deck_sheet3);

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
            xy = calculatePosition(deck, i);
            canvas.drawBitmap(getSubImage(deck,i), xy[0], xy[1], null);
        }
    }

    //Returns the x and y coordinate of the position with index pos
    public int[] calculatePosition(Deck deck, int index){
        int[] xy = {0,0};
        int colSpacing = 100;
        int rowSpacing = 130;
        int rowWidth = 10;
        int cardWidth = 81;
        int cardSpacing = colSpacing - cardWidth;

        setupFoundation(deck);

        //first block
        if (index < 20) {
            xy[0] = (index % (rowWidth / 2)) * colSpacing + colSpacing;
            xy[1] = (index / (rowWidth / 2)) * rowSpacing + rowSpacing;
            //cellar left row
        } else if (index > 19 && index < 24){
            xy[0] = (index % (rowWidth / 2)) * colSpacing + 2*colSpacing;
            xy[1] = (index / (rowWidth / 2)) * rowSpacing + rowSpacing;
            //right block
        } else if (index > 23 && index < 48){
            xy[0] = ((index - 24) % (rowWidth / 2)) * colSpacing + cardWidth + 6*colSpacing + cardSpacing;
            xy[1] = ((index - 24) / (rowWidth / 2)) * rowSpacing + rowSpacing;
            //foundation
        } else if (index > 47){
            xy[0] = 6 * colSpacing;
            xy[1] = (index - 48)*rowSpacing + rowSpacing/2;
        }
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

        int y = 117*(row - 1);
        int x = 81*(col - 1);

        Bitmap resized = Bitmap.createBitmap(ss, x, y, 81, 117);
        return resized;
    }
    //setup deck foundation
    public void setupFoundation (Deck deck){
        ArrayList<Card> cards = deck.getCards();
        //Foundation of the board must be of the same rank;
        Card tempCard;

        //swap 1st card and foundation card
        Card foundationCard = cards.get(48);
        int foundationRank = foundationCard.getRank();
        tempCard = cards.get(48);
        cards.set(48, cards.get(0));
        cards.set(0, tempCard);
        int n = 48;

        //Search the deck for cards with the foundationRank
        for (int i = 0; i < 48; i++){
            if (cards.get(i).getRank() == foundationRank){
                tempCard = cards.get(i);
                cards.set(i, cards.get(n));
                cards.set(n, tempCard);
                n++;
            }
        }
        Game.setDeck(deck);
    }
}
