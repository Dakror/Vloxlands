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
	public static final String[] dataMaps = { "All", "Ores" };
	public static final boolean[] dataMapFullBlending = { false, true };
	
	public static final String version = "infdev 0.1";
	
	public static int fov;
	public static boolean paused;
	public static int shadowQuality = 1;
	private static int gameSpeed = 0;
	public static int[] gameSpeeds = { 1, 2, 5 };
	public static FileHandle dir;
	static Preferences pref;
	public static String savegameName;
	
	public static boolean debug = false;
	
	public static void changeGameSpeed(boolean increase)
	{
		if (increase && gameSpeed < gameSpeeds.length - 1) gameSpeed++;
		else if (!increase && gameSpeed > 0) gameSpeed--;
		else return;
		
		Gdx.app.log("Config.changeGameSpeed", "Setting Game speed to " + getGameSpeed());
	}
	
	public static int getGameSpeed()
	{
		return gameSpeeds[gameSpeed];
	}
	
	public static void init()
	{
		if (Gdx.app.getType() == ApplicationType.Android) dir = Gdx.files.local("de.dakror/Vloxlands/");
		else dir = Gdx.files.external(".dakror/Vloxlands/");
		
		dir.mkdirs();
		pref = Gdx.app.getPreferences("settings.xml");
		if (!pref.contains("fullscreen")) pref.putBoolean("fullscreen", true);
		if (!pref.contains("fov")) pref.putInteger("fov", fov = 67);
		else fov = pref.getInteger("fov");
		if (!pref.contains("shadowQuality")) pref.putInteger("shadowQuality", shadowQuality);
		else shadowQuality = pref.getInteger("shadowQuality");
		pref.flush();
	}
	
	public static void savePrefs()
	{
		pref.putBoolean("fullscreen", Gdx.graphics.isFullscreen());
		pref.putInteger("fov", fov);
		pref.putInteger("shadowQuality", shadowQuality);
		pref.flush();
	}
}
