package com.aevobits.games.crazyeights.entity;

import com.aevobits.games.crazyeights.actor.BaseActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by vito on 28/11/16.
 */

public class CardBack extends BaseActor {

    public void createGraphicCard(float cardWidth, float cardHeight, float x, float y){
        Texture tex = new Texture(Gdx.files.internal("cardBack.png"));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.setTexture(tex);
        this.setWidth(cardWidth);
        this.setHeight(cardHeight);
        this.setOriginCenter();
        this.setRectangleBoundary();
        this.setPosition(x, y);
    }
}
