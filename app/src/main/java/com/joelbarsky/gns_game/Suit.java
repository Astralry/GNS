package com.joelbarsky.gns_game;

/**
 * Created by kevin on 2017-10-11.
 */

enum Suit {
    DIAMONDS("diamonds"),
    CLUBS("Clubs"),
    HEARTS("Hearts"),
    SPADES("Spades");

    private final String suitText;

    //constructor
    Suit(String suitText){
        this.suitText = suitText;
    }

    //Public Method
    public String printSuit(){
        return suitText;
    }
}
