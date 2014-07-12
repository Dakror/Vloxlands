package de.dakror.vloxlands.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.dakror.vloxlands.Vloxlands;

/**
 * @author Dakror
 */
public class DesktopLauncher
{
	public static void main(String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Vloxlands";
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;
		config.preferencesDirectory = ".dakror/Vloxlands";
		config.addIcon("img/logo/logo128.png", FileType.Internal);
		config.addIcon("img/logo/logo32.png", FileType.Internal);
		config.addIcon("img/logo/logo16.png", FileType.Internal);
		config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
		config.fullscreen = false;

		new LwjglApplication(new Vloxlands(), config);
	}
}
