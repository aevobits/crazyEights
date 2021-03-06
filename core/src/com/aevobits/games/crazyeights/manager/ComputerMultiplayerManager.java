package com.aevobits.games.crazyeights.manager;

import com.aevobits.games.crazyeights.entity.Card;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.List;

/**
 * Created by vito on 17/02/17.
 */

class ComputerMultiplayerManager {

    Card selectMultiplayerPlay(List<Card> hand, List<Card> discardPile, String messageReceived){

        String[] tokens = messageReceived.split("#");
        String cardToken = tokens[1];
        String[] card = cardToken.split(",");
        Card card2 = new Card(Card.Suit.values()[Integer.valueOf(card[0])], Card.Rank.values()[Integer.valueOf(card[1])]);
        for (Card cardHand:hand){
            if ((cardHand.rank == card2.rank) && (cardHand.suit == card2.suit)){
                discardPile.add(0, cardHand);
            }
        }
        hand.remove(card2);

        return card2;
    }

    Card drawMultiplayerPlay(List<Card> deck, int idxCard, String messageReceived){

        String[] tokens = messageReceived.split("#");
        String cardToken = tokens[1];
        String[] cards = cardToken.split(";");
        String[] card = (cards[idxCard - 1]).split(",");
        Card card2 = new Card(Card.Suit.values()[Integer.valueOf(card[0])], Card.Rank.values()[Integer.valueOf(card[1])]);
        deck.remove(card2);

        return card2;
    }

    void drawReshuffleCard(List<Card> deck, List<Card> discardPile, Group discadPileGroup, String messageReceived){

        String[] tokens = messageReceived.split("#");
        String cardsToken = tokens[1];
        String[] cards = cardsToken.split(";");

        for (String cardToken:cards){
            String[] card = cardToken.split(",");
            Card card2 = new Card(Card.Suit.values()[Integer.valueOf(card[0])], Card.Rank.values()[Integer.valueOf(card[1])]);
            deck.add(card2);
            discardPile.remove(1);
            discadPileGroup.getChildren().removeIndex(0);
        }
    }

    Card.Suit chooseSuit(String messageReceived){

        String[] tokens = messageReceived.split("#");
        String wildSuitOrdinal = tokens[tokens.length - 1];

        return Card.Suit.values()[Integer.valueOf(wildSuitOrdinal)];
    }
}
