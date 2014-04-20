package de.dakror.vloxlands.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.dakror.vloxlands.Vloxlands;

public class DesktopLauncher
{
	public static void main(String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Vloxlands";
		config.width = 1280;
		config.height = 720;
		config.samples = 8;
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;
		new LwjglApplication(new Vloxlands(), config);
	}
}
