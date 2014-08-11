package de.dakror.vloxlands.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.world.Island;

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
		
		float w1 = getWidth() / ((Game.world.getWidth() + Game.world.getDepth())) * 2;
		
		float width = w1;
		float height = width / aspect;
		
		float mapWidth = width / 2 * (Game.world.getWidth() + Game.world.getDepth());
		float mapHeight = height / 2 * (Game.world.getWidth() + Game.world.getDepth());
		
		for (Island island : Game.world.getIslands())
		{
			if (island != null)
			{
				int ix = (int) (Game.world.getWidth() - island.index.x);
				
				float x = width / 2 * (ix + island.index.z);
				float y = height / 2 * (ix - island.index.z);
				MinimapIsland mi = new MinimapIsland(island);
				mi.setBounds(x + (getWidth() - mapWidth) / 2 - width / 2, -y + height / 2 * Game.world.getWidth() + (getHeight() - mapHeight) / 2, width, height);
				addActor(mi);
			}
		}
		
		addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				if (button != Buttons.LEFT) return false;
				
				for (Actor a : getChildren())
					if (a instanceof MinimapIsland) ((MinimapIsland) a).active = false;
				
				Actor actor = hit(x, y, true);
				if (actor != null && actor instanceof MinimapIsland)
				{
					if (Game.instance.cursorStructure == null && !Game.instance.regionSelectionMode)
					{
						((MinimapIsland) actor).active = true;
						Game.instance.focusIsland(((MinimapIsland) actor).island, false);
					}
				}
				return false;
			}
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		setPosition(Gdx.graphics.getWidth() - getWidth(), Gdx.graphics.getHeight() - getHeight());
		
		Drawable bg = Vloxlands.skin.getDrawable("default-rect");
		bg.draw(batch, getX(), getY(), getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}
}
