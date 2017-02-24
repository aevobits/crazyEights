package com.aevobits.games.crazyeights;

import com.aevobits.games.crazyeights.service.PlayServices;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

public abstract class BaseGame extends Game
{
    public static PlayServices playServices;
    // used to store resources common to multiple screens
    public Skin skin;
    public Skin defaultSkin;
    public ActionResolver actionResolver;
    public I18NBundle myBundle;
    public FreeTypeFontGenerator generator;
    public FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    public boolean multiplayer;

    public BaseGame(){
        defaultSkin = new Skin();
    }

    public BaseGame(ActionResolver actionResolver, PlayServices playServices, boolean multiplayer)
    {
        this.actionResolver = actionResolver;
        BaseGame.playServices = playServices;
        this.multiplayer = multiplayer;
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