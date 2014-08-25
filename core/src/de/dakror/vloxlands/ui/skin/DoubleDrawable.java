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
		if (bg != null) bg.draw(batch, x, y, width, height);
		if (fg != null) fg.draw(batch, x, y, width, height);
	}
	
	@Override
	public float getMinWidth()
	{
		if (fg instanceof TilesetDrawable) return fg.getMinWidth();
		if (bg instanceof TilesetDrawable) return bg.getMinWidth();
		return fg != null && bg != null ? Math.max(fg.getMinWidth(), bg.getMinWidth()) : fg != null ? fg.getMinWidth() : bg.getMinWidth();
	}
	
	@Override
	public float getMinHeight()
	{
		if (fg instanceof TilesetDrawable) return fg.getMinHeight();
		if (bg instanceof TilesetDrawable) return bg.getMinHeight();
		return fg != null && bg != null ? Math.max(fg.getMinHeight(), bg.getMinHeight()) : fg != null ? fg.getMinHeight() : bg.getMinHeight();
	}
}
