package com.aevobits.games.crazyeights.entity;

import com.aevobits.games.crazyeights.actor.AnimatedActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by vito on 05/12/16.
 */

public class CardBackAnimated extends AnimatedActor {

    public void createGraphicCard(float cardWidth, float cardHeight, float x, float y){
        Texture tex = new Texture(Gdx.files.internal("cardBack-frame1.png"));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.setTexture(tex);
        this.setWidth(cardWidth);
        this.setHeight(cardHeight);
        this.setOriginCenter();
        this.setRectangleBoundary();
        this.setPosition(x, y);
    }

    public void animateCard(){
        TextureRegion[] frames = new TextureRegion[3];
        for (int n = 1; n <= 3; n++)
        {
            String fileName = "cardBack-frame" + n + ".png";
            Texture tex = new Texture(Gdx.files.internal(fileName));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames[n-1] = new TextureRegion( tex );
        }

        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);

        Animation anim = new Animation(1f, framesArray, Animation.PlayMode.NORMAL);
        this.storeAnimation( "flip", anim );

        //Texture frame1 = new Texture(Gdx.files.internal("cardBack-frame1.png"));
        //this.storeAnimation( "stopped", frame1 );
    }
}
