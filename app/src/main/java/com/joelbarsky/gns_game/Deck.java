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

    //returns position of card in deck
    public int deckindex(Card c){
        return cards.indexOf(c);
    }

    //setup deck foundation
    public void setupFoundation() {
        //Foundation of the board must be of the same rank;
        Card tempCard;

        //swap 1st card and foundation card
        Card foundationCard = cards.get(48);
        int foundationRank = foundationCard.getRank();
        tempCard = cards.get(48);
        cards.set(48, cards.get(0));
        cards.set(0, tempCard);
        int n = 48;

        //Search the deck for cards with the foundationRank
        for (int i = 0; i < 48; i++) {
            if (cards.get(i).getRank() == foundationRank) {
                tempCard = cards.get(i);
                cards.set(i, cards.get(n));
                cards.set(n, tempCard);
                n++;
            }

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
