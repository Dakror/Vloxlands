package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.layer.GameLayer;

/**
 * @author Dakror
 */
public class MinimapIsland extends Actor
{
	Island island;
	boolean active;
	
	public MinimapIsland(Island island)
	{
		this.island = island;
		addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				boolean reallyHit = x >= getWidth() / 4 && x <= getWidth();
				if (reallyHit)
				{
					for (Actor a : getParent().getChildren())
					{
						if (a instanceof MinimapIsland) ((MinimapIsland) a).active = false;
					}
				}
				active = reallyHit;
				if (active)
				{
					GameLayer.instance.focusIsland(MinimapIsland.this.island, false);
				}
				return reallyHit;
			}
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		if (island.fbo != null)
		{
			if (active)
			{
				Drawable d = Vloxlands.skin.getDrawable("default-rect");
				d.draw(batch, getX() + getWidth() / 4, getY(), getWidth() / 2, getHeight());
			}
			batch.draw(island.fbo.getColorBufferTexture(), getX(), getY(), getWidth(), getHeight(), 0, 0, island.fbo.getColorBufferTexture().getWidth(), island.fbo.getColorBufferTexture().getHeight(), false, true);
		}
	}
}
