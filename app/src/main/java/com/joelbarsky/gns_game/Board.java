package com.joelbarsky.gns_game;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by kevin on 2017-10-11.
 */

public class Board {

    //Attributes
    public ArrayList<Stack<Card>> cards;

    //Constructor
    public Board(){
        cards = new ArrayList<Stack<Card>>();
    }

    //Public Methods
    public ArrayList<Stack<Card>> getCards(){
        return this.cards;
    }

    public void clear(){
        cards.clear();
    }

    public void add(Card card){
        Stack<Card> stack = new Stack<Card>();
        stack.clear();
        stack.push(card);
        cards.add(stack);
    }

    public String printDeck(){
        String deck = "";
        for (int i = 0; i< 52; i++){
            Stack<Card> cardStack = cards.get(i);
            Card card = cardStack.peek();
            deck += card.toString() + "\n";
        }
        return deck;
    }

}