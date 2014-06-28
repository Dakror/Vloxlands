package de.dakror.vloxlands;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

/**
 * @author Dakror
 */
public class Config
{
	public static final String version = "infdev 0.1";

	public static FileHandle dir;
	public static Preferences pref;
	public static String savegameName;

	public static boolean debug = false;

	public static void init()
	{
		if (Gdx.app.getType() == ApplicationType.Android) dir = Gdx.files.internal("de.dakror/Vloxlands/");
		else dir = Gdx.files.external(".dakror/Vloxlands/");

		dir.mkdirs();
		pref = Gdx.app.getPreferences("settings.xml");
		pref.putBoolean("fullscreen", false);
		pref.putInteger("fov", 67);
		pref.flush();
	}

	public static void savePrefs()
	{
		pref.putBoolean("fullscreen", Gdx.graphics.isFullscreen());

		pref.flush();
	}
}
