package com.joelbarsky.gns_game;
import java.util.Random;

/**
 * Created by kevin on 2017-10-11.
 * This is the deck object
 */

public class Deck extends Board{

    Random rand = new Random();

    //Public Methods
    public void populate (){
        for (Suit suit: Suit.values()){
            for (Rank rank: Rank.values()){
                Card card = new Card(rank, suit);
                this.add(card);
            }
        }
    }
    public void shuffle(){
        for(int i = cards.size() - 1; i > 0; i--){
            int position = rand.nextInt();
            Card randomCard = cards.get(position);
            Card lastCard = cards.get(i);
            cards.set(i, randomCard);
            cards.set(position, lastCard);
        }
    }
}
