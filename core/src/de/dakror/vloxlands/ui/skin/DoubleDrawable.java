package de.dakror.vloxlands.ui.skin;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * @author Dakror
 */
public class DoubleDrawable extends BaseDrawable
{
	public Drawable fg, bg;
	
	public DoubleDrawable()
	{}
	
	public DoubleDrawable(Drawable fg, Drawable bg)
	{
		this.fg = fg;
		this.bg = bg;
	}
	
	public DoubleDrawable(Drawable drawable)
	{
		super(drawable);
	}
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height)
	{
		if (fg != null) fg.draw(batch, x, y, width, height);
		if (bg != null) bg.draw(batch, x, y, width, height);
	}
}
