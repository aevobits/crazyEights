package com.aevobits.games.crazyeights.manager;

import com.aevobits.games.crazyeights.entity.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by vito on 17/11/16.
 */

public class ComputerPlayerManager {

    GameManager gameManager;

    public ComputerPlayerManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Select a play to make
     * @param hand
     * @return the card to play, or null if no valid play
     */
    public Card selectPlay(List<Card> hand){
        List<Card> validMoves = new ArrayList<Card>();
        for(Card card: hand){
            if(gameManager.isValidMove(card)){
                validMoves.add(card);
            }
        }
        if(!validMoves.isEmpty()){
            Collections.sort(validMoves, new Comparator<Card>() {
                @Override
                public int compare(Card lhs, Card rhs) {
                    //Play low value cards and eights last
                    if(lhs.rank == rhs.rank){
                        return 0;
                    }
                    if(lhs.getRankNum() > rhs.getRankNum() || rhs.rank == Card.Rank.EIGHT ||
                            rhs.rank == Card.Rank.TWO || rhs.rank == Card.Rank.FOUR || rhs.rank == Card.Rank.QUEEN){
                        return -1;
                    }
                    if(lhs.getRankNum() < rhs.getRankNum() || lhs.rank == Card.Rank.EIGHT ||
                            rhs.rank == Card.Rank.TWO || rhs.rank == Card.Rank.FOUR || rhs.rank == Card.Rank.QUEEN){
                        return 1;
                    }
                    return 0;
                }
            });
            return validMoves.get(0);
        }else{
            return null;
        }
    }

    public Card.Suit chooseSuit(List<Card> hand){
        int numHearts, numSpades, numClubs, numDiamonds;
        numHearts = numSpades = numClubs = numDiamonds = 0;
        for(Card card : hand){
            switch(card.suit){
                case HEARTS:
                    numHearts++;
                    break;
                case SPADES:
                    numSpades++;
                    break;
                case CLUBS:
                    numClubs++;
                    break;
                case DIAMONDS:
                    numDiamonds++;
                    break;
            }
        }
        int maxCount = Math.max(Math.max(numClubs, numDiamonds), Math.max(numHearts, numSpades));
        if(numClubs == maxCount){ return Card.Suit.CLUBS; }
        if(numHearts == maxCount){ return Card.Suit.HEARTS; }
        if(numDiamonds == maxCount){ return Card.Suit.DIAMONDS; }
        if(numSpades == maxCount){ return Card.Suit.SPADES; }
        //Fallback, shouldn't really get here
        return Card.Suit.values()[new Random().nextInt(4)];
    }
}
