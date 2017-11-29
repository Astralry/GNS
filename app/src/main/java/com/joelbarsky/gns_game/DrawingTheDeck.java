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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Created by kevin on 2017-10-25.
 * This class is responsible for drawing the deck
 */

public class DrawingTheDeck extends SurfaceView implements Runnable{

    static Deck[] gameBoard = new Deck[15];
    private Bitmap ss;

    // for scaling on different screen size
    // scale factor is predetermined
    private double cardWidthFactor = 81.000000/1776.000000;
    private double cardHeightFactor = 117.000000/1080.000000;
    private double colSpacingFactor = 100.000000/1776.000000;
    private double rowSpacingFactor = 130.000000/1080.000000;
    private int width = 81;
    private int height = 117;
    static boolean buildUp = true;
    static int movesToFoundation =0;

    Thread t = null;
    SurfaceHolder holder;
    boolean running = false;

    // stores the cooridinate of the cards
    private float[] allX;
    private float[] allY;

    // snapping variables
    private int[] snappingX = new int[53];
    private int[] snappingY = new int[53];
    private static boolean holdingCard = false;

    // card variables
    private int colSpacing = (int) Math.round(Game.getScreenWidth()*colSpacingFactor);
    private int rowSpacing = (int) Math.round(Game.getScreenHeight()*rowSpacingFactor);
    private int rowWidth = 10; // # of cards in each row
    private int cardWidth = (int) Math.round(Game.getScreenWidth()*cardWidthFactor);
    private int cardHeight = (int) Math.round(Game.getScreenHeight()*cardHeightFactor);
    private int cardSpacing = colSpacing - cardWidth;

    // for undo function
    // undo button variables
    private Bitmap undo;
    private static final int undoXCoordinate = 1600;
    private static final int undoYCoordinate = 350;
    private static final int undoWidth = 117;
    private static final int undoHeight = 117;
    // undo function
    private static int undoIndex = 0;
    private static int[] undoCard = new int[52];
    private static int maxUndoStep = 10;
    private static float[] undoX = new float[maxUndoStep];
    private static float[] undoY = new float[maxUndoStep];

    //undo for stacking
    //maxundo, 0 = previous index, 1 = next index
    private static int undoStackIndex = 0;
    private static int[] undoPreviousStack = new int[maxUndoStep];

    // saving function
    private Bitmap save;
    private static final int saveXCoordinate = 1800;
    private static final int saveYCoordinate = 350;
    private static final int saveWidth = 117;
    private static final int saveHeight = 117;

    //display order for render()
    private int[] displayOrder = new int[52];

    private int index = 100;

    // stacking purpose
    private int[] numCards = new int [53];
    private int offset = 20;
    private boolean removeStack = false;
    int previousIndex = 100;
    int nextIndex = 100;

    private int updates;
    private int frames;
    Deck deck;


    public int xy[] = {0,0};

    public DrawingTheDeck(Context context) {
        super(context);
        undoCard[0] = 100;
        System.out.println("col: " + colSpacing);
        System.out.println("row: " + rowSpacing);
        System.out.println("width: " + cardWidth);
        System.out.println("height: " + cardHeight);
        //Load the sprite sheet
        ss = BitmapFactory.decodeResource(getResources(), R.drawable.deck_sheet3);
        undo = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.undo), undoWidth, undoHeight);
        save = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.save), saveWidth, saveHeight);
        holder = getHolder();

        //get the deck
        deck = Game.getDeck();

        //the positions of all the cards are contained here
        allX = new float[52];
        allY = new float[52];

        //setup board
        for (int i = 0; i < 53; i++){
            xy = setupPosition(i);
            snappingX[i] = xy[0];
            snappingY[i] = xy[1];

            if (i < 52){
                allX[i] = xy[0];
                allY[i] = xy[1];
                numCards[i] = 1;
                displayOrder[i] = i;
            }
        }
        numCards[52] = 0;
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
        Deck deck = Game.getDeck();
        Card a = null;
        Card b;
        boolean isInUndo = Game.isInUndo();
        boolean isAddingStack = Game.isAddingStack();
        boolean isSavingStep = Game.isSavingStep();
        boolean isInDisplayOrder = Game.isInDisplayOrder();
        boolean isInSnapMode = Game.isInSnapMode();
        boolean isSavingStack = Game.isSavingStack();
        boolean preventUndo1 = Game.isPreventUndo1();

        //index of 100 means no card was touched
        //loop through all cards to find the index of the touched card
        if (!inContact) {
            index = 100;
            for (int j = 0; j < 52; j++) {
                int i = displayOrder[j];
                if (x > allX[i] - (cardWidth / 2) && x < allX[i] + (cardWidth / 2) && y > allY[i] - (cardHeight / 2) && y < allY[i] + (cardHeight / 2)) {
                    index = i;
                }
            }
        }
        if(index != 100){
            a = deck.getCard(index);

        }
        // if the undo button is touched, revert step
        if (isInUndo) {
            undo();
            Game.setInUndo(false);
        }

        if (isMoveable() && isAddingStack && !isInUndo) {
            double dist = 0.0;
            double minDist = 10000;
            // returns the index that the card should snap to
            for (int i = 0; i < 53; i++){
                dist = Math.sqrt(((x - snappingX[i]) * (x - snappingX[i]) + (y - snappingY[i]) * (y - snappingY[i])));
                if (dist < minDist){
                    minDist = dist;
                    previousIndex = i;
                }
            }
            Game.setAddingStack(false);
        }
        //if a card is touched, update its position
        if (index != 100  && inContact &&isMoveable()) {

            // 0 position is the oldest undo, last position is the newest
            if (isSavingStep) {
                saveUndoStep(index);
            }
            if (isInDisplayOrder){
                setRenderOrder(index);
            }
            if (isSavingStack){
                saveUndoStack();
            }

            allX[index] = x;
            allY[index] = y;
        } else if (index != 100&&isMoveable()&& isInSnapMode && !isInUndo){
            calculateStacking(x, y);
            float[] temp = snap(x, y);
            allX[index] = temp[0];
            allY[index] = temp[1];

            //int j;
            //b = returnCardAt(a,x,y);
            //if (b == null){
               int j = rowFromPos(allX[index],allY[index]);
            //}else{
              //  j = findCard(b);
            //}

            if (j > gameBoard.length && index != 100) {
                //undo();
                //Game.setInUndo(false);
            }

            else if (allowedMove(a, j)) {
                updateGameBoard(a, j);
                System.out.println("test");
                if (gameEnd()){
                    //todo go to win screen
                    System.out.println("win");
                }
            }
            else if (!preventUndo1){
                undo();
                Game.setInUndo(false);
                System.out.println("test1");
            }
        }
        // resets undo boolean
        if (isInUndo){
            Game.setInUndo(false);
        }
    }

    public void setupGameBoard(){
        movesToFoundation = 0;
        for (int i=0;i<15;i++){
            gameBoard[i] = new Deck();
        }
        for (int i=0; i<52; i++) {
            Card c = deck.getCards().get(i);
            //left side
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
        c.drawBitmap(undo, undoXCoordinate, undoYCoordinate, null);
        c.drawBitmap(save, saveXCoordinate, saveYCoordinate, null);

        for (int j = 0; j < 52; j++) {
            int i = displayOrder[j];
            c.drawBitmap(getSubImage(deck, i), allX[i] - (cardWidth/2), allY[i] - (cardHeight/2), null);
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);

        //c.drawText(Game.getX()+ " x,y " + Game.getY() + " \n " + Game.isInContact() + index, 1000, 300, paint);
        //c.drawText(frames + " FPS, ticks " + updates, 1000, 400, paint);
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
        } else if (index > 47 && index != 52) {
            xy[0] = 6 * colSpacing;
            xy[1] = (index - 48) * rowSpacing + rowSpacing * 3 / 2;
        } else if (index == 52){
            xy[0] = 6 * colSpacing;
            xy[1] = (index - 48) * rowSpacing + 5 / 2 * rowSpacing;
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

        return getResizedBitmap(Bitmap.createBitmap(ss, x, y, 81, 117), cardWidth, cardHeight);
    }

    // snap and stack at offset
    public float[] snap(float x, float y){
        float[] xy = {0,0};
        double dist = 0.0;
        double minDist = 10000;

        // returns the index that the card should snap to
        for (int i = 0; i < 53; i++){
            dist = Math.sqrt(((x - snappingX[i]) * (x - snappingX[i]) + (y - snappingY[i]) * (y - snappingY[i])));
            if (dist < minDist){
                minDist = dist;
                nextIndex = i;
            }
        }
        if (numCards[nextIndex] > 1) {
            xy[0] = snappingX[nextIndex] + offset;
            xy[1] = snappingY[nextIndex];
        }
        else{
            xy[0] = snappingX[nextIndex];
            xy[1] = snappingY[nextIndex];
        }
        Game.setInSnapMode(false);
        return xy;
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

    public boolean isMoveable() {
        //Get card at location on board
        Card card = returnCard();
        int index = findCard(card);
        return !(inCellar(card) && !cellarOpen())&&!inStack(card) && gameBoard[index].deckindex(card) == 0;
    }

    public boolean isPlayable( float x, float y){
        Card moving_card = returnCard();
        Card destination = returnCardAt(moving_card, x,y);

        int pile;
        if (destination ==null){
            pile = rowFromPos(x,y);
        }
        else{
            pile = findCard(destination);
        }
        if (pile ==100){
            return false;
        }
        return allowedMove(moving_card,pile);

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

        while (i<15){
            if (gameBoard[i].contains(c)){
                break;
            }
            else{
                i++;
            }
        }
        return i;
    }
    //returns the card that is being touched
    public Card returnCard(){
        deck = Game.getDeck();
        if (index == 100){
            return null;
        }
        Card card = deck.getCard(index);
        return card;
    }
    //return card at x and y excluding input card
    public Card returnCardAt(Card a, float x, float y){
        Deck deck = Game.getDeck();
        int index = 100;

        //cellar
        if (rowFromPos(x,y) == 10){
            if (gameBoard[10].isEmpty()){
                return null;
            } else {
                return gameBoard[10].getCard(0);
            }
        }

        Card card;
        if (!Game.isInContact()) {
            for (int i = 0; i < 52; i++) {
                if (x > allX[i] - (cardWidth / 2) && x < allX[i] + (cardWidth / 2) && y > allY[i] - (cardHeight / 2) && y < allY[i] + (cardHeight / 2)) {
                    index = i;
                    card = deck.getCard(i);
                    if(card!=a){
                        return card;
                    }

                }
            }
        }
        if (index == 100){
            return null;
        }
        card = deck.getCard(index);
        return card;
    }
    //update the gameboard
    public void updateGameBoard(Card card, int i){
        int p = findCard(card);
        gameBoard[p].remove(card);
        gameBoard[i].addAt(0, card);

    }
    //x and y input = row output
    public int rowFromPos(float x, float y){
        int index = 100;
        if (x>=11*colSpacing/2 && x<=13*colSpacing/2){
            if(y>=rowSpacing*11/2 && y<=rowSpacing*13/2){
                index = 10;
            }
            else if(y>rowSpacing && y<=rowSpacing*2){
                index = 11;
            }
            else if(y>rowSpacing*2 && y<=rowSpacing*3){
                index = 12;
            }
            else if(y>rowSpacing*3 && y<=rowSpacing*4){
                index = 13;
            }
            else if(y>rowSpacing*4 && y<=rowSpacing*5){
                index = 14;
            }

        }
        else if (x<=(11*colSpacing/2) && x >= colSpacing/2){
            if (y>=rowSpacing && y<=(5/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index = 0;
            }
            else if(y>= (5/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(10/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =1;
            }
            else if(y>= (10/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(15/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =2;
            }
            else if(y>= (15/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(20/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =3;
            }
            else if(y>= (20/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(25/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =4;
            }

        }
        else if (x>=(6*colSpacing + colSpacing/2) && x<= 12*colSpacing){
            if (y>=rowSpacing && y<=(5/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index = 5;
            }
            else if(y>= (5/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(10/ (rowWidth / 2)) * rowSpacing + rowSpacing) {
                index = 6;
            }
            else if(y>= (10/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(15/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =7;
            }
            else if(y>= (15/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(20/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =8;
            }
            else if(y>= (20/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<=(25/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =9;
            }
        }

        return index;
    }

    //true if move is valid
    public boolean allowedMove(Card a, int i){
        if(a==null || i == 100){
            return false;
        }

        Card destination;

        if (gameBoard[i].isEmpty()){
            destination = null;
        }
        else{
            destination = gameBoard[i].getCard(0);
        }

        if ((destination == null) && !(i == 4 || i ==9)){
            System.out.println("EMPTY ROW");
            return true;
        }

        else if (destination!=null){
            int diff = a.getRank() - destination.getRank();
            if (diff ==-12 && a.getRank()==1 ){
                diff = 1;
            }
            else if(diff ==12 && destination.getRank()==1){
                diff = -1;
            }
            if (a.getSuit() == destination.getSuit() && i != 4 && i != 9) {
                if (movesToFoundation==0 && i>10){
                    if (java.lang.Math.abs(diff) ==1){
                        movesToFoundation ++;
                        buildUp = diff==1;
                        return true;
                    }
                }
                else if (i > 10) {
                    if (buildUp && diff == 1) {
                        movesToFoundation ++;
                        return true;
                    } else if (!buildUp && diff == -1) {
                        movesToFoundation ++;
                        return true;
                    }
                }
                else if (i == 10 && gameBoard[10].isEmpty()) {
                    return true;
                }
                else if (java.lang.Math.abs(diff) ==1 && i!=10){
                    return true;
                }
            }
        }
        if(destination!=null){
            System.out.println(destination.toString());
            System.out.print(i);
        }
        System.out.println();
        return false;
    }

    //
    //ASTAR
    //
    public boolean isPlayableOnRow(Card moving_card){
        Card destination;
        for (int i= 0;i<15;i++){
            boolean yes = allowedMove(moving_card,i);
            if (yes){
                return true;
            }
        }
        return false;
    }
    public boolean isPlayableOnFoundation(Card moving_card){
        for (int i= 11;i<15;i++){
            boolean yes = allowedMove(moving_card,i);
            if (yes){
                return true;
            }
        }
        return false;
    }
    public boolean isAccessable(Card card){
        int j = findCard(card);
        int index = gameBoard[j].getIndex(card);
        if (index==0){
            return true;
        }
        if (j==10 ){
            if (cellarOpen()){
                return true;
            }
            else {
                return false;
            }
        }
        try {
            Card beside = gameBoard[j].getCard(index-1);
            if (isPlayableOnRow(beside)){
                return true;
            }
        } catch (Exception e){

        }

        return false;

    }
    public int rowScore(int i){
        int score = 0;
        int j = 0;
        while (j<gameBoard[i].length()){
            Card card = gameBoard[i].getCard(j);
            if (isAccessable(card)) {
                if (isPlayableOnFoundation(card)) {
                    score += 30 / (j + 1);
                } else if (isPlayableOnRow(card)) {
                    score += 10/(j+1);
                }
                j++;
            }
        }
        return score;
    }
    public int gameScore(){
        int score = 0;
        for (int i=0; i<11; i++){
            score += rowScore(i);
        }
        for (int i =11; i<15; i++){
            score += gameBoard[i].length()*30;
        }
        return score;
    }
    public int gameCost(Card a, int row){
        int score = gameScore();
        Deck[] temp = gameBoard;
        int a_index = findCard(a);
        gameBoard[a_index].remove(a);
        gameBoard[row].addAt(0,a);
        int new_score = gameScore();
        gameBoard = temp;
        return score - new_score;
    }
    public void playBestMove(int a, int b){
        Card moving = gameBoard[a].getCard(0);
        gameBoard[a].remove(moving);
        gameBoard[b].addAt(0,moving);
    }
    public int[] bestMove(){
        int highscore= gameScore();
        int prev;
        int [] move = {0,0};
        for (int i = 0; i<11; i++){
            if (gameBoard[i].isEmpty()){
                break;
            }
            Card moving = gameBoard[i].getCard(0);
            int j = 0;
            while(j<15){
                if (allowedMove(moving,j)) {
                    prev = highscore;
                    highscore = gameScore() - gameCost(moving, j);
                    if (highscore > prev) {
                        move[0] = i;
                        move[1] = j;

                    }
                }
                j++;
            }
        }
        return move;
    }
    public boolean gameEnd(){
        for (int i =0; i<11; i++){
            if (!gameBoard[i].isEmpty()){
                return false;
            }
        }
        return true;
    }
    public boolean noMoves(){
        for (int i =0; i<11; i++){
            if (!gameBoard[i].isEmpty()){
                Card card = gameBoard[i].getCard(0);
                if (isPlayableOnRow(card)){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean Solveable(){
        int i = 0;
        while(i<200){
            int [] x = bestMove();
            playBestMove(x[0],x[1]);
            i++;
            if(gameEnd()){
                return true;
            }
        }

        return false;
    }
    // to save the current step for undo purpose
    public void saveUndoStep(int index){
        if (undoIndex < 0){
            undoIndex = 0;
        }
        if (undoIndex > maxUndoStep-1) {
            for (int i = 0; i < maxUndoStep - 1; i++) {
                undoX[i] = undoX[i + 1];
                undoY[i] = undoY[i + 1];
                undoCard[i] = undoCard[i+1];
            }
            undoIndex = maxUndoStep - 1;
        }
        undoX[undoIndex] = allX[index];
        undoY[undoIndex] = allY[index];
        undoCard[undoIndex] = index;
        undoIndex++;
        Game.setSavingStep(false);
    }

    // undo action
    public void undo(){
        Deck deck = Game.getDeck();
        undoIndex--;
        if (undoIndex > maxUndoStep - 1){
            undoIndex = maxUndoStep - 1;
        }
        else if (undoIndex < 0){
            undoIndex = 0;
        }

        // 100 means there is no card to be undo
        if (undoCard[undoIndex] != 100) {
            undoStack(allX[undoCard[undoIndex]], allY[undoCard[undoIndex]]);

            //undo card position
            int initrow = rowFromPos(allX[undoCard[undoIndex]],allY[undoCard[undoIndex]]);
            System.out.println(initrow);
            allX[undoCard[undoIndex]] = undoX[undoIndex];
            allY[undoCard[undoIndex]] = undoY[undoIndex];
            Card c = deck.getCard(undoCard[undoIndex]);

            int j = rowFromPos(undoX[undoIndex],undoY[undoIndex]);
            if (initrow>10){
                movesToFoundation--;
            }
            System.out.println("X ="+ undoX[undoIndex]+ " Y= "+ undoY[undoIndex]);
            System.out.println(j);
            undoCard[undoIndex] = 100;
            updateGameBoard(c,j);
            undoX[undoIndex] = 0;
            undoY[undoIndex] = 0;

            Game.setInUndo(false);
        }
        else{
            //TODO: should display something on the screen that says cannot undo anymore steps
        }
    }
    public void saveUndoStack(){
        if (undoStackIndex < 0){
            undoStackIndex = 0;
        }
        if (undoStackIndex > maxUndoStep-1) {
            for (int i = 0; i < maxUndoStep - 1; i++) {
                undoPreviousStack[i] = undoPreviousStack[i+1];
                //undoNextStack[i] = undoNextStack[i+1];
            }
            undoStackIndex = maxUndoStep - 1;
        }
        undoPreviousStack[undoStackIndex] = previousIndex;
        //undoNextStack[undoStackIndex] = nextIndex;
        System.out.println("saved previous: " + undoPreviousStack[undoStackIndex] + " saved at " + undoStackIndex);
        undoStackIndex++;
        Game.setSavingStack(false);
    }

    public void undoStack(float x, float y){
        int undoNextIndex = 0;
        double dist = 0.0;
        double minDist = 10000;
        undoStackIndex--;
        if (undoStackIndex > maxUndoStep - 1){
            undoStackIndex = maxUndoStep - 1;
        }
        else if (undoStackIndex < 0){
            undoStackIndex = 0;
        }
        //System.out.println("undoing index " + undoStackIndex + " in the undoStackArray");
        // 100 means there is no card to be undo
        if (undoCard[undoStackIndex] != 100) {
        //undo card position
        for (int i = 0; i < 53; i++){
            dist = Math.sqrt(((x - snappingX[i]) * (x - snappingX[i]) + (y - snappingY[i]) * (y - snappingY[i])));
            if (dist < minDist){
                minDist = dist;
                undoNextIndex = i;
            }
        }
        System.out.println(undoPreviousStack[undoStackIndex]);

            if (undoPreviousStack[undoStackIndex]!=100){
                numCards[undoPreviousStack[undoStackIndex]]++;
                numCards[undoNextIndex]--;
            }

        //undoStacking[undoStackIndex][0] = 0;
        //undoStacking[undoStackIndex][1] = 0;
        }
        else{
        //TODO: should display something on the screen that says cannot undo anymore steps
        }
        previousIndex = 100;
    }
    public void calculateStacking(float x, float y) {
        if (previousIndex != 100) {
            float[] xy = {0, 0};
            double dist = 0.0;
            double minDist = 10000;

            for (int i = 0; i < 53; i++) {
                dist = Math.sqrt(((x - snappingX[i]) * (x - snappingX[i]) + (y - snappingY[i]) * (y - snappingY[i])));
                if (dist < minDist) {
                    minDist = dist;
                    nextIndex = i;
                }
            }
            // card is moved -> numCards at that index is --
            if (numCards[previousIndex] > 0) {
                numCards[previousIndex]--;
            }
            numCards[nextIndex]++;
        }
    }

    public void setRenderOrder(int cardIndex){
        int displayOrderIndex = getArrayIndex(displayOrder,cardIndex);
        if (displayOrderIndex >= 0 && displayOrderIndex < 52) {
            for (int i = displayOrderIndex; i < displayOrder.length - 1; i++) {
                displayOrder[i] = displayOrder[i + 1];
            }
            displayOrder[displayOrder.length - 1] = cardIndex;
            Game.setInDisplayOrder(false);
        }
    }

    public static void setHoldingCard(boolean holdingCard) {
        DrawingTheDeck.holdingCard = holdingCard;
    }
    public int getArrayIndex(int[] arr,int value) {

        int k=0;
        for(int i=0;i<arr.length;i++){

            if(arr[i]==value){
                k=i;
                break;
            }
        }
        return k;
    }
}

