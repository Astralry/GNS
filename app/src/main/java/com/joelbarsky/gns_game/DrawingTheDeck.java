package com.joelbarsky.gns_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Matrix;

import java.util.ArrayList;

/**
 * Created by kevin on 2017-10-25.
 * This class is responsible for drawing the deck
 */

public class DrawingTheDeck extends SurfaceView implements Runnable{

    private Bitmap ss;
    private Bitmap undo;
    private static int undoXCoordinate = 100;
    private static int undoYCoordinate = 10;

    private static int undoWidth = 117;
    private static int undoHeight = 117;


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
        undo = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.undo), undoWidth, undoHeight);
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


        //index of 100 means no card was touched

        //loop through all cards

//        if(Game.isInContact()) {
            for (int i = 0; i < 52; i++) {
                if (x > allX[i] - (81 / 2) && x < allX[i] + (81 / 2) && y > allY[i] - (117 / 2) && y < allY[i] + (117 / 2)) {
                    index = i;
                }
            }
//        }
//        if (!Game.isInContact()){
//            index = 100;
//        }


        //if a card is touched, update its position
        if (Game.isInContact()){

            allX[index] = x;
            allY[index] = y;
        }
        else {
            allX[index] = snapX(x);
            allY[index] = snapY(y);
        }
    }

    //DRAWING METHOD
    public void render(Canvas c){
        c.drawARGB(255, 26, 99, 1);
        c.drawBitmap(undo, undoXCoordinate, undoYCoordinate, null);
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

    // snapping to coordinates
    public int snapX(float x){
        int xCoordinate = 0;
        for (int i = 0; i < 52; i++){
            if (Math.abs(x - snappingX[i]) < colSpacing/2){
                xCoordinate = i;
            }
        }
        return snappingX[xCoordinate];
    }

    public int snapY(float y){
        int yCoordinate = 0;
        for (int i = 0; i < 52; i++){
            if (Math.abs(y - snappingY[i]) < rowSpacing/2){
                yCoordinate = i;
            }
        }
        return snappingY[yCoordinate];
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    // getter and setter
    public int getColSpacing() {
        return colSpacing;
    }

    public int getRowSpacing() {
        return rowSpacing;
    }

    public int getRowWidth() {
        return rowWidth;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    public static int getUndoXCoordinate() {
        return undoXCoordinate;
    }

    public static int getUndoYCoordinate() {
        return undoYCoordinate;
    }

    public static int getUndoWidth() {
        return undoWidth;
    }

    public static int getUndoHeight() {
        return undoHeight;
    }
}
