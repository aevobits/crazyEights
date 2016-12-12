package com.aevobits.games.crazyeights.desktop;

import com.aevobits.games.crazyeights.CrazyEightsGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// change configuration settings
		config.width = 480;
		config.height = 800;
		config.resizable = true;
		config.title = "Crazy Eights";

		CrazyEightsGame myProgram = new CrazyEightsGame();
		LwjglApplication launcher = new LwjglApplication( myProgram, config );
	}
}
