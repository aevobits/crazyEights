package com.aevobits.games.crazyeights.screen;

import com.aevobits.games.crazyeights.BaseGame;
import com.aevobits.games.crazyeights.CrazyEightsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by vito on 19/12/16.
 */

public class MenuScreen extends BaseScreen {

    public MenuScreen(BaseGame g){
        super(g);
    }

    @Override
    public void create() {

        TextButton startButton = new TextButton("Start Game", game.defaultSkin, "uiTextButtonStyle");
        startButton.pad(10f);
        startButton.addListener(new InputListener(){
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
                        return true;
                    }

                    public void touchUp (InputEvent event, float x, float y, int pointer, int button){
                        //game.playServices.showOrLoadInterstital();
                        //game.setScreen(new GameScreen(game));
                        ((CrazyEightsGame)game).gameScreen();
                    }
        });

        TextButton quickButton = new TextButton("Quick Game", game.defaultSkin, "uiTextButtonStyle");
        quickButton.pad(10f);
        quickButton.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button){
                game.playServices.quickStartGame(game);
            }
        });

        TextButton quitButton = new TextButton("Quit", game.defaultSkin, "uiTextButtonStyle");
        quitButton.pad(10f);
        quitButton.addListener(new InputListener(){
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
                        return true;
                    }

                    public void touchUp (InputEvent event, float x, float y, int pointer, int button){
                        Gdx.app.exit();
                    }
        });

        uiTable.add().expandX();
        uiTable.row();
        uiTable.add(startButton).center().pad(20f);
        uiTable.row();
        uiTable.add(quickButton).center().pad(20f);
        uiTable.row();
        uiTable.add(quitButton).center().pad(20f);
        //uiTable.setDebug(true);

    }

    @Override
    public void update(float dt) {

    }
}
