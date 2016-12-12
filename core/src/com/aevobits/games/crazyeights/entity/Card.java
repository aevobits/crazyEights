package com.aevobits.games.crazyeights.entity;

import com.aevobits.games.crazyeights.actor.BaseActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by vito on 15/11/16.
 */

public class Card extends BaseActor {

    public enum Suit {SPADES, HEARTS, CLUBS, DIAMONDS};
    public enum Rank {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING};
    //public enum Rank {ACE, TWO, FOUR, EIGHT, QUEEN};
    public final Suit suit;
    public final Rank rank;

    public Card(Suit suit, Rank rank){
        this.suit = suit;
        this.rank = rank;
        this.dragable = true;
    }

    public int getSuitNum(){
        return suit.ordinal();
    }
    public int getRankNum(){
        return rank.ordinal();
    }

    @Override
    public boolean equals(Object other){
        if(other == this) return true;

        if(other.getClass() == Card.class){
            Card otherCard = (Card)other;
            if(otherCard.getRankNum() == getRankNum() &&
                    otherCard.getSuitNum() == getSuitNum()){
                return true;
            }
            return false;
        }else{
            return false;
        }
    }

    public void createGraphicCard(float cardWidth, float cardHeight, float x, float y, String frontOrBack){
        String fileName = "cardBack.png";
        if (frontOrBack.equalsIgnoreCase("front")){
            fileName = this.rank.ordinal() + 1 + "_of_" + this.suit.name().toLowerCase() + ".png";
        }
        Texture tex = new Texture(Gdx.files.internal(fileName));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.setTexture(tex);
        this.setWidth(cardWidth);
        this.setHeight(cardHeight);
        this.setOriginCenter();
        this.setRectangleBoundary();
        this.setPosition(x, y);
    }

}
