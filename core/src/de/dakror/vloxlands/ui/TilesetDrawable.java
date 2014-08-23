package de.dakror.vloxlands.ui;

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
	public TextureRegion tl, tm, tr, ml, mm, mr, bl, bm, br;
	
	public TilesetDrawable()
	{}
	
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
			drawables[0] = new TextureRegionDrawable(tl);
			drawables[1] = new TiledDrawable(tm);
			drawables[2] = new TextureRegionDrawable(tr);
			drawables[3] = new TiledDrawable(ml);
			drawables[4] = new TiledDrawable(mm);
			drawables[5] = new TiledDrawable(mr);
			drawables[6] = new TextureRegionDrawable(bl);
			drawables[7] = new TiledDrawable(bm);
			drawables[8] = new TextureRegionDrawable(br);
		}
		
		float cw = drawables[0].getMinWidth();
		float ch = drawables[0].getMinHeight();
		float mw = width - cw * 2;
		float mh = width - ch * 2;
		
		drawables[0].draw(batch, x, y, cw, ch);
		drawables[1].draw(batch, x + cw, y, mw, ch);
		drawables[2].draw(batch, x + width - cw, y, cw, ch);
		drawables[3].draw(batch, x, y + ch, cw, mh);
		drawables[4].draw(batch, x + cw, y + ch, mw, mh);
		drawables[5].draw(batch, x + width - cw, y + ch, cw, mh);
		drawables[6].draw(batch, x, y + height - ch, cw, ch);
		drawables[7].draw(batch, x + cw, y + height - ch, mw, ch);
		drawables[8].draw(batch, x + width - cw, y + height - ch, cw, ch);
	}
}
