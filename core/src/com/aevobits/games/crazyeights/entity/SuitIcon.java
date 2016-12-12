package com.aevobits.games.crazyeights.entity;

import com.aevobits.games.crazyeights.actor.BaseActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by vito on 28/11/16.
 */

public class SuitIcon extends BaseActor {
    public void createGraphicIcon(Card.Suit selectedSuit, float x, float y){
        String fileName = selectedSuit.name().toLowerCase() + ".png";
        Texture tex = new Texture(Gdx.files.internal(fileName));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.setTexture(tex);
        this.setWidth(60);
        this.setHeight(50);
        this.setPosition(x, y);
    }
}
