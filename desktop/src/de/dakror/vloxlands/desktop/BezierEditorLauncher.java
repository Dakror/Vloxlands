package de.dakror.vloxlands.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.dakror.vloxlands.util.BezierEditor;

/**
 * @author Dakror
 */
public class BezierEditorLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Bezier Editor";
		config.width = 400;
		config.height = 400;
		config.resizable = false;
		new LwjglApplication(new BezierEditor(), config);
	}
}
