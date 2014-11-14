package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public class MinimapIsland extends Actor {
	Island island;
	boolean active;
	
	public MinimapIsland(Island island) {
		this.island = island;
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (touchable && getTouchable() != Touchable.enabled) return null;
		return x >= getWidth() / 4 && x < getWidth() / 4 * 3 && y >= 0 && y < getHeight() ? this : null;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (island.fbo != null) {
			if (active) {
				Drawable d = Vloxlands.skin.getDrawable("outline");
				d.draw(batch, (int) (getX() + getWidth() / 4), (int) getY(), (int) getWidth() / 2, (int) getHeight());
			}
			batch.draw(island.fbo.getColorBufferTexture(), getX(), getY(), getWidth(), getHeight(), 0, 0, island.fbo.getColorBufferTexture().getWidth(), island.fbo.getColorBufferTexture().getHeight(), false, true);
		}
	}
}
