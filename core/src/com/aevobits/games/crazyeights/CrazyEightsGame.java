package com.aevobits.games.crazyeights;

import com.aevobits.games.crazyeights.screen.GameMultiplayerScreen;
import com.aevobits.games.crazyeights.screen.GameScreen;
import com.aevobits.games.crazyeights.screen.MenuScreen;
import com.aevobits.games.crazyeights.service.PlayServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.I18NBundle;

public class CrazyEightsGame extends BaseGame {

	public MenuScreen ms;
	public GameMultiplayerScreen multiplayerGameScreen;
	public GameScreen gameScreen;
	public CrazyEightsGame(){}

	public CrazyEightsGame(ActionResolver actionResolver, PlayServices playServices, boolean multiplayer){
		super(actionResolver, playServices, multiplayer);
	}

	@Override
	public void create() {
		// initialize resources common to multiple screens
		skin = new Skin();//Gdx.files.internal("data/clean-crispy-ui.json"));

		FileHandle baseFileHandle = Gdx.files.internal("i18n/bundle");
		//Locale locale = new Locale(Locale.getL, "CA");
		myBundle = I18NBundle.createBundle(baseFileHandle);

		// Bitmap font
		BitmapFont comicFont = new BitmapFont(Gdx.files.internal("comic.fnt"), Gdx.files.internal("comic.png"), false);
		comicFont.getData().setScale(80f);
		comicFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		defaultSkin.add("comicFont", comicFont);

		// Label style
		Label.LabelStyle comicFontLabelStyle = new Label.LabelStyle(new BitmapFont(), Color.BLACK);
		defaultSkin.add("comicFontLabelStyle", comicFontLabelStyle);

		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/komikax.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 70;
		//parameter.shadowColor = Color.GRAY;
		//parameter.shadowOffsetX = 3;
		//parameter.shadowOffsetY = 3;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 2;
		BitmapFont komikaxFont70 = generator.generateFont(parameter);

		Label.LabelStyle komikax70 = new Label.LabelStyle(komikaxFont70, Color.RED);
		defaultSkin.add("komikax70", komikax70);

		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/exo.otf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 30;
		/*
		parameter.shadowColor = Color.GRAY;
		parameter.shadowOffsetX = 3;
		parameter.shadowOffsetY = 3;

		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 0.5f;
		*/
		BitmapFont exoFont35 = generator.generateFont(parameter);

		Label.LabelStyle exo35 = new Label.LabelStyle(exoFont35, Color.BLACK);
		defaultSkin.add("exo35", exo35);

		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/collegia.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 15;
		BitmapFont collegiaFont15 = generator.generateFont(parameter);

		Label.LabelStyle collegia15 = new Label.LabelStyle(collegiaFont15, Color.BLACK);
		defaultSkin.add("collegia15", collegia15);

		Texture	dialogWinnerHandTex =	new	Texture(Gdx.files.internal("dialogWinnerHand.png"));
		defaultSkin.add("backgroundDialogWinnerHand", new NinePatch(dialogWinnerHandTex, 30,30,30,30));
		Window.WindowStyle dialogWinnerHand = new Window.WindowStyle(exoFont35, Color.BLACK, defaultSkin.getDrawable("backgroundDialogWinnerHand"));
		defaultSkin.add("dialogWinnerHand", dialogWinnerHand);

		Texture	dialogWinnerGameTex =	new	Texture(Gdx.files.internal("dialogWinnerGame.png"));
		defaultSkin.add("backgroundDialogWinnerGame", new NinePatch(dialogWinnerGameTex, 30,30,30,30));
		Window.WindowStyle dialogWinnerGame = new Window.WindowStyle(exoFont35, Color.BLACK, defaultSkin.getDrawable("backgroundDialogWinnerGame"));
		defaultSkin.add("dialogWinnerGame", dialogWinnerGame);

		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 15;
		//parameter.borderColor = Color.BLACK;
		//parameter.borderWidth = 0.5f;
		BitmapFont exoFont15 = generator.generateFont(parameter);

		Label.LabelStyle exo15 = new Label.LabelStyle(exoFont15, Color.BLACK);
		defaultSkin.add("exo15", exo15);

		Texture face1 = new Texture(Gdx.files.internal("faces/1.png"));
		defaultSkin.add("face1", face1);

		Texture face2 = new Texture(Gdx.files.internal("faces/2.png"));
		defaultSkin.add("face2", face2);

		Texture face3 = new Texture(Gdx.files.internal("faces/3.png"));
		defaultSkin.add("face3", face3);

		TextButton.TextButtonStyle uiTextButtonStyle = new TextButton.TextButtonStyle();
		uiTextButtonStyle.font = exoFont15;
		Texture	buttonUpTex =	new	Texture(Gdx.files.internal("button.png"));
		defaultSkin.add("buttonUp", buttonUpTex);
		uiTextButtonStyle.up = defaultSkin.getDrawable("buttonUp");
		defaultSkin.add("uiTextButtonStyle", uiTextButtonStyle);

		/*
		// Button style
		TextButtonStyle	uiTextButtonStyle = new	TextButtonStyle();
		uiTextButtonStyle.font = uiFont;
		uiTextButtonStyle.fontColor	= Color.NAVY;
		Texture	upTex =	new	Texture(Gdx.files.internal("ninepatch-1.png"));
		skin.add("buttonUp", new NinePatch(upTex, 26,26,16,20));
		uiTextButtonStyle.up = skin.getDrawable("buttonUp");
		Texture	overTex	= new Texture(Gdx.files.internal("ninepatch-2.png"));
		skin.add("buttonOver", new NinePatch(overTex, 26,26,16,20));
		uiTextButtonStyle.over = skin.getDrawable("buttonOver");
		uiTextButtonStyle.overFontColor	= Color.BLUE;
		Texture	downTex	= new Texture(Gdx.files.internal("ninepatch-3.png"));
		skin.add("buttonDown", new NinePatch(downTex, 26,26,16,20));
		uiTextButtonStyle.down = skin.getDrawable("buttonDown");
		uiTextButtonStyle.downFontColor	= Color.BLUE;
		skin.add("uiTextButtonStyle", uiTextButtonStyle);

		// Slider style
		SliderStyle uiSliderStyle = new	SliderStyle();
		skin.add("sliderBack", new Texture(Gdx.files.internal("slider-after.png"))	);
		skin.add("sliderKnob", new Texture(Gdx.files.internal("slider-knob.png"))	);
		skin.add("sliderAfter",	new Texture(Gdx.files.internal("slider-after.png"))	);
		skin.add("sliderBefore", new Texture(Gdx.files.internal("slider-before.png"))	);
		uiSliderStyle.background = skin.getDrawable("sliderBack");
		uiSliderStyle.knob = skin.getDrawable("sliderKnob");
		uiSliderStyle.knobAfter	= skin.getDrawable("sliderAfter");
		uiSliderStyle.knobBefore = skin.getDrawable("sliderBefore");
		skin.add("uiSliderStyle", uiSliderStyle);

		skin.add("white", new Texture(Gdx.files.internal("white4px.png")));

		Window.WindowStyle ws =	new	Window.WindowStyle();
		ws.titleFont = new BitmapFont() ;
		ws.titleFontColor = Color.BLACK;
		skin.add("defaultWindowStyle", ws);
		*/
		this.ms = new MenuScreen(this);
		this.setScreen(ms);
	}

	public void multiplayerGameScreen(String messageReceived){
		this.multiplayerGameScreen = new GameMultiplayerScreen(this, messageReceived);
		this.setScreen(this.multiplayerGameScreen);
	}

	public void updateMessage(String messageReceived){
		this.multiplayerGameScreen.updateMessage(messageReceived);
	}

	public void gameScreen(){
		this.gameScreen = new GameScreen(this);
		this.setScreen(this.gameScreen);
	}

}
