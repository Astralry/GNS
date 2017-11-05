package com.joelbarsky.gns_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.FloatMath;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kevin on 2017-10-25.
 * This class is responsible for drawing the deck
 */

public class DrawingTheDeck extends SurfaceView implements Runnable{

    static Deck[] gameBoard = new Deck[15];
    private Bitmap ss;
    private int width = 81;
    private int height = 117;

    Thread t = null;
    SurfaceHolder holder;
    boolean running = false;

    private float[] allX;
    private float[] allY;
    private int[] snappingX = new int[52];
    private int[] snappingY = new int[52];
    private int index = 0;

    private int colSpacing = 100;
    private int rowSpacing = 130;
    private int rowWidth = 10;
    private int cardWidth = 81;
    private int cardHeight = 117;
    private int cardSpacing = colSpacing - cardWidth;

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
        allY = new float[52];

        //setup board
        for (int i = 0; i < 52; i++){
            xy = setupPosition(i);
            allX[i] = xy[0];
            allY[i] = xy[1];
            snappingX[i] = xy[0];
            snappingY[i] = xy[1];
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

            frames++;
            Canvas c = holder.lockCanvas();
            render(c);
            holder.unlockCanvasAndPost(c);
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                updates = 0;
                frames = 0;
            }
        }
    }

    //ACTION METHOD
    public void tick(){
        moveCard();

    }

    //Moves each card individually
    public void moveCard(){
        //Get the x and y of the touch location
        float x = Game.getX();
        float y = Game.getY();
        boolean inContact = Game.isInContact();



        //index of 100 means no card was touched

        //loop through all cards to find the index of the touched card
        if (!inContact) {
            index = 100;
            for (int i = 0; i < 52; i++) {
                if (x > allX[i] - (81 / 2) && x < allX[i] + (81 / 2) && y > allY[i] - (117 / 2) && y < allY[i] + (117 / 2)) {
                    index = i;
                }
            }
        }

        //if a card is touched, update its position
        if (index != 100 && inContact&&isMoveable()){
            allX[index] = x;
            allY[index] = y;
        } else if (index != 100){
            allX[index] = snap(x, y)[0];
            allY[index] = snap(x, y)[1];
        }
    }
    public void setupGameBoard(){
        for (int i=0;i<15;i++){
            gameBoard[i] = new Deck();
        }
        for (int i=0; i<52; i++) {
            Card c = deck.getCards().get(i);
            //left side
            System.out.println(i);
            if (i < 24) {
                gameBoard[i/5].add(c);
            }
            //foundation
            else if (i > 47) {
                gameBoard[i - 37].add(c);
            }
            //rightside, rightmost card is position 0
            else {
                gameBoard[(i - 24) / 5 + 5].addAt(0,c);
            }
        }
    }

    //DRAWING METHOD
    public void render(Canvas c){
        c.drawARGB(255, 26, 99, 1);
        for (int i = 0; i < 52; i++) {
            c.drawBitmap(getSubImage(deck, i), allX[i] - (81/2), allY[i] - (117/2), null);
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);

        c.drawText(Game.getX()+ " x,y " + Game.getY() + " \n " + Game.isInContact() + index, 1000, 300, paint);
        c.drawText(frames + " FPS, ticks " + updates, 1000, 400, paint);

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
    public int[] setupPosition(int index) {
        int xy[] = {0,0};

        //first block
        if (index < 20) {
            xy[0] = (index % (rowWidth / 2)) * colSpacing + colSpacing;
            xy[1] = (index / (rowWidth / 2)) * rowSpacing + 2*rowSpacing;
            //cellar left row
        } else if (index > 19 && index < 24) {
            xy[0] = (index % (rowWidth / 2)) * colSpacing + 2 * colSpacing;
            xy[1] = (index / (rowWidth / 2)) * rowSpacing + 2*rowSpacing;
            //right block
        } else if (index > 23 && index < 48) {
            xy[0] = ((index - 24) % (rowWidth / 2)) * colSpacing + cardWidth + 6 * colSpacing + cardSpacing;
            xy[1] = ((index - 24) / (rowWidth / 2)) * rowSpacing + 2*rowSpacing;
            //foundation
        } else if (index > 47) {
            xy[0] = 6 * colSpacing;
            xy[1] = (index - 48) * rowSpacing + rowSpacing * 3 / 2;
        }
        return xy;
    }

    //Returns the sub image of the card from deck at position pos
    public Bitmap getSubImage(Deck deck, int pos) {
        ArrayList<Card> cards = deck.getCards();
        Card card = cards.get(pos);

        int rank = card.getRank();
        String suit = card.getSuit();

        int row = 0;

        switch (suit) {
            case "HEARTS":
                row = 1;
                break;
            case "DIAMONDS":
                row = 2;
                break;
            case "CLUBS":
                row = 3;
                break;
            case "SPADES":
                row = 4;
                break;
        }

        int y = 117 * (row - 1);
        int x = 81 * (rank - 1);

        return Bitmap.createBitmap(ss, x, y, 81, 117);
    }

    public float[] snap(float x, float y){
        int index = 0;
        float[] xy = {0,0};
        double dist = 0;
        double minDist = 10000;

        for (int i = 0; i < 52; i++){
            dist = Math.sqrt(((x - snappingX[i]) * (x - snappingX[i]) + (y - snappingY[i]) * (y - snappingY[i])));
            if (dist < minDist){
                minDist = dist;
                index = i;
            }
        }

        xy[0] = snappingX[index];
        xy[1] = snappingY[index];

        return xy;
    }
    public boolean isMoveable() {
        //Get card at location on board
        Card card = returnCard();
        int index = findCard(card);
        return !(inCellar(card) && !cellarOpen())&&!inStack(card) && gameBoard[index].deckindex(card) == 0;
    }
    //true if touching stack card
    public boolean inStack(Card c){
        int i = findCard(c);
        return i > 10;
    }
    //True if card is in cellar
    public boolean inCellar(Card c){
        int i = findCard(c);
        return i ==10;
    }
    //true if bottom right OR bottom left pile is empty
    public boolean cellarOpen(){
        return gameBoard[4].isEmpty() || gameBoard[9].isEmpty();
    }
    //returns pile with card
    public int findCard (Card c){
        int i = 0;

        while (i<16){
            if (gameBoard[i].contains(c)){
                break;
            }
            else{
                i++;
            }
        }
        return i;
    }
    public Card returnCard(){
        deck = Game.getDeck();
        //Get the x and y of the touch location
        float x = Game.getX();
        float y = Game.getY();

        //index of 100 means no card was touched

        //loop through all cards
        if (!Game.isInContact()) {
            for (int i = 0; i < 52; i++) {
                if (x > allX[i] - (81 / 2) && x < allX[i] + (81 / 2) && y > allY[i] - (117 / 2) && y < allY[i] + (117 / 2)) {
                    index = i;
                }
            }
        }
        return deck.getCard(index);
    }

}

