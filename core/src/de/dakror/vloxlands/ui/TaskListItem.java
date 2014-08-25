package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public class TaskListItem extends Label
{
	public Structure structure;
	
	public TaskListItem(CharSequence text, Skin skin)
	{
		super(text, skin);
	}
	
	public TaskListItem(CharSequence text, LabelStyle style)
	{
		super(text, style);
	}
	
	public TaskListItem(CharSequence text, Skin skin, String styleName)
	{
		super(text, skin, styleName);
	}
	
	public TaskListItem(CharSequence text, Skin skin, String fontName, Color color)
	{
		super(text, skin, fontName, color);
	}
	
	public TaskListItem(CharSequence text, Skin skin, String fontName, String colorName)
	{
		super(text, skin, fontName, colorName);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		try
		{
			if ((Integer) getUserObject() == 0)
			{
				float fac = (1 - (structure.getTaskTicksLeft() / (float) structure.firstTask().getDuration()));
				TextureRegion t = Vloxlands.skin.getRegion("progressBar");
				batch.draw(t.getTexture(), getX(), getY(), Math.round(getParent().getWidth() * fac), (int) getHeight(), t.getRegionX(), t.getRegionY(), Math.round(t.getRegionWidth() * fac), t.getRegionHeight(), false, false);
			}
		}
		catch (NullPointerException e)
		{
			return;
		}
		
		super.draw(batch, parentAlpha);
	}
}
