package com.aevobits.games.crazyeights;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

public abstract class BaseGame extends Game
{
    // used to store resources common to multiple screens
    public Skin skin;
    public Skin defaultSkin;
    public ActionResolver actionResolver;
    public I18NBundle myBundle;
    public FreeTypeFontGenerator generator;
    public FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public BaseGame(){
        defaultSkin = new Skin();
    }

    public BaseGame(ActionResolver actionResolver)
    {
        this.actionResolver = actionResolver;
        defaultSkin = new Skin();
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    }
    
    public abstract void create();

    public void dispose()
    {
        skin.dispose();
        defaultSkin.dispose();
        generator.dispose();
    }
}