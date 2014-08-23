package de.dakror.vloxlands.ui.skin;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * @author Dakror
 */
public class TilesetDrawable extends BaseDrawable
{
	/**
	 * Indices:<br>
	 * 0-1-2<br>
	 * 3-4-5<br>
	 * 6-7-8
	 */
	Drawable[] drawables;
	public static final String[] values = { "lt", "mt", "rt", "lm", "mm", "rm", "lb", "mb", "rb" };
	public TextureRegion lt, mt, rt, lm, mm, rm, lb, mb, rb;
	
	public TilesetDrawable()
	{}
	
	public TilesetDrawable(Drawable... drawables)
	{
		assert (drawables.length == 9);
		this.drawables = drawables;
	}
	
	public TilesetDrawable(TextureRegion... regions)
	{
		assert (drawables.length == 9);
		drawables = new Drawable[9];
		drawables[0] = new TextureRegionDrawable(regions[0]);
		drawables[1] = new TiledDrawable(regions[1]);
		drawables[2] = new TextureRegionDrawable(regions[2]);
		drawables[3] = new TiledDrawable(regions[3]);
		if (regions[4] != null) drawables[4] = new TiledDrawable(regions[4]);
		drawables[5] = new TiledDrawable(regions[5]);
		drawables[6] = new TextureRegionDrawable(regions[6]);
		drawables[7] = new TiledDrawable(regions[7]);
		drawables[8] = new TextureRegionDrawable(regions[8]);
	}
	
	public TilesetDrawable(Drawable drawable)
	{
		super(drawable);
	}
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height)
	{
		if (drawables == null)
		{
			drawables = new Drawable[9];
			drawables[0] = new TextureRegionDrawable(lt);
			drawables[1] = new TiledDrawable(mt);
			drawables[2] = new TextureRegionDrawable(rt);
			drawables[3] = new TiledDrawable(lm);
			if (mm != null) drawables[4] = new TiledDrawable(mm);
			drawables[5] = new TiledDrawable(rm);
			drawables[6] = new TextureRegionDrawable(lb);
			drawables[7] = new TiledDrawable(mb);
			drawables[8] = new TextureRegionDrawable(rb);
		}
		
		float cw = drawables[0].getMinWidth();
		float ch = drawables[0].getMinHeight();
		float mw = width - cw * 2;
		float mh = height - ch * 2;
		
		float sh = drawables[1].getMinHeight();
		float sw = drawables[3].getMinWidth();
		
		drawables[0].draw(batch, x, y + height - ch, cw, ch);
		drawables[1].draw(batch, x + cw, y + height - sh, mw, sh);
		drawables[2].draw(batch, x + width - cw, y + height - ch, cw, ch);
		drawables[3].draw(batch, x, y + ch, sw, mh);
		if (drawables[4] != null) drawables[4].draw(batch, x + cw, y + ch, mw, mh);
		drawables[5].draw(batch, x + width - sw, y + ch, sw, mh);
		drawables[6].draw(batch, x, y, cw, ch);
		drawables[7].draw(batch, x + cw, y, mw, sh);
		drawables[8].draw(batch, x + width - cw, y, cw, ch);
	}
}
