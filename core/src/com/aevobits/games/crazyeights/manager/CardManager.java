package com.aevobits.games.crazyeights.manager;

import com.aevobits.games.crazyeights.entity.Card;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by vito on 15/11/16.
 */

class CardManager {

    private static List<Card> getSortedDeck(){
        List<Card> ret = new LinkedList<Card>();
        for(Card.Suit suit : Card.Suit.values()){
            for(Card.Rank rank : Card.Rank.values()){
                ret.add(new Card(suit, rank));
            }
        }
        return ret;
    }

    static List<Card> getShuffledDeck(){
        List<Card> ret = getSortedDeck();
        Collections.shuffle(ret, new Random());
        return ret;
    }

    static void draw(List<Card> from, List<Card> to){
        draw(from, to, 1);
    }
    static boolean draw(List<Card> from, List<Card> to, int count){
        if(from.size() < count){
            return false;
        }
        for(int i = 0; i < count; i++){
            to.add(0, from.remove(0));
        }
        return true;
    }

    /**
     * Deal perHand cards to all hands, from deck.
     * @param deck
     * @param perHand
     * @param hands
     * @return true on success, false on failue (ex: if deck is empty)
     */
    public static boolean deal(List<Card> deck, int perHand, List<Card>...hands){
        if(deck.size() < hands.length * perHand){
            return false;
        }
        for(int i = 0; i < perHand; i++){
            for(List<Card> curHand: hands){
                draw(deck, curHand);
            }
        }
        return true;
    }

    public static void dealDebug(List<Card> deck, List<Card> oppHand, List<Card> playerHand){

        oppHand.add(new Card(Card.Suit.CLUBS, Card.Rank.FOUR));
        oppHand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT));
        oppHand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN));

        playerHand.add(new Card(Card.Suit.HEARTS, Card.Rank.FOUR));
        playerHand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT));
        playerHand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));

        int lenght = deck.size();
        for (int i = 0; i < lenght;){
            boolean found = false;
            for (Card cardOppHand:oppHand){
                if (cardOppHand.rank == deck.get(i).rank &&
                        cardOppHand.suit == deck.get(i).suit){
                    deck.remove(i);
                    found = true;
                    lenght--;
                }
            }
            if (!found) {
                for (Card cardPlayerHand : playerHand) {
                    if (cardPlayerHand.rank == deck.get(i).rank &&
                            cardPlayerHand.suit == deck.get(i).suit){
                        deck.remove(i);
                        found = true;
                        lenght--;
                    }
                }
            }
            if (!found){
                i++;
            }
        }

    }

    /**
     * Takes all the card left in the hands, puts them into the deck,
     *   and reshuffles the deck
     * @param deck - the deck to reshuffle
     * @param hands - the hands to put back into the deck
     */
    public static void reshuffle(List<Card> deck, List<Card>...hands){
        for(List<Card> curHand : hands){
            deck.addAll(curHand);
            curHand.clear();
        }
        reshuffle(deck);
    }
    /**
     * Reshuffles the passed in deck
     * @param deck
     */
    public static void reshuffle(List<Card> deck){
        Collections.shuffle(deck, new Random());
    }
}
