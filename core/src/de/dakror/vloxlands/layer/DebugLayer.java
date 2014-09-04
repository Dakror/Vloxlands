package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.FloatArray;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.Updater;
import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.Game;

/**
 * @author Dakror
 */
public class DebugLayer extends Layer
{
	SpriteBatch spriteBatch;
	BitmapFont font;
	
	FloatArray renderTimes = new FloatArray();
	FloatArray tickTimes = new FloatArray();
	
	long lastTick;
	int max = 500;
	
	@Override
	public void show()
	{
		spriteBatch = new SpriteBatch();
		font = Vloxlands.skin.getFont("alagard_18pt");
	}
	
	@Override
	public void tick(int tick)
	{
		if (lastTick == 0) lastTick = System.nanoTime();
		long delta = System.nanoTime() - lastTick;
		if (delta > 0)
		{
			tickTimes.add(delta / 1000000000f);
			while (tickTimes.size > max)
				tickTimes.removeIndex(0);
			lastTick = System.nanoTime();
		}
	}
	
	@Override
	public void render(float delta)
	{
		renderTimes.add(delta);
		while (renderTimes.size > max)
			renderTimes.removeIndex(0);
		
		spriteBatch.begin();
		
		drawString("Vloxlands " + Config.version, 0, Gdx.graphics.getHeight());
		drawString("FPS: " + Gdx.graphics.getFramesPerSecond() + ", UPS: " + Updater.instance.ticksPerSecond, 0, Gdx.graphics.getHeight() - 14);
		if (Game.world != null)
		{
			drawString("C: " + Game.world.visibleChunks + " / " + Game.world.loadedChunks + " / " + Game.world.chunks, 0, Gdx.graphics.getHeight() - 28);
			drawString("E: " + Game.world.visibleEntities + " / " + Game.world.totalEntities, 0, Gdx.graphics.getHeight() - 14 * 3);
			drawString("X: " + Game.camera.position.x, 0, Gdx.graphics.getHeight() - 14 * 4);
			drawString("Y: " + Game.camera.position.y, 0, Gdx.graphics.getHeight() - 14 * 5);
			drawString("Z: " + Game.camera.position.z, 0, Gdx.graphics.getHeight() - 14 * 6);
			
			int minutes = (int) (-Game.time * 12 * 60 + 12 * 60 + 6 * 60);
			
			drawString("Time: " + String.format("%1$02d:%2$02d", (minutes / 60) % 24, minutes % 60), 0, Gdx.graphics.getHeight() - 14 * 7);
			drawString("Speed: " + Config.getGameSpeed(), 0, Gdx.graphics.getHeight() - 14 * 8);
		}
		drawString("Seed: " + Game.seed, 0, Gdx.graphics.getHeight() - 14 * 9);
		
		int full = 500;
		int fac = 25;
		drawString(fac + "ms", 0, full + 14);
		drawString(fac + "ms", max, full + 14);
		spriteBatch.end();
		
		Vloxlands.shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		Vloxlands.shapeRenderer.identity();
		Vloxlands.shapeRenderer.begin(ShapeType.Filled);
		Vloxlands.shapeRenderer.setColor(Color.BLACK);
		Vloxlands.shapeRenderer.rect(0, 0, 5, full);
		for (int i = 0; i < renderTimes.size; i++)
		{
			float rt = renderTimes.get(i) * fac;
			Color c = new Color(rt, 0, 0, 0.5f);
			Vloxlands.shapeRenderer.rect(5 + i, 0, 1, rt * full, Color.BLACK, Color.BLACK, c, c);
		}
		
		Vloxlands.shapeRenderer.rect(max, 0, 5, full);
		for (int i = 0; i < tickTimes.size; i++)
		{
			float rt = tickTimes.get(i) * fac;
			Color c = new Color(rt, 0, 0, 0.5f);
			Vloxlands.shapeRenderer.rect(5 + i + max, 0, 1, rt * full, Color.BLACK, Color.BLACK, c, c);
		}
		
		Vloxlands.shapeRenderer.end();
		
	}
	
	public void drawString(String s, int x, int y)
	{
		TextBounds tb = font.getBounds(s);
		Vloxlands.skin.getDrawable("shadow_mm").draw(spriteBatch, x, y - tb.height - 1, tb.width, tb.height);
		font.draw(spriteBatch, s, x, y);
	}
	
	@Override
	public void resize(int width, int height)
	{
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}
}
