package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.Updater;

/**
 * @author Dakror
 */
public class DebugLayer extends Layer
{
	SpriteBatch spriteBatch;
	BitmapFont font;
	
	@Override
	public void show()
	{
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
	}
	
	@Override
	public void render(float delta)
	{
		spriteBatch.begin();
		
		font.draw(spriteBatch, "Vloxlands " + Config.version, 0, Gdx.graphics.getHeight());
		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond() + ", UPS: " + Updater.instance.ticksPerSecond, 0, Gdx.graphics.getHeight() - 20);
		if (GameLayer.world != null)
		{
			font.draw(spriteBatch, "C: " + GameLayer.world.visibleChunks + " / " + GameLayer.world.loadedChunks + " / " + GameLayer.world.chunks, 0, Gdx.graphics.getHeight() - 40);
			font.draw(spriteBatch, "E: " + GameLayer.world.visibleEntities + " / " + GameLayer.world.totalEntities, 0, Gdx.graphics.getHeight() - 60);
			font.draw(spriteBatch, "X: " + GameLayer.camera.position.x, 0, Gdx.graphics.getHeight() - 80);
			font.draw(spriteBatch, "Y: " + GameLayer.camera.position.y, 0, Gdx.graphics.getHeight() - 100);
			font.draw(spriteBatch, "Z: " + GameLayer.camera.position.z, 0, Gdx.graphics.getHeight() - 120);
			
			int minutes = (int) (-GameLayer.time * 12 * 60 + 12 * 60 + 6 * 60);
			
			font.draw(spriteBatch, "Time: " + String.format("%1$02d:%2$02d", (minutes / 60) % 24, minutes % 60), 0, Gdx.graphics.getHeight() - 140);
			font.draw(spriteBatch, "Speed: " + Config.getGameSpeed(), 0, Gdx.graphics.getHeight() - 160);
		}
		font.draw(spriteBatch, "Seed: " + GameLayer.seed, 0, Gdx.graphics.getHeight() - 180);
		
		spriteBatch.end();
	}
	
	@Override
	public void resize(int width, int height)
	{
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}
}
