package de.dakror.vloxlands.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.layer.GameLayer;

/**
 * @author Dakror
 */
public class Minimap extends Group
{
	public Minimap()
	{
		float aspect = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		
		setBounds(Gdx.graphics.getWidth() - 250, Gdx.graphics.getHeight() - 250, 250, 250);
		setOrigin(getX(), getY());
		
		float w1 = getWidth() / ((GameLayer.world.getWidth() + GameLayer.world.getDepth())) * 2;
		
		float width = w1;
		float height = width / aspect;
		
		float mapWidth = width / 2 * (GameLayer.world.getWidth() + GameLayer.world.getDepth());
		float mapHeight = height / 2 * (GameLayer.world.getWidth() + GameLayer.world.getDepth());
		
		for (Island island : GameLayer.world.getIslands())
		{
			if (island != null)
			{
				float x = width / 2 * (island.index.x + island.index.z);
				float y = height / 2 * (island.index.x - island.index.z);
				MinimapIsland mi = new MinimapIsland(island);
				mi.setBounds(x + (getWidth() - mapWidth) / 2, -y + height / 2 * (GameLayer.world.getWidth() - 1) + (getHeight() - mapHeight) / 2, width, height);
				addActor(mi);
			}
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		Drawable bg = Vloxlands.skin.getDrawable("default-rect");
		bg.draw(batch, Gdx.graphics.getWidth() - getWidth(), Gdx.graphics.getHeight() - getHeight(), getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}
}
