package com.joelbarsky.gns_game;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by kevin on 2017-10-11.
 */

public class Board {

    //Attributes
    public ArrayList<Card> cards;

    //Constructor
    public Board(){
        cards = new ArrayList<Card>();
    }

    //Public Methods
    public ArrayList<Card> getCards(){
        return this.cards;
    }

    public void clear(){
        cards.clear();
    }

    public void addAt(int i, Card card){cards.add(i,card);
    }
    public void add(Card card){
        cards.add(card);
    }

    public void remove(Card card){
        cards.remove(card);
    }

    public Card getCard(int i){ return cards.get(i);
    }
    public boolean isEmpty(){ return cards.isEmpty();}

    public boolean contains(Card c){ return cards.contains(c);
    }

    public String printDeck(){
        String deck = "";
        for (int i = 0; i< 52; i++){
            Card card = cards.get(i);
            deck += card.toString() + "\n";
        }
        return deck;
    }

}