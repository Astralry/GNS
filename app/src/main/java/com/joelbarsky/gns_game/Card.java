package com.joelbarsky.gns_game;

/**
 * Created by kevin on 2017-10-11.
 */

public class Card {

    //Private Fields
    private Suit suit;
    private Rank rank;

    //Constructor Method
    public Card(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }

    //Public Methods
    public String getSuit() {
        return suit.printSuit();
    }
    public int getRank(){
        return rank.getRank();
    }
    public String toString(){
        String str = " ";
        str += rank.printRank() + " of " + suit.printSuit();
        return str;
    }
}
