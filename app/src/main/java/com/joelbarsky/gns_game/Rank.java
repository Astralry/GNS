package com.joelbarsky.gns_game;

/**
 * Created by kevin on 2017-10-11.
 */

enum Rank {

    ACE(1, "Ace"), TWO(2, "2"), THREE(3,"3"),
    FOUR(4, "4"), FIVE(5, "5"),
    SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"),
    NINE(9, "9"), TEN(10, "10"),
    JACK(11, "Jack"), QUEEN(12, "Queen"),
    KING(13, "King");

    //Private Fields
    private final int rankValue;
    private final String rankString;

    //Constructor
    Rank(int rankValue, String rankString){
        this.rankValue = rankValue;
        this.rankString = rankString;
    }

    //Public Methods
    public int getRank(){
        return rankValue;
    }
    public String printRank(){
        return rankString;
    }

}
