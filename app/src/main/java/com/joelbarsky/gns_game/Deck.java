package com.joelbarsky.gns_game;
import java.util.ArrayList;
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
            Card randomCard = cards.get(position);
            Card lastCard = cards.get(i);
            cards.set(i, randomCard);
            cards.set(position, lastCard);

        }
    }
    public String printCardFromDeck(Deck deck, int pos){
        Card card;
        String print;
        ArrayList<Card> cards;
        cards = deck.getCards();
        card = cards.get(pos);
        print = card.toString();
        return print;
    }
}
