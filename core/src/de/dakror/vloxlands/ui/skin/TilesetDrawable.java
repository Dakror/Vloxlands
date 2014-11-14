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
public class TilesetDrawable extends BaseDrawable {
	/**
	 * Indices:<br>
	 * 0-1-2<br>
	 * 3-4-5<br>
	 * 6-7-8
	 */
	Drawable[] drawables;
	public static final String[] values = { "lt", "mt", "rt", "lm", "mm", "rm", "lb", "mb", "rb" };
	
	public TextureRegion lt, mt, rt, lm, mm, rm, lb, mb, rb;
	public boolean center = false;
	
	public TilesetDrawable() {}
	
	public TilesetDrawable(Drawable... drawables) {
		assert (drawables.length == 9);
		this.drawables = drawables;
	}
	
	public TilesetDrawable(TextureRegion... regions) {
		assert (drawables.length == 9);
		drawables = new Drawable[9];
		drawables[0] = new TextureRegionDrawable(regions[0]);
		if (regions[1] != null) drawables[1] = new TiledDrawable(regions[1]);
		if (regions[2] != null) drawables[2] = new TextureRegionDrawable(regions[2]);
		if (regions[3] != null) drawables[3] = new TiledDrawable(regions[3]);
		if (regions[4] != null) drawables[4] = new TiledDrawable(regions[4]);
		if (regions[5] != null) drawables[5] = new TiledDrawable(regions[5]);
		if (regions[6] != null) drawables[6] = new TextureRegionDrawable(regions[6]);
		if (regions[7] != null) drawables[7] = new TiledDrawable(regions[7]);
		if (regions[8] != null) drawables[8] = new TextureRegionDrawable(regions[8]);
	}
	
	public TilesetDrawable(Drawable drawable) {
		super(drawable);
	}
	
	@Override
	public void draw(Batch batch, float x1, float y1, float w1, float h1) {
		int x = (int) x1;
		int y = (int) y1;
		int width = (int) w1;
		int height = (int) h1;
		
		if (drawables == null) {
			drawables = new Drawable[9];
			drawables[0] = new TextureRegionDrawable(lt);
			if (mt != null) drawables[1] = new TiledDrawable(mt);
			if (rt != null) drawables[2] = new TextureRegionDrawable(rt);
			if (lm != null) drawables[3] = new TiledDrawable(lm);
			if (mm != null) drawables[4] = new TiledDrawable(mm);
			if (rm != null) drawables[5] = new TiledDrawable(rm);
			if (lb != null) drawables[6] = new TextureRegionDrawable(lb);
			if (mb != null) drawables[7] = new TiledDrawable(mb);
			if (rb != null) drawables[8] = new TextureRegionDrawable(rb);
		}
		
		int cw = (int) drawables[0].getMinWidth();
		int ch = (int) drawables[0].getMinHeight();
		
		if (width < cw * 2 && width > 0) cw = (int) (width / 2f);
		if (height < ch * 2 && height > 0) ch = (int) (height / 2f);
		
		int mw = width - cw * 2;
		int mh = height - ch * 2;
		
		int sh = (int) (drawables[1] != null ? drawables[1].getMinHeight() : 0);
		int sw = (int) (drawables[3] != null ? drawables[3].getMinWidth() : 0);
		
		int deltaW = Math.max(cw, sw) - Math.min(cw, sw);
		int deltaH = Math.max(ch, sh) - Math.min(ch, sh);
		
		drawables[0].draw(batch, x, y + height - ch, cw, ch);
		if (drawables[1] != null) drawables[1].draw(batch, x + cw, y + height - sh + (center ? deltaH / 2 : 0), mw, sh);
		if (drawables[2] != null) drawables[2].draw(batch, x + width - cw, y + height - ch, cw, ch);
		if (drawables[3] != null) drawables[3].draw(batch, x + (center ? deltaW / 2 : 0), y + ch, sw, mh);
		if (drawables[4] != null) drawables[4].draw(batch, x + cw, y + ch, mw, mh);
		if (drawables[5] != null) drawables[5].draw(batch, x + width - sw - (center ? deltaW / 2 : 0), y + ch, sw, mh);
		if (drawables[6] != null) drawables[6].draw(batch, x, y, cw, ch);
		if (drawables[7] != null) drawables[7].draw(batch, x + cw, y - (center ? deltaH / 2 : 0), mw, sh);
		if (drawables[8] != null) drawables[8].draw(batch, x + width - cw, y, cw, ch);
	}
	
	@Override
	public float getMinWidth() {
		return drawables[0].getMinWidth() * 2;
	}
	
	@Override
	public float getMinHeight() {
		return drawables[0].getMinHeight() * 2;
	}
}
