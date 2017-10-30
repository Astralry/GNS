package com.joelbarsky.gns_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by kevin on 2017-10-25.
 * This class is responsible for drawing the deck
 */

public class DrawingTheDeck extends SurfaceView implements Runnable{

    private Bitmap ss;
    boolean init = false;

    Thread t = null;
    SurfaceHolder holder;
    boolean running = false;

    private float[] allX;
    private float[] allY;


    private int updates;
    private int frames;
    Deck deck;
    public int xy[] = {0,0};

    public DrawingTheDeck(Context context) {
        super(context);

        //Load the sprite sheet
        ss = BitmapFactory.decodeResource(getResources(), R.drawable.deck_sheet3);
        holder = getHolder();

        //get the deck
        deck = Game.getDeck();

        //the positions of all the cards are contained here
        allX = new float[52];
        allY = new float [52];

        //setup board
        for (int i = 0; i < 52; i++){
            xy = setupPosition(deck, i);
            allX[i] = xy[0];
            allY[i] = xy[1];
        }
    }

    @Override
    public void run() {

        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1 ){
                tick();
                updates++;
                delta--;
            }
            if (!holder.getSurface().isValid()) {
                continue;
            }


            Canvas c = holder.lockCanvas();
            render(c);
            holder.unlockCanvasAndPost(c);


            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println(updates + " Ticks, FPS " + frames);
                updates = 0;
                frames = 0;
            }
        }
    }

    //ACTION METHOD
    public void tick(){

        moveCard();

    }

    //returns the index of the touched card
    public void moveCard(){
        //Get the x and y of the touch location
        float x = Game.getX();
        float y = Game.getY();

        //index of 100 means no card was touched
        int index = 100;

        for (int i = 0; i < 52; i++){
            if (x > allX[i] && x < allX[i] + 81 && y > allY[i] && y < allY[i] + 81){
                index = i;
            }
        }
        if (index != 100){
            allX[index] = x;
            allY[index] = y;
        }
    }

    //DRAWING METHOD
    public void render(Canvas c){
        c.drawARGB(255, 26, 99, 1);
        for (int i = 0; i < 52; i++) {
            c.drawBitmap(getSubImage(deck, i), allX[i], allY[i], null);
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);

        c.drawText(Game.getX()+ " x,y " + Game.getY(), 1000, 300, paint);
    }

    public void pause() {
        running = false;
        while (true) {
            try {
                t.join();
                t = null;
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        running = true;
        t = new Thread(this);
        t.start();
    }

    //Returns the x and y coordinate of the position with index pos
    public int[] setupPosition(Deck deck, int index) {
        int xy[] = {0,0};
        int colSpacing = 100;
        int rowSpacing = 130;
        int rowWidth = 10;
        int cardWidth = 81;
        int cardSpacing = colSpacing - cardWidth;

        //first block
        if (index < 20) {
            xy[0] = (index % (rowWidth / 2)) * colSpacing + colSpacing;
            xy[1] = (index / (rowWidth / 2)) * rowSpacing + rowSpacing;
            //cellar left row
        } else if (index > 19 && index < 24) {
            xy[0] = (index % (rowWidth / 2)) * colSpacing + 2 * colSpacing;
            xy[1] = (index / (rowWidth / 2)) * rowSpacing + rowSpacing;
            //right block
        } else if (index > 23 && index < 48) {
            xy[0] = ((index - 24) % (rowWidth / 2)) * colSpacing + cardWidth + 6 * colSpacing + cardSpacing;
            xy[1] = ((index - 24) / (rowWidth / 2)) * rowSpacing + rowSpacing;
            //foundation
        } else if (index > 47) {
            xy[0] = 6 * colSpacing;
            xy[1] = (index - 48) * rowSpacing + rowSpacing / 2;
        }
        return xy;
        //TODO arrange them like the board

    }

    //Returns the sub image of the card from deck at position pos
    public Bitmap getSubImage(Deck deck, int pos) {
        ArrayList<Card> cards = deck.getCards();
        Card card = cards.get(pos);

        int rank = card.getRank();
        String suit = card.getSuit();

        int col = rank;
        int row = 0;

        if (suit.equals("HEARTS")) {
            row = 1;
        } else if (suit.equals("DIAMONDS")) {
            row = 2;
        } else if (suit.equals("CLUBS")) {
            row = 3;
        } else if (suit.equals("SPADES")) {
            row = 4;
        }

        int y = 117 * (row - 1);
        int x = 81 * (col - 1);

        Bitmap resized = Bitmap.createBitmap(ss, x, y, 81, 117);
        return resized;
    }
}
