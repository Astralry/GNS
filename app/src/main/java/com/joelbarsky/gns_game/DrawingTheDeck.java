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
    static boolean buildUp = true;

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
        Deck deck = Game.getDeck();
        Card a = null;
        Card b;


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
        if(index != 100){
            a = deck.getCard(index);

        }
        //if a card is touched, update its position
        if (index != 100 && inContact&&isMoveable()){
            allX[index] = x;
            allY[index] = y;
        } else if (index != 100&&isMoveable()){
            allX[index] = snap(x, y)[0];
            allY[index] = snap(x, y)[1];
            b = returnCardAt(a,x,y);
            System.out.println("A is " + a.toString()+ "B is" + b.toString());
            int j = findCard(b);
            //int j = rowFromPos(x,y);
            System.out.println("J = " + j);
            System.out.println(allowedMove(a,j));
            if (allowedMove(a,j)) {
                updateGameBoard(a,j);

            }
            //todo else (if not playable) undo movement
        }
    }
    public void setupGameBoard(){
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
        double dist = 0.0;
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
    public Card returnCard(){
        deck = Game.getDeck();
        if (index == 100){
            return null;
        }
        Card card = deck.getCard(index);
        return card;
    }
    public Card returnCardAt(Card a, float x, float y){
        Deck deck = Game.getDeck();
        int index = 100;
        Card card;
        if (!Game.isInContact()) {
            for (int i = 0; i < 52; i++) {
                if (x > allX[i] - (81 / 2) && x < allX[i] + (81 / 2) && y > allY[i] - (117 / 2) && y < allY[i] + (117 / 2)) {
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
    public void updateGameBoard(Card card, int i){
        int p = findCard(card);
        gameBoard[p].remove(card);
        gameBoard[i].addAt(0, card);
    }
    public int rowFromPos(float x, float y){
        int index = 100;
        if (x>11*colSpacing/2 && x<13*colSpacing/2){
            if(y>rowSpacing*9/2 && y<rowSpacing*13/2){
                index = 10;
            }
            else if(y>rowSpacing && y<rowSpacing*2){
                index = 11;
            }
            else if(y>rowSpacing*3/2 && y<rowSpacing*5/2){
                index = 12;
            }
            else if(y>rowSpacing*5/2 && y<rowSpacing*7/2){
                index = 13;
            }
            else if(y>rowSpacing*7/2 && y<rowSpacing*9/2){
                index = 14;
            }

        }
        else if (x<(11*colSpacing/2) && x > colSpacing/2){
            if (y>rowSpacing && y<(5/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index = 0;
            }
            else if(y> (5/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(10/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =1;
            }
            else if(y> (10/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(15/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =2;
            }
            else if(y> (15/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(20/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =3;
            }
            else if(y> (20/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(25/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =4;
            }

        }
        else if (x>(6*colSpacing + colSpacing/2) && x< 12*colSpacing){
            if (y>rowSpacing && y<(5/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index = 5;
            }
            else if(y> (5/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(10/ (rowWidth / 2)) * rowSpacing + rowSpacing) {
                index = 6;
            }
            else if(y> (10/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(15/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =7;
            }
            else if(y> (15/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(20/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =8;
            }
            else if(y> (20/ (rowWidth / 2)) * rowSpacing + 2* rowSpacing && y<(25/ (rowWidth / 2)) * rowSpacing + rowSpacing){
                index =9;
            }
        }

        return index;
    }
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
    public boolean allowedMove(Card a, int i){
        if(a==null){
            return false;
        }
        Card destination;
        if (gameBoard[i].isEmpty()){
            destination = null;
        }
        else{
            destination = gameBoard[i].getCard(0);
        }
        if ((destination == null) && !(i == 5 || i ==9)){
            return true;
        }
        else if (destination!=null){
            System.out.println("in else if statement");
            int diff = a.getRank() - destination.getRank();
            if (a.getSuit() == destination.getSuit() && i != 5 && i != 9) {
                if (i > 10) {
                    if (buildUp && diff == 1) {
                        return true;
                    } else if (diff == -1) {
                        return true;
                    }
                }
                else if (i == 10 && gameBoard[10].isEmpty()) {
                    return true;
                }
                else if (java.lang.Math.abs(diff) ==1){
                    return true;
                }
                System.out.println("Still in else if statement");
            }
        }

        return false;
    }

    //
    //ASTAR
    //
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

}

