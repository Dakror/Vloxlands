package de.dakror.vloxlands.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;

/**
 * @author Dakror
 */
public class BezierEditor implements ApplicationListener
{
	SpriteBatch spriteBatch;
	ImmediateModeRenderer20 renderer;

	@Override
	public void create()
	{}

	@Override
	public void resize(int width, int height)
	{}

	@Override
	public void render()
	{}

	@Override
	public void pause()
	{}

	@Override
	public void resume()
	{}

	@Override
	public void dispose()
	{}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Bezier Editor";
		config.width = 400;
		config.height = 400;
		new LwjglApplication(new BezierEditor(), config);
	}
}
