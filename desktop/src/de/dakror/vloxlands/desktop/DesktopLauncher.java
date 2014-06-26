package de.dakror.vloxlands.desktop;

import com.badlogic.gdx.Files.FileType;
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
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;
		config.preferencesDirectory = ".dakror/Vloxlands";
		config.addIcon("img/logo/logo128.png", FileType.Internal);
		config.addIcon("img/logo/logo32.png", FileType.Internal);
		config.addIcon("img/logo/logo16.png", FileType.Internal);
		new LwjglApplication(new Vloxlands(), config);
	}
}
