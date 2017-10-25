package com.joelbarsky.gns_game;
import java.util.Random;
import java.util.Stack;

/**
 * Created by kevin on 2017-10-11.
 * This is the deck object
 */

public class Deck extends Board {
    Random rand = new Random();
    public void populate(){
        for (Suit suit: Suit.values()){
            for (Rank rank: Rank.values()){
                Card card = new Card(rank, suit);
                this.add(card);
            }
        }
    }
    public void shuffle(){
        for (int i = cards.size() - 1; i > 0; i--){
            int position= rand.nextInt(i);
            Stack<Card> randomCardStack = cards.get(position);
            Stack<Card> lastCardStack = cards.get(i);
            cards.set(i, randomCardStack);
            cards.set(position, lastCardStack);

        }
    }
}
